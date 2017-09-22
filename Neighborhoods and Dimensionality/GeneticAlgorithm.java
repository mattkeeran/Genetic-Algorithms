//============================================================================**
// Imports
//============================================================================**

import java.util.Random;

//============================================================================**
// genetic_algorithm.GeneticAlgorithm Class
//============================================================================**

class GeneticAlgorithm
{
    //----------------------------------------------------------------------------**
    // Private member variables
    //----------------------------------------------------------------------------**

	private ConfigManager mParams;
	private Random mRandomizer;

	private Population mPopulation;
    private int mNumRuns;
    private int mNumGenerations;

	private FitnessFunction mFitnessFunction;
	private RunStatistics mStatistics;

    //============================================================================**
    // genetic_algorithm.GeneticAlgorithm()
    //============================================================================**

    GeneticAlgorithm(ConfigManager params)
	{
		mParams = params;
	}

    //============================================================================**
    // setup()
    // Description: Initializes the Genetic Algorithm by initializing parameters
    // and Run Statistics
    //============================================================================**

    void setup()
	{
		mRandomizer = new Random(mParams.getIntParameter("RandomSeed"));
        mNumRuns = mParams.getIntParameter("NumRuns");
        mNumGenerations = mParams.getIntParameter("NumGenerations");

        String problemType = mParams.getStringParameter("ProblemType");

		mPopulation = new Population(mRandomizer, mParams);

		if (problemType.equals("Topology"))
		{
			mFitnessFunction = new DecimalValueFitnessFunction();
		}
		else
		{
			System.err.println("Invalid problem Type: " + problemType);
		}

		mStatistics = new RunStatistics();
	}

    //============================================================================**
    // begin()
    // Description: Run loop of the Genetic Algorithm.
    //============================================================================**

    void begin()
	{
		// Do regular runs
		for (int runNum = 1; runNum <= mNumRuns; runNum++)
		{
			// Perform a single run with new chromos
			performRun(mPopulation, runNum, true);
		}
	}

    //============================================================================**
    // performGeneration()
    //============================================================================**

    private void performGeneration(Population population, int generationNum, int runNum)
	{
		// Calculate the fitness of the population
		population.calculationFitnessByFunction(mFitnessFunction, mStatistics, generationNum, runNum);

		// Reproduction
		population.performReproduction();
	}

    //============================================================================**
    // performRun()
    //============================================================================**

    private void performRun(Population population, int runNum, boolean initChromos)
	{
		if(initChromos)
		{
			mPopulation = new Population(mRandomizer, mParams);
		}

		// Perform generations
		for (int generationNum = 1; generationNum <= mNumGenerations; generationNum++)
		{
			performGeneration(population, generationNum, runNum);

			mStatistics.printGeneration();
		}
	}
}
