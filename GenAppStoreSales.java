/* Modified to download Apple Store rates.txt and plot it scheduled graphics
 * Rafael Redondo Tejedor April 2013
 */

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2011, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ------------------------------
 * CombinedCategoryPlotDemo1.java
 * ------------------------------
 * (C) Copyright 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * Changes
 * -------
 * 05-May-2008 : Version 1 (DG);
 *
 */


import java.awt.Font;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.plot.CombinedCategoryPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo for the {@link CombinedCategoryPlot} class.
 */
@SuppressWarnings("serial")
public class GenAppStoreSales extends ApplicationFrame {

	public static String currentPath;
	public static String sourcePath;
	public static String chartPath;
	
	public static int pursuedDay;
	public static int pursuedMonth;
	public static int pursuedYear;
	public static int currentDay;
	public static int currentMonth;
	public static int currentYear;
	
	public static Calendar rollCalendar;
	public static Calendar customCalendar;
	
	public static ArrayList<String> countryLabels;
	public static ArrayList<ArrayList<Integer>> countryUnits;
	public static ArrayList<String> countryInAppLabels;
	public static ArrayList<ArrayList<Integer>> countryInAppUnits;
	
	public static String appID = "EightDigitNumber"; // Put your app ID here
	public static String email = "my@email.com"; 	 // Put your email account here
	public static String password = "myPassword"; 	 // Put your itunes password here
    public static String autoingestionPreArgs = " " + email + " " + password + " " + appID + " Sales ";
    public static String appSKU = "myAppSKU";

	public static String chartTitle, windowTitle;
	public static int periodUnits;
	
	public static int widthChart = 1700, heightChart = 1000;
	public static JFreeChart chart;
	
	public static int totalUnits = 0, totalInAppUnits = 0, totalUpdateUnits = 0, dayUnits = 0, dayINAPPUnits = 0;
	
    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public GenAppStoreSales(String title, ArrayList<String> cLabels,ArrayList<ArrayList<Integer>> cUnits) {
        super(title);
        JPanel chartPanel = createDemoPanel(title, cLabels, cUnits);
        chartPanel.setPreferredSize(new java.awt.Dimension(widthChart, heightChart));
        setContentPane(chartPanel);
    }

    /**
     * Creates a dataset.
     *
     * @return A dataset.
     */
    public static CategoryDataset createDataset1(ArrayList<String> cLabels,ArrayList<ArrayList<Integer>> cUnits) {
    	
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        
        for (int country = 0; country < cUnits.size(); country++)
        	for (int d = cUnits.get(country).size()-1; d >= 0 ; d--)
        		result.addValue( cUnits.get(country).get(d), cLabels.get(country),
        					String.valueOf(cUnits.get(country).size()-d) );

        return result;
    }

    /**
     * Creates a dataset.
     *
     * @return A dataset.
     */
    public static CategoryDataset createDataset2(ArrayList<String> cLabels,ArrayList<ArrayList<Integer>> cUnits) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();

        periodUnits = 0;
        int sum = 0;
        for (int d = cUnits.get(0).size()-1; d >= 0; d--) {
        	for (int country = 0; country < cUnits.size(); country++) {
        		sum += cUnits.get(country).get(d);
        	}
       		result.addValue(sum, "0", String.valueOf(cUnits.get(0).size()-d));
       		periodUnits += sum;
       		sum = 0;
    	}
        chartTitle = windowTitle + " @ Total Sales = " + String.valueOf(periodUnits) + " units";

