package grapnel.charts;

import org.jfree.chart.*
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.ui.*
import org.jfree.data.statistics.*;
import org.jfree.data.category.*;
import org.jfree.chart.renderer.category.*


import groovy.swing.SwingBuilder
import java.awt.*
import javax.swing.WindowConstants as WC
import javax.swing.*

import java.awt.Shape
import java.awt.geom.Ellipse2D

import grapnel.util.*

/**
* Configuring JFreeChart charts is not hard, but it does clutter up your code. 
* Moreover, I tend to always want the same settings.  So this class just encapsulates
* the sort of standard version of a lot of charts I commonly create.  
*
*/ 
class Charts{
	
	static err = System.err
	
	/**
	* Saves a chart as PNG with fileName
	*/ 
	static saveChart(chart,fileName){
		def chartpanel = new ChartPanel(chart);
		ImageUtils.savePanelAsPNG(chartpanel,fileName)
	}
	
	/***
	* Creates a window and displays the chart. Window has title chartTitle
	*/ 
	static showChart(params=[:]){
		def mychart = params.chart
		def chartTitle = params.title ?: mychart.getTitle().getText()
		def chartSize = params.chartSize ?: new java.awt.Dimension(400,200)

		def swing = new SwingBuilder()
		def frame = swing.frame(title:chartTitle,
								defaultCloseOperation:WC.EXIT_ON_CLOSE,
								pack:true,show:true) {														
			borderLayout()
			panel(new ChartPanel(mychart),preferredSize: chartSize,mouseWheelEnabled:true)
		}
		return(swing)		
	}
	
	static def showCharts(params=[:]){

		def charts = params.charts
		def chartsTitle = params.title ?: "Chart Window"
		def chartSize = params.chartSize ?: new Dimension(400,200)
		def orientation = params.orientation ?: "HORIZONTAL"

		def swing = new SwingBuilder()
		def frame = swing.frame(title:chartsTitle,
								defaultCloseOperation:WC.EXIT_ON_CLOSE,
								pack:true,show:true) {														
		
			if (orientation=="HORIZONTAL") gridLayout(rows:1,columns:charts.size())
			else gridLayout(rows:charts.size(),columns:1)
			charts.each{chart->													
				panel(new ChartPanel(chart),preferredSize: chartSize,mouseWheelEnabled:true)
			}
		}
		return(swing)			
	}
	 
  /**
  * Creates an XY series from a Groovy [:], aka LinkedHashMap
  */ 
  static createXYFromMap(LinkedHashMap data,String seriesName){
    XYSeries series1 = new XYSeries(seriesName);    
    for(x in data.keySet()){
      def y = data.get(x);
      series1.add(x,y);
    }        
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    
    return(dataset);
  }

	/**
	* Creates an XYSeries from a pair of collections X and Y
	*/ 
	static createXYFromCollections(Collection X,Collection Y,String seriesName){
		XYSeries series1 = new XYSeries(seriesName);    
		for(int i = 0;i < X.size();i++){
			def x = X[i];
			def y = Y[i];
			series1.add(x,y);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		return(dataset);
	}

	/**
	* Creates an XYSeries from a pair of DoubleVectors
	*/ 
	static createXYFromDoubleVectors(DoubleVector X,DoubleVector Y,String seriesName){
    XYSeries series1 = new XYSeries(seriesName);    
		for(int i = 0;i < X.size();i++){
			def x = X[i];
			def y = Y[i];			
      series1.add(x,y);
    }        
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);    
    return(dataset);
  }
  
  /**
  * Returns a default line chart with data in a LinkedHashMap. 
  */
  static lineChart(String title,LinkedHashMap data,int xsize,int ysize){            
    def xydata = createXYFromMap(data,"Series 1");    
    return(lineChart(title,xydata,xsize,ysize))
  }
  
