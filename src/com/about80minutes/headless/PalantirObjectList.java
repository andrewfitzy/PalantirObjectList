package com.about80minutes.headless;

import java.util.List;

import com.palantir.api.horizon.v1.HObjectResultsPage;
import com.palantir.api.horizon.v1.HorizonConnection;
import com.palantir.api.horizon.v1.object.HObject;
import com.palantir.api.workspace.PalantirClient;
import com.palantir.api.workspace.PalantirClients;
import com.palantir.api.workspace.PalantirHeadlessClientContext;

/**
 * Example of creating a headless client and paging through the list of objects
 * in the base realm. This functionality in itself isn't massively useful
 * however the way a connection is established and results paged is useful.
 */
public class PalantirObjectList {

	private String username = "admin";
	private String password = "palantir";

	private PalantirClient client = null;
	private PalantirHeadlessClientContext context = null;

	/**
	 * Main method, bootstraps the application
	 * 
	 * @param args a {@link java.lang.String} array of arguments.
	 */
	public static void main(String[] args) {
		PalantirObjectList test = new PalantirObjectList();
		test.setupConnection();
		Integer objectCount = test.countObjectIDs();
		System.out.println(String.format("Number of results: %d", objectCount));
		test.closeConnection();
	}

	/**
	 * Sets up a connection to Palantir
	 */
    private void setupConnection() {
    	//get a headless client, this is a client with no UI
    	client = PalantirClients.getHeadlessClient();
    	
    	//login using the credentials listed
		client.login(username, password.toCharArray());
		context = client.getClientContext();
    }
    
    /**
     * Method for getting a list of object IDs
     * 
     * @return a {@link java.util.List} of {@link java.lang.Long} object IDs
     */
    private Integer countObjectIDs() {
    	Integer objectCount = Integer.valueOf(0);
    	
    	//Obtain the horizon connection, use horizon instead of search.
    	HorizonConnection connection = context.getHorizonConnection();

    	//get the first page of results
    	HObjectResultsPage page = connection.getPageOfHObjects(0, null, false); //0 = all objects
    	
    	//get the results from the results page
    	List<HObject> tmpList = page.getResults();
    	
    	//add number of results to the total that will be returned.
    	objectCount += tmpList.size();
    	
    	//process more result pages repeating operations above
    	while(page.moreResultsAvailable()) {
    		page = connection.getPageOfHObjects(0, page.getTokenForNextPage(), false); //0 = all objects
    		tmpList = page.getResults();
    		objectCount += tmpList.size();
    	}
		return objectCount;
    }

	/**
     * Closes the connection to Palantir
     */
	private void closeConnection() {
		if(context != null) {
			if(context.getInvestigationManager().getInvestigation() != null) {
				context.getInvestigationManager().closeInvestigation();
			}
		}
		if(client != null) {
			client.shutdown();
		}
	}
}
