package com.example.shinobipiechart;

import java.util.List;

/* 
 * Description: An interface class for sending the data back from
 * AsyncTask to the Activity
 * 
 */
public interface AsyncResponse {
	void processFinish(List<List<String>> output);
	
	}
