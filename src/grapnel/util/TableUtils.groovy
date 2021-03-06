import grapnel.util.*

class TableUtils{

	def err = System.err

	def defaultValue = "?"
		
		
	/**
	* Finds the union of all column names and all row names for the given list of 
	* tables. 
	*/ 
	def unionTableIDs(fileNames){
		// Figure out the size of the output table we're going to need by 
		// scanning the input files...	
		def allRowNames = [] as Set
		def allColNames = [] as Set
		fileNames.each{fileName->
			def file = new File(fileName)
			if (!file.exists() || (file.length() == 0)){
				err.println "$fileName does not exist or has size 0."
				return;
			}			
			err.println "Pre-scanning $fileName ..."
			file.withReader{r->
				def colNames = parseColNamesFromHeading(r.readLine())
				allColNames.addAll(colNames)
				r.eachLine{line->
					def rowName,vals
					(rowName,vals) = parseRowValuesFromLine(line,colNames)
					allRowNames << rowName
				}
			}		
		}	
		err.println "Total rows: ${allRowNames.size()}.  Total cols: ${allColNames.size()}"	
		allRowNames = allRowNames.sort()
		return([allRowNames,allColNames])
	}
	
		
	/**
	* Combines several tables into one larger table, doing a union of both rows and columns. 
	* rowNames is the union of all rowNames and colNames is the union of all column names. 
	* Assumes the files have been pre-scanned to determine the combined column and row names.  
	*/ 
	def combineTables(files,combinedRowNames,combinedColNames){		
		
		def rowSet = [] as Set
		def colSet = [] as Set
		
		rowSet.addAll(combinedRowNames)
		colSet.addAll(combinedColNames)
			
		def combinedTable = new Table(combinedRowNames as ArrayList,combinedColNames as ArrayList)

		// Initialize table to contain the specified null value. 
		combinedTable.assign(defaultValue)
		files.each{fileName->
			def file = new File(fileName)
			if (!file.exists() || (file.length() == 0)){
				err.println "$fileName does not exist or has size 0."
				return;
			}			
			err.println "Processing $fileName..."
			file.withReader{r->
				def colNames = parseColNamesFromHeading(r.readLine())
				r.eachLine{line->
					def rowName,vals										
					(rowName,vals) = parseRowValuesFromLine(line,colNames)
					
					// Skip rows we encounter that aren't in our rowSet
					if (!rowSet.contains(rowName)) return;
					
					vals.eachWithIndex{v,i->
						if ((v == null) || (v == "null")) v=defaultValue
						
						// Skip any columns not in our colset
						// Optimization: This buried test might cost us... 
						if (!colSet.contains(colNames[i])) return;						
						combinedTable.set(rowName,colNames[i],v)
					}
				}
			}		
		}
		return(combinedTable)					
	}


	def parseColNamesFromHeading(heading){
		def cols = heading.split("\t")
		cols = cols[1..-1] // omit first column
		return(cols)
	}

	def parseRowValuesFromLine(line,cols){
		def fields = line.split("\t",-1) // -1 ensures that trailing delimiters are split
		def rowName = fields[0]
	
		def vals = fields[1..-1] // 
		if (vals.size() != cols.size()){
			err.println "Values size (${vals.size()} != patients size (${patients.size()}))"
			System.exit(1)
		}
		return([rowName,vals])
	}


}