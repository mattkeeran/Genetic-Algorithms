

//============================================================================**
// Imports
//============================================================================**

import java.util.*;

//============================================================================**
// genetic_algorithm.Search Class
//============================================================================**

public class Search
{
    //============================================================================**
    // main()
    // Description: Entry point of the program.
    //============================================================================**

    public static void main(String[] args) throws java.io.IOException
	{
		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

		//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		ConfigManager params = new ConfigManager();
		params.decodeFile(args[0]);
		
		GeneticAlgorithm algorithm = new GeneticAlgorithm(params);
		
		// Setup algorithm
		algorithm.setup();
		
		// Run algorithm
		algorithm.begin();

		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
	} 

} 