        return result;

    }

    /**
     * Creates a chart.
     *
     * @return A chart.
     */
    private static JFreeChart createChart(ArrayList<String> cLabels,ArrayList<ArrayList<Integer>> cUnits) {

        CategoryDataset dataset1 = createDataset1(cLabels,cUnits);
        NumberAxis rangeAxis1 = new NumberAxis("Value");
        rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
        renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        CategoryPlot subplot1 = new CategoryPlot(dataset1, null, rangeAxis1,renderer1);
        subplot1.setDomainGridlinesVisible(true);

        CategoryDataset dataset2 = createDataset2(cLabels,cUnits);
        NumberAxis rangeAxis2 = new NumberAxis("Value");
        rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer2 = new BarRenderer();
        renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        CategoryPlot subplot2 = new CategoryPlot(dataset2, null, rangeAxis2,renderer2);
        subplot2.setDomainGridlinesVisible(true);

        CategoryAxis domainAxis = new CategoryAxis("Time");
        CombinedCategoryPlot plot = new CombinedCategoryPlot(domainAxis, new NumberAxis("Units"));
        plot.add(subplot1, 2);
        plot.add(subplot2, 1);

        JFreeChart result = new JFreeChart(chartTitle, new Font("SansSerif", Font.BOLD, 22), plot, true);
        return result;

    }
    
    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel(String title, ArrayList<String> cLabels,ArrayList<ArrayList<Integer>> cUnits) {
        chart = createChart(cLabels,cUnits);
        return new ChartPanel(chart);
    }

    /** Connects to the AppleStore and download pending sale reports
     * (if they are still available)
     * Requires Auntoingestion.class in the same path together with GenAppStoreSales 
     * @throws IOException 
     */
    private static void autoingestionDownload(String reportName, String dateType, String dateCode) throws IOException
    {
    	File reportFile = new File(sourcePath, reportName);    	
		Runtime rt = Runtime.getRuntime();
		
    	if (!reportFile.isFile()) {
    		String s;
 
    		System.out.println(reportName + " requesting...");
    		Process downloadReport = rt.exec("java Autoingestion" + autoingestionPreArgs + dateType + " " + "Summary " + dateCode);
    		
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(downloadReport.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(downloadReport.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null)
            	System.out.println(s);

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null)
            	System.out.println(s);
//            System.out.println("gzip -d " + currentPath.replace(" ", "\\ ") + "/" + reportName);
//            System.out.println("mv " + reportName + " " + sourcePath.replace(" ", "\\ ") + "/" + reportName);
            rt.exec("gzip -d " + currentPath.replace(" ", "\\ ") + "/" + reportName);
            rt.exec("mv " + reportName + " " + sourcePath.replace(" ", "\\ ") + "/" + reportName);
            rt.exec("rm " +currentPath.replace(" ", "\\ ")+ "/*.gz");
            
    	}
//    	else System.out.println(reportName + " verified");
    }
    
    private static void updateRollCalendar()
    {
		pursuedDay = rollCalendar.get(Calendar.DATE);
		pursuedMonth = rollCalendar.get(Calendar.MONTH)+1;
		pursuedYear = rollCalendar.get(Calendar.YEAR);
    }
    private static void printDatePeriod(String periodType, String documentType)
    {
    	rollCalendar.add(Calendar.DATE, 1);
    	updateRollCalendar();
        System.out.print(periodType + " "+documentType+" verified from " + pursuedDay +"."+pursuedMonth+"."+pursuedYear);
        System.out.println(" to " + (currentDay-1) +"."+currentMonth+"."+currentYear);
    	rollCalendar.add(Calendar.DATE, -1);
    	updateRollCalendar();
    }  
    
    private static void readSales(String periodTime, boolean restoreSales, boolean computeSales) throws IOException 
    {	
		countryLabels = new ArrayList<String>();
		countryUnits = new ArrayList<ArrayList<Integer>>();
		countryInAppLabels = new ArrayList<String>();
		countryInAppUnits = new ArrayList<ArrayList<Integer>>();
    	int t = 0, periodDays = 0;   	
    	boolean anotherDay = true;
    	if (periodTime.equals("day")) 
    		periodDays = 1;
    	else if (periodTime.equals("week")) 
    		periodDays = 7;
    	else if (periodTime.equals("month")) 
    		periodDays = rollCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    	else if (periodTime.equals("year")) 
    		periodDays = rollCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
    	else if (periodTime.equals("custom")) {
    		while (rollCalendar.compareTo(customCalendar) > 0) {
    			rollCalendar.add(Calendar.DATE, -1);
    			periodDays++;
    		}
    		rollCalendar.add(Calendar.DATE, periodDays);
    	}
    	
    	while (anotherDay) {
			String dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);
			String reportName = "S_D_" + appID + "_" + dateCode + ".txt";
			File salesFile = new File(sourcePath + "/" + reportName);
			if (salesFile.isFile()) {
				String SKU, productCountry;
				int productUnits;
				boolean update;
				float productPrice;
		        BufferedReader salesFileReader = new BufferedReader(new FileReader(sourcePath + "/" + reportName));
		        String line = salesFileReader.readLine(); // skips the first line
		        while((line = salesFileReader.readLine()) != null)
		        {   
		        	String[] columns = line.split("\t");
		        	// SKU
		        	SKU = columns[2];
		        	// Product Units
		        	if (columns[6].equals("7T"))
		        		update = true;
		        	else update = false;
		        	productUnits = Integer.parseInt(columns[7]);
		        	// Product Price
		        	productPrice = Float.parseFloat(columns[8]);
		        	// Country Code
		        	productCountry = columns[12];
		        	
//		        	System.out.println("SKU " + SKU + " Units " + productUnits + " Country Code " + productCountry);
		        	
	        		if (SKU.equals(appSKU)) {
	        			if (periodTime.equals("day")) {dayUnits += productUnits;}
	        			if (computeSales) {
	        				totalUnits += productUnits;
	        				if (update) { totalUpdateUnits += productUnits;}
	        				}
			        	if (restoreSales) {	
		        			int index = countryLabels.indexOf(productCountry);
		        			if (index < 0 ) {
			        			countryLabels.add(productCountry);
			        			countryUnits.add(new ArrayList<Integer>(Collections.nCopies(periodDays, 0)));
			        			countryUnits.get(countryUnits.size()-1).set(t, productUnits);
		        			}
		        			else countryUnits.get(index).set(t, productUnits);
			        	}
	        		}
	        		else {
	        			if (periodTime.equals("day")) {dayINAPPUnits += productUnits;}
	        			if (computeSales) { totalInAppUnits += productUnits; }
			        	if (restoreSales) {
		        			int index = countryInAppLabels.indexOf(productCountry);
		        			if (index < 0 ) {
			        			countryInAppLabels.add(productCountry);
			        			countryInAppUnits.add(new ArrayList<Integer>(Collections.nCopies(periodDays, 0)));
			        			countryInAppUnits.get(countryInAppUnits.size()-1).set(t, productUnits);
		        			}
		        			else countryInAppUnits.get(index).set(t, productUnits);
			        	}
		        	}
		        }
		        salesFileReader.close();
			}

			if (periodTime.equals("day")) {
					anotherDay = false;
			}
			else if (periodTime.equals("week")){
	    		if (rollCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
	    			anotherDay = false;
	    		}
	    	}
	    	else if (periodTime.equals("month")) {
	    		if (rollCalendar.get(Calendar.DAY_OF_MONTH) == 1){
	    			anotherDay = false;
	    		}
	    	}
	    	else if (periodTime.equals("year")) {
	    		if (rollCalendar.get(Calendar.DAY_OF_YEAR) == 1){
	    			anotherDay = false;
	    		}
	    	}
	    	else if (periodTime.equals("custom")) {
	    		if (rollCalendar.compareTo(customCalendar) <= 0){
	    			anotherDay = false;
	    		}
	    	}
			rollCalendar.add(Calendar.DATE, -1);
			updateRollCalendar();
			t++;
	    }
    }
    
    public static void genPlot(String periodType, String plotFileName, String pursuedPeriodDate, ArrayList<String> cLabels, ArrayList<ArrayList<Integer>> cUnits) 
    {
    	windowTitle = "TOnOa Music "+periodType+" Sales " + pursuedPeriodDate;
		GenAppStoreSales demo = new GenAppStoreSales(windowTitle, cLabels, cUnits);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
//		demo.setVisible(true);

		try {
			File plotFile = new File(chartPath + "/" + plotFileName + ".png"); 
			ChartUtilities.saveChartAsPNG(plotFile, chart, widthChart, heightChart);
			System.out.println("Generated " + periodType + " chart " + plotFileName);
		} catch (IOException e) { 
			e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates. 
		}
    }
    
    /**
     * Starting point for the demonstration application.
     * @throws IOException 
     * 
     */
    public static void main(String[] args) throws IOException {
    	System.out.print("\nRefreshing Apple Store Reports...");
        
    	// Init Calendars
		rollCalendar = Calendar.getInstance();
        updateRollCalendar();
		currentYear = rollCalendar.get(Calendar.YEAR);
		currentMonth = rollCalendar.get(Calendar.MONTH)+1;
		currentDay = rollCalendar.get(Calendar.DATE);
        Calendar launchCalendar = Calendar.getInstance();
        int launchYear = 2013, launchMonth = 2, launchDay = 28; //Month from 0..11
        launchCalendar.set(launchYear, launchMonth, launchDay);
    	/* Report Folders
    	 */
		currentPath = new File("").getAbsolutePath();
		String sourceName = "/sources";
		File sourceDir = new File(currentPath, sourceName);
		if (!sourceDir.isDirectory()) {
			if (!(new File(currentPath + sourceName)).mkdirs()) {
			    System.out.println("[Error] Couldn't create 'source' folder.");
			}
		}
		sourcePath = sourceDir.getAbsolutePath();

		String chartName = "/charts";
		File chartDir = new File(currentPath, chartName);
		if (!chartDir.isDirectory()) {
			if (!(new File(currentPath + chartName)).mkdirs()) {
			    System.out.println("[Error] Couldn't create 'chart' folder.");
			}
		}
        chartPath = chartDir.getAbsolutePath();
        
        String dateCode, reportName;
        
        // DAILY REPORT
        System.out.println("\n-> Daily reports");
        for (int d = 0; d < 14; d++) 
        {
        	rollCalendar.add(Calendar.DATE, -1);
        	if (rollCalendar.compareTo(launchCalendar) <= 0)
        		break;
    		updateRollCalendar();
    		dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);
    		reportName = "S_D_" + appID + "_" + dateCode + ".txt";
    		autoingestionDownload(reportName, "Daily", dateCode);
        }
        printDatePeriod("DAILY","report");
        
        // WEEKLY REPORT
        System.out.println("\n-> Weekly reports");
        rollCalendar = Calendar.getInstance();
        rollCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		pursuedDay = currentDay; pursuedMonth = currentMonth; pursuedYear = currentYear;
        for (int w = 0; w < 13; w++) 
        { 
        	rollCalendar.add(Calendar.DATE, -7);
        	if (rollCalendar.compareTo(launchCalendar) <= 0)
        		break;
        	updateRollCalendar();
    		dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);
    		reportName = "S_W_" + appID + "_" + dateCode + ".txt";
    		autoingestionDownload(reportName, "Weekly", dateCode);
        }
        printDatePeriod("WEEKLY","report");

        // MONTHLY REPORTS
        System.out.println("\n-> Monthly reports");
        rollCalendar = Calendar.getInstance();
        pursuedDay = currentDay; pursuedMonth = currentMonth-1; pursuedYear = currentYear;
        for (int m = 0; m < 12; m++) 
        { 
        	rollCalendar.add(Calendar.MONTH, -1);
        	rollCalendar.set(Calendar.DATE, rollCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        	if (rollCalendar.compareTo(launchCalendar) <= 0)
        		break;
        	updateRollCalendar();
    		dateCode = String.format("%d%02d", pursuedYear, pursuedMonth);
    		reportName = "S_M_" + appID + "_" + dateCode + ".txt";
    		autoingestionDownload(reportName, "Monthly", dateCode);
        }
        printDatePeriod("MONTHLY","report");

        // YEARLY REPORTS
        System.out.println("\n-> Yearly reports");
        rollCalendar = Calendar.getInstance();
        rollCalendar.add(Calendar.DATE,-1);
        pursuedDay = currentDay-1; pursuedMonth = currentMonth; pursuedYear = currentYear;
        for (int y = 0; y < 100; y++) 
        { 
        	rollCalendar.add(Calendar.YEAR, -1);
        	if (rollCalendar.compareTo(launchCalendar) <= 0)
        		break;
        	updateRollCalendar();
    		dateCode = String.format("%d", pursuedYear);
    		reportName = "S_Y_" + appID + "_" + dateCode + ".txt";
    		autoingestionDownload(reportName, "Yearly", dateCode);
        }
        printDatePeriod("YEARLY","report");
        
        /**
         * Reading Sales.txt & Generating Charts
         */
        // WEEK CHARTS
        String plotName, pursuedPeriodDate;
        System.out.print("\nRestoring charts...\n");
        System.out.println("-> Week charts");
        rollCalendar = Calendar.getInstance();        
        rollCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        rollCalendar.add(Calendar.DATE, -7);
        updateRollCalendar();
		while (rollCalendar.compareTo(launchCalendar) > 0) {
	    	pursuedPeriodDate = String.format("%d.%d.%d", pursuedDay, pursuedMonth, pursuedYear);
			dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);			
			plotName = "S_W_" + appID + "_" + dateCode;
			File plotFile = new File(chartPath + "/" + plotName + ".png");
			if (!plotFile.isFile()) {
				readSales("week", true, true);
				if (countryLabels.size() > 0) {
					genPlot("WEEK", plotName, pursuedPeriodDate, countryLabels,countryUnits);
				}
				if (countryInAppLabels.size() > 0) {
					genPlot("WEEK IN-APP", plotName + "_InApp", pursuedPeriodDate, countryInAppLabels,countryInAppUnits);
				}				
			}
			else readSales("week", false, true);// Week already plotted
		}
		printDatePeriod("WEEK","charts");

		// Incomplete current Week (let the current day be computed)
		rollCalendar = Calendar.getInstance();  
		updateRollCalendar();
		readSales("week", false, true);	
		// MONTH CHARTS
		System.out.println("\n-> Month charts");
        rollCalendar = Calendar.getInstance();   
        rollCalendar.add(Calendar.MONTH, -1);
        rollCalendar.set(Calendar.DATE, rollCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        updateRollCalendar();
		while (rollCalendar.compareTo(launchCalendar) > 0) {
			pursuedPeriodDate = String.format("%d.%d.%d", pursuedDay, pursuedMonth, pursuedYear);
			dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);
			plotName = "S_M_" + appID + "_" + dateCode;
			File plotFile = new File(chartPath + "/" + plotName + ".png");
			if (!plotFile.isFile()) {
				readSales("month", true, false);
				if (countryLabels.size() > 0) {
					genPlot("MONTH", plotName, pursuedPeriodDate, countryLabels,countryUnits);
				}
				if (countryInAppLabels.size() > 0) {
					genPlot("MONTH", plotName + "_InApp", pursuedPeriodDate, countryInAppLabels,countryInAppUnits);
				}				
			}
			else {readSales("month", false, false);}

		}
		printDatePeriod("MONTH","charts");
	
		// YEAR CHARTS
		System.out.println("\n-> Year charts");
        rollCalendar = (Calendar)launchCalendar.clone();
        rollCalendar.set(Calendar.DATE, -1);
        rollCalendar.add(Calendar.YEAR, -1);
        updateRollCalendar();
		while (rollCalendar.compareTo(launchCalendar) > 0) {
			pursuedPeriodDate = String.format("%d.%d.%d", pursuedDay, pursuedMonth, pursuedYear);
			dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);			
			plotName = "S_Y_" + appID + "_" + dateCode;
			File plotFile = new File(chartPath + "/" + plotName + ".png");
			if (!plotFile.isFile()) {
				readSales("year", true, false);
				if (countryLabels.size() > 0) {
					genPlot("YEAR", plotName, pursuedPeriodDate, countryLabels,countryUnits);
				}
				if (countryInAppLabels.size() > 0) {
					genPlot("YEAR", plotName + "_InApp", pursuedPeriodDate, countryInAppLabels,countryInAppUnits);
				}				
			}
			else readSales("year", false, false);
		}
		printDatePeriod("YEAR","charts");

		// CUSTOM CHART PERIOD
		System.out.println("\n-> Custom charts");
		customCalendar = (Calendar)launchCalendar.clone(); 		// begin day
        rollCalendar = Calendar.getInstance();	// end day
        rollCalendar.add(Calendar.DATE, -1);
        updateRollCalendar();
		pursuedPeriodDate = String.format("%d.%d.%d", pursuedDay, pursuedMonth, pursuedYear);
		dateCode = String.format("%d%02d%02d", pursuedYear, pursuedMonth, pursuedDay);			
		plotName = "S_C_" + appID + "__whole";// + dateCode;
		File plotFile = new File(chartPath + "/" + plotName + ".png");
		if (!plotFile.isFile()) {
			readSales("custom", true, false);
			if (countryLabels.size() > 0) {
				genPlot("CUSTOM", plotName, pursuedPeriodDate, countryLabels,countryUnits);
			}
			if (countryInAppLabels.size() > 0) {
				genPlot("CUSTOM IN-APP", plotName + "_InApp", pursuedPeriodDate, countryInAppLabels,countryInAppUnits);
			}			
		}
		printDatePeriod("CUSTOM Period","charts");
		
		
		// Day Sales units
		rollCalendar = Calendar.getInstance();
		rollCalendar.add(Calendar.DATE, -1);
		updateRollCalendar();
		readSales("day", false, false);	

		System.out.println("\nTotal units: " + totalUnits + "/" + totalUpdateUnits + "-Up (+" + dayUnits + ")");
		System.out.println("Total IN-APP units: " + totalInAppUnits + " (+" + dayINAPPUnits + ")\n");
		System.exit(0);
    }

}