	/**
	* Creats a line chart form data in an XYSeriesCollection
	*/ 
  static lineChart(String title,XYSeriesCollection xydata){
    
    // create the chart...
    JFreeChart chart = ChartFactory.createXYLineChart(
        title,      // chart title
        "X",                      // x axis label
        "Y",                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        true,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setDefaultShapesVisible(true);
    renderer.setDefaultShapesFilled(true);

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }
  
	/**
	* XY Plot from two collections. 
	*/ 
	static xyplot(String title,String xlabel,String ylabel,Collection x,Collection y){
		def xydata = createXYFromCollections(x,y,"Series 1")
		return(xyplot(title,xlabel,ylabel,xydata))
	}

	/**
	* XY Plot from two DoubleVectors
	*/ 
	static xyplot(String title,String xlabel,String ylabel,DoubleVector x,DoubleVector y){
		def xydata = createXYFromDoubleVectors(x,y,"Series 1")
		return(xyplot(title,xlabel,ylabel,xydata))
	}


	/**
	* XY Plot with generic X and Y labels. 
	*/ 
	static xyplot(String title,DoubleVector x,DoubleVector y){
		def xydata = createXYFromDoubleVectors(x,y,"Series 1")
		return(xyplot(title,"X","Y",xydata))
	}

	/**
	* XY Plot from LinkedHashMap data. 
	*/ 
	static xyplot(String title,LinkedHashMap data){
		def xydata = createXYFromMap(data,"Series 1")
		return(xyplot(title,"X","Y",xydata))
	}

	/**
	* XY Plot from XYSeriesCollection
	*/ 
  static xyplot(String title,XYSeriesCollection xydata){
    return(xyplot(title,"X","Y",xydata))
  }
  
	/**
	*	XY Plot from XYSeriesCollection
	*/ 
  static xyplot(String title,String xlabel,String ylabel, 
    XYSeriesCollection xydata){
    
    // Only show legend if there is more than one series. 
    def bShowLegend = false;
    if (xydata.getSeriesCount() > 1) bShowLegend = true;
    else bShowLegend = false;
    
    // create the chart...
    JFreeChart chart = ChartFactory.createScatterPlot(
        title,      // chart title
        xlabel,                      // x axis label
        ylabel,                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        bShowLegend,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
	plot.setForegroundAlpha(0.30f);		
	
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setDefaultShapesVisible(true);
    renderer.setDefaultShapesFilled(true);
    renderer.setDefaultLinesVisible(false);	
	def color1 = Color.blue
	renderer.setSeriesPaint(0, color1);
	Shape shape  = new Ellipse2D.Double(0,0,8,8);
	renderer.setDefaultShape(shape);
	renderer.setSeriesShape(0, shape);
	
	plot.setBackgroundPaint(new Color(0,0,0,10)); 

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }

	/***
	* Create a histogram from values in an arbitrary collection...
	*/ 						
	static hist(cName,values){
		
		def binmax = Collections.max(values) //values.max()
		def binmin = Collections.min(values) //values.min()
		
		//err.println "createHistogramFromValues"
		def series = new HistogramDataset()
		def valarray = new double[values.size()]
		values.eachWithIndex{v,i->valarray[i] = v as double}

		series.addSeries("Series1",valarray,50,binmin as double,binmax as double)
		def chartTitle = "$cName"
		def chart = Charts.hist(chartTitle,"","",series) 

		def titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD,12)
		def title = new org.jfree.chart.title.TextTitle(chartTitle,titleFont)
		chart.setTitle(title);

		return(chart)
	}
  
	/***
	* Create histogram with generic labels. 
	*/ 
  static hist(String title,HistogramDataset xydata){
    return(hist(title,"Count","X",xydata))
  }
  
	/***
	* Create histogram from HistogramDataset
	*/ 
  static hist(String title,String xlabel,String ylabel, HistogramDataset xydata){
    
    // Only show legend if there is more than one series. 
    def bShowLegend = false;
    if (xydata.getSeriesCount() > 1) bShowLegend = true;
    else bShowLegend = false;
    
    // create the chart...
    JFreeChart chart = ChartFactory.createHistogram(
        title,      // chart title
        xlabel,                      // x axis label... KJD: if null doesn't it take it from series???
        ylabel,                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        bShowLegend,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
	//plot.setBackgroundPaint(Color.lightGray); 
	plot.setBackgroundPaint(new Color(0,0,0,25));  
	plot.setForegroundAlpha(0.80f);
	
	def renderer = (XYBarRenderer) plot.getRenderer();
	def color1 = Color.blue
	renderer.setSeriesPaint(0, color1);

	if (xydata.getSeriesCount() > 1) plot.setForegroundAlpha(0.85f);

    renderer.setDrawBarOutline(false);
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setShadowVisible(false);

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }

	static def createDatasetFromCollection(values){
		def binmax = Collections.max(values) //values.max()
		def binmin = Collections.min(values) //values.min()

		//err.println "createHistogramFromValues"
		def series = new HistogramDataset()
		def valarray = new double[values.size()]
		values.eachWithIndex{v,i->valarray[i] = v as double}

		series.addSeries("Series1",valarray,50,binmin as double,binmax as double)
		return(series)
	}	
  
	static def addMarker(hist,location,label){
		ValueMarker marker = new ValueMarker(location);  // position is the value on the axis
		marker.setPaint(Color.green)
		marker.setStroke(new BasicStroke((float)2.0))
		marker.setLabelAnchor(RectangleAnchor.TOP);
		marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
		marker.setLabel(label); // see JavaDoc for labels, colors, strokes
		XYPlot plot = (XYPlot) hist.getPlot();
		plot.addDomainMarker(marker);				
	}
  
  
	static hist(params=[:]){
		
		//String title,String xlabel,String ylabel, HistogramDataset xydata){
		def title = params.title ?: "Dual Histogram"		
		def xlabel = params.xlabel ?: "X-axis"
		def ylabel = params.ylabel ?: "Y-axis"
		def dataset = params.dataset
		
		if (!(dataset instanceof HistogramDataset)) dataset = createDatasetFromCollection(dataset)				
		
		// Only show legend if there is more than one series. 
		def bShowLegend = false;
		if (dataset.getSeriesCount() > 1) bShowLegend = true;
		else bShowLegend = false;
  
		// create the chart...
		JFreeChart chart = ChartFactory.createHistogram(
				  title,      // chart title
				  xlabel,                      // x axis label... KJD: if null doesn't it take it from series???
				  ylabel,                      // y axis label
				  dataset,                  // data
				  PlotOrientation.VERTICAL,
				  bShowLegend,                     // include legend
				  false,                     // tooltips
				  false                     // urls
		);

		// get a reference to the plot for further customisation...
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		//plot.setBackgroundPaint(Color.lightGray); 
		plot.setBackgroundPaint(new Color(0,0,0,25));  
		plot.setForegroundAlpha(0.80f);
		   
		def renderer = (XYBarRenderer) plot.getRenderer();
		def color1 = Color.blue
		renderer.setSeriesPaint(0, color1);
		   
		if (dataset.getSeriesCount() > 1) plot.setForegroundAlpha(0.85f);
		   
		renderer.setDrawBarOutline(false);
		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setShadowVisible(false);
		   
		// change the auto tick unit selection to integer units only...
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());		   
		return chart;
	}
  

	/***
	* Create a histogram from two sets of values in arbitrary collection...
	* KJD TODO: change hist and other chart functions to take named parameters. 
	*/ 						
	static dualhist(params=[:]){
		
		// PARAMS:
		def cName = params.title ?: "Dual Histogram"
		def series1name = params.series1name ?: "Series1"
		def series2name = params.series2name ?: "Series2"
		def values1 = params.series1 ?: [1,2,3]
		def values2 = params.series2 ?: [2,4,6]
		
		def color1 = params.color1 ?: Color.blue
		def color2 = params.color2 ?: Color.green


		// 
		double binmax1 = values1.max()
		double binmax2 = values2.max()
		double binmin1 = values1.min()
		double binmin2 = values2.min()

		//err.println "createHistogramFromValues"
		def series = new HistogramDataset()
		def valarray1 = new double[values1.size()]
		values1.eachWithIndex{v,i->valarray1[i] = v as double}

		def valarray2 = new double[values2.size()]
		values2.eachWithIndex{v,i->valarray2[i] = v as double}

		def chartTitle = "$cName"
		series.addSeries(series1name,valarray1,100,binmin1,binmax1)
		series.addSeries(series2name,valarray2,100,binmin2,binmax2)
		def chart = Charts.hist(chartTitle,"","",series) 

		def titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD,14)
		def title = new org.jfree.chart.title.TextTitle(chartTitle,titleFont)
		chart.setTitle(title);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);  // For the plot
    	plot.setForegroundAlpha(0.60f);

		def renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, color1);
		renderer.setSeriesPaint(1, color2);

		return(chart)
	} 
	
	static categoryHistogram(title,xlabel,ylabel,label2countMap){
		def categoryData = createCategoryFromMap(label2countMap)
		JFreeChart chart = ChartFactory.createBarChart(
		            title,         // chart title
		            xlabel,               // domain axis label
		            ylabel,                  // range axis label
		            categoryData,                  // data
		            PlotOrientation.HORIZONTAL, // orientation
		            false,                     // include legend
		            true,                     // tooltips?
		            false                     // URLs?
		        );
						
		final CategoryPlot plot = chart.getCategoryPlot();
		def renderer = (BarRenderer) plot.getRenderer()
		renderer.setBarPainter(new StandardBarPainter());	
		renderer.setShadowVisible(false);	
		return(chart);
	}

	static CategoryDataset createCategoryFromMap(label2countMap) {
		def dataset = new DefaultCategoryDataset( );
		label2countMap.each{label,count->
			dataset.addValue(count,"S1",label)
		 }		  
	     return dataset; 
	}
	
}