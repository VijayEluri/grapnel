package durbin.weka;

import durbin.weka.*
import durbin.util.*
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.*

class WekaClassifierInfo{

	/**
	* Returns a map from features to weights for the features used by this classifier. 
	* Some classifiers use all the features, some use only a subset.  Some have obvious 
	* weights, some less so. 
	*/ 
	static def getFeatures(classifier){
		
		switch(classifier){
			case {it instanceof durbin.weka.BalancedRandomForest}:
			case {it instanceof weka.classifiers.trees.RandomForest}:
			def features2weights = getRFFeatures(classifier)
			return(features2weights)
			break;
			
			case {it instanceof weka.classifiers.functions.SimpleLogistic}:
			def features2weights = getLogisticFeatures(classifier)
			return(features2weights)
			break;
			
			case {it instanceof weka.classifiers.functions.SMO}:
			if (classifier.m_KernelIsLinear){
				def features2weights = getSMOFeatures(classifier);
				return(features2weights);
			}else{
				System.err.println "ERROR: Unsupported non-linear kernel for WekaClassifierInfo"
			}
			break;

			default:
			System.err.println "ERROR: Unsupported classifier type in WekaClassifierInfo"
			break;
		}
		def features2weights = ["NA":0]
		return(features2weights)
	}
			
	/**
	* Goes through each tree of the random forest and collects all of the features
	* in that tree. 
	*/ 	
	static def getRFFeatures(classifier){
		def featureSet = [] as Set

		def trees = classifier.m_bagger.m_Classifiers
		trees.each{tree->
			dfsCollectFeatures(tree,featureSet)
		}
		def features2weights = [:]
		featureSet.each{f->
			features2weights[f] = 1.0;  // Default weight for now. 
		}
		return(features2weights);
	}
	
	/***
	* Performs depth first search through tree to collect feature names. 
	*/ 
	static def dfsCollectFeatures(tree,features){
		if (tree.m_Attribute == -1) return; // end of graph
	
		tree.m_Successors.eachWithIndex{child,i->
			def featureName = tree.m_Info.attribute(tree.m_Attribute).name()
			features<<featureName
			dfsCollectFeatures(child,features)
		}	
	}
	
	
	static def getLogisticFeatures(classifier){
		def logBase = classifier.m_boostedModel
		int[][] attributes = logBase.getUsedAttributes()
		// First dimension is attributes for class 0 or class 1 (etc.), which are 
		// the same for us...		
		def attributeNames = attributes[0].collect{attr->
			logBase.m_numericDataHeader.attribute(attr).name()
		}
		
		double[][] coefficients = logBase.getCoefficients();
		def attributeWeights = attributes[0].collect{attr->
			coefficients[0][attr+1]
		}
		def constant = coefficients[0][0]  // Not using right now, but might in the future. 
		
		def names2weights = [:]
		attributeNames.eachWithIndex{n,i->
			names2weights[n] = attributeWeights[i]
		}				
					
		names2weights = names2weights.sort{-Math.abs(it.value)}
		return(names2weights)
	}	
	
	static def getSMOFeatures(classifier){
		// if there are only two classes... 
		def c = classifier.m_classifiers[0][1]

		if (classifier.m_filterType == classifier.FILTER_NORMALIZE) println "Normalized values."

		def name2weight = [:]

		c.m_sparseWeights.eachWithIndex{w,i->
		  if (c.m_sparseIndices[i] != (int)classifier.m_classIndex) {
			  def name = c.m_data.attribute(c.m_sparseIndices[i]).name()
			  def value = c.m_sparseWeights[i]
			  name2weight[name] = value 
			  //print Utils.doubleToString(c.m_sparseWeights[i], 12, 4)+"\t"
			  //println c.m_data.attribute(c.m_sparseIndices[i]).name()
		  }
		}
		//println c.m_b

		name2weight = name2weight.sort{-Math.abs(it.value)}
		return(name2weight)
	}		
}