package com.example.shinobipiechart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.shinobicontrols.charts.ChartFragment;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.PieDonutSeries.RadialEffect;
import com.shinobicontrols.charts.PieSeries;
import com.shinobicontrols.charts.PieSeriesStyle;

import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.SimpleDataAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.shinobipiechart.Constants;
import com.example.shinobipiechart.R;

/* 
 * Description: Activity class for displaying the pie chart
 * 
 */

public class PieChartActivity extends Activity implements AsyncResponse {
	
	// chartFragment instance to place a chart inside android app 
	private ChartFragment chartFragment;
	// chart instance to represent the data 
	private ShinobiChart shinobiChart;
	//AsyncTask instance for handling the HTTP POST to the TOTANGO server
	ServerHelper asyncTask =new ServerHelper();
	// Contact value text view instance
	TextView txtContarct = null;
	// Total account textview instance
	TextView txtTotal = null;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shinobi_quick_start);
		
		// Setting the delegate to the current activity
		asyncTask.delegate = this;

		// Should create a chart only for the first time
		if (savedInstanceState == null || chartFragment==null) 
		{
			chartFragment =(ChartFragment) getFragmentManager().findFragmentById(R.id.chart);
				
			ChartFragment oldChartView = null;
			
			@SuppressWarnings("deprecation")
			Object o = getLastNonConfigurationInstance ();
			if (o != null && o instanceof ChartFragment) {
			    oldChartView = (ChartFragment)o;
			}
			    
			// If this is the activity's first existence
			if (oldChartView == null) 
			{
				// Get the a reference to the ShinobiChart
				shinobiChart = chartFragment.getShinobiChart();
				shinobiChart.setLicenseKey(Constants.LICENSE_KEY);
				shinobiChart.setTitle("HEALTH PORTFOLIO");
			}
			else
			{
				// Remove the new ChartFragment from its parent
				ViewGroup parent = ((ViewGroup) chartFragment.getView().getParent());
				parent.removeView(chartFragment.getView());
				
				// Add the old ChartFragment to the parent, and replace the local reference
				parent.addView(oldChartView.getView());
				chartFragment = oldChartView;
			}
		
			try 
			{
				// HTTP POST request to get the data to be displayed
	        	// in the chart
				getDataFromHTTPPOST();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		}
		
		// Initializing the text view with reference to the resource id
		txtContarct = (TextView) findViewById(R.id.txtcontractVal);
		txtTotal = (TextView) findViewById(R.id.txtTotalAccount);
		
		//Button implementation to go to next page
		Button nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(new View.OnClickListener() {
			
		@Override
		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			Intent myIntent = new Intent(PieChartActivity.this, CustomListViewActivity.class);
			PieChartActivity.this.startActivity(myIntent);
		}
		});
		
    }
    
    
    
    
    /*	Method:		To make the activity to retain the
     * 				chartFragment instance on configuration change
	 * parameter:
	 * returns :	Object
	 */
    public Object onRetainNonConfigurationInstance () 
    {
        return chartFragment;
    }
    
    
    
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
	}
	
	
	
	
	/*	Method:		Implementation of interface method to display
     * 				the HTTP response in the chart
	 * parameter:	HTTP Response data(Red Total hits, Red Contract_value sum, 
	 * 				Green Total Hits, Green Contact_value sum, Yellow
	 * 				Total hits, Yellow Contact Value Sum)
	 * returns :	void
	 */
    public void processFinish(List<List<String>> result){
        
    	//This you will received result fired from async class of onPostExecute(result) method. 
    	Long grandTotalHits = Long.parseLong(result.get(0).get(0))+Long.parseLong(result.get(0).get(2))+Long.parseLong(result.get(0).get(4));
    	Double red = (Double.parseDouble(result.get(0).get(0)))/grandTotalHits;
    	Double green = Double.parseDouble(result.get(0).get(2))/grandTotalHits;
		Double yellow = Double.parseDouble(result.get(0).get(4))/grandTotalHits;	
		Double contractValue = Double.parseDouble(result.get(0).get(1))+Double.parseDouble(result.get(0).get(3))+Double.parseDouble(result.get(0).get(5));
			
		// Add the values into the adapter
		SimpleDataAdapter<String, Double> dataAdapter = new SimpleDataAdapter<String, Double>();
		dataAdapter.add(new DataPoint<String, Double>(String.format("%.4f", red), red));
		dataAdapter.add(new DataPoint<String, Double>(String.format("%.4f", green), green));
		dataAdapter.add(new DataPoint<String, Double>(String.format("%.4f", yellow), yellow));
		
		PieSeries series = new PieSeries();
		series.setDataAdapter(dataAdapter);
		if(shinobiChart==null)
		{
			// Get the shinobi chart instance on null
			shinobiChart = chartFragment.getShinobiChart();
			shinobiChart.setLicenseKey(Constants.LICENSE_KEY);
			
			txtContarct = (TextView) findViewById(R.id.txtcontractVal);
			txtTotal = (TextView) findViewById(R.id.txtTotalAccount);
		}
		else
		{
			shinobiChart.addSeries(series);
			
			//change the slice color
			PieSeriesStyle style = series.getStyle();
			style.setFlavorColors(new int[] {
				Color.argb(255, 233, 74, 114),//pink
			    Color.argb(255, 103, 169, 66), // green
			    Color.argb(255, 248, 184, 60), // yellow			      
			});
			
			//styling
			style.setRadialEffect(RadialEffect.FLAT);
			style.setCrustShown(false);
		    style.setLabelTextSize(16.0f);
		    
		  //redraw the chart
		    shinobiChart.redrawChart();
		}
		 
		// Set the Contarct value and Total accounts value to the textview
		txtTotal.setText(grandTotalHits.toString());
		txtContarct.setText("$"+format(contractValue));
     }
    
    

    /*	Method:		Format the Total Contract value into 1 precision
     * 				after decimal
	 * parameter:	Double Contactvalue
	 * 				
	 * returns :	String
	 */
    private static String format(Double number) 
    {
    	int power; 
        String suffix = " kmbt";
        String formattedNumber = "";

        
        NumberFormat formatter = new DecimalFormat("###.#");
        power = (int)StrictMath.log10(number);
        number = number/(Math.pow(10,(power/3)*3));
        formattedNumber=formatter.format(number);
        formattedNumber = formattedNumber + suffix.charAt(power/3);
        return formattedNumber.length()>6 ?  formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber; 
    }
     
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
		if (chartFragment != null) 
		{
			if(shinobiChart==null)
			{	
				shinobiChart = chartFragment.getShinobiChart();
				shinobiChart.setLicenseKey(Constants.LICENSE_KEY);
				
				txtContarct = (TextView) findViewById(R.id.txtcontractVal);
				txtTotal = (TextView) findViewById(R.id.txtTotalAccount);	
			}
		
		// Ensure the GL views get to hear about the pause/resume events
		chartFragment.onResume();
		}
	}
  
    
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		    		
		if (chartFragment != null) 
		{
			// Ensure the GL views get to hear about the pause/resume events
			chartFragment.onPause();
		}
	}
	
	
	/*	Method:		Starts the asynctask to get the data
	 * 				through HTTP POST to the TOTANGO server
	 * parameter:
	 * returns :	void
	 */
	private void getDataFromHTTPPOST()
	{
		asyncTask.execute(Constants.TOTANGO_SERVER_URL_CHART);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		// Remove the old ChartFragment from its parent
		getFragmentManager().beginTransaction().remove(chartFragment).commit();	
	}

}
	
	
	
    
        
      

	

