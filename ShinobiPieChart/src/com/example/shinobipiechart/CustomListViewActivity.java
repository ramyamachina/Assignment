package com.example.shinobipiechart;

import java.util.ArrayList;
import java.util.List;

import com.example.shinobipiechart.R;

import android.os.Bundle;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


/* 
 * Description: General class containing the constant declarations.
 * 
 */

public class CustomListViewActivity extends Activity implements AsyncResponse{
	
    ServerHelper asyncTask =new ServerHelper();
    private ListView lstView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        // delegates results on postExecution of asyncTask
        asyncTask.delegate = this;
        
        try 
        {
        	// HTTP POST request to get the data to be displayed
        	// in the list
        	getDataFromHttpPOST();
        	
        } catch (Exception e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Custom list view
        lstView = (ListView) findViewById(R.id.ListView01);
        lstView.setOnItemClickListener(new OnItemClickListener() {
 
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Object o = lstView.getItemAtPosition(position);
			SetterGetter fullObject = (SetterGetter)o;
			Toast.makeText(CustomListViewActivity.this, "You have chosen: " + " " + fullObject.getName(), Toast.LENGTH_LONG).show();
			
		}  
        });
    }
    
    
    
	/*	Method:		Starts the asynctask to get the data
	 * 				through HTTP POST to the TOTANGO server
	 * parameter:
	 * returns :	void
	 */
    private void getDataFromHttpPOST() {
		// TODO Auto-generated method stub
    	asyncTask.execute(Constants.TOTANGO_SERVER_URL_LIST);
	}
    
    
    
    /*	Method:		Implementation of interface method to display
     * 				the HTTP response in the list view
	 * parameter:	HTTP Response data(Name,week, $ spent, 
	 * 				% of Engagement, ActiveUserCount)
	 * returns :	void
	 */

	@Override
	public void processFinish(List<List<String>> output) {
		// TODO Auto-generated method stub
		
		ArrayList<SetterGetter> results = new ArrayList<SetterGetter>();
		for(int i=0;i<output.size();i++)
		{
			SetterGetter sr = new SetterGetter();
			sr.setName(output.get(i).get(0));
			sr.setWeek(output.get(i).get(1));
			sr.setCount(output.get(i).get(2));
			sr.setEngValue(output.get(i).get(3));
			sr.setuserValue(output.get(i).get(4));
			 
			results.add(sr);
		}
	
		lstView.setAdapter(new CustomBaseAdapter(this, results));
	}

}