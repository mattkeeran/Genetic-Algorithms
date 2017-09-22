
/**
 * Imports
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class is a representation of a population of individuals in an n-dimensional cube.
 */
public class Population
{
    /*
     * Private constants
     */

    private static final String NO_CROSSOVER = "NO_CROSSOVER";

    private static final String NO_MUTATION = "NO_MUTATION";

    private static final String COMPACT_NEIGHBORHOOD = "COMPACT";
    private static final String LINEAR_NEIGHBORHOOD = "LINEAR";

    private static final String FITNESS_PROPORTIONAL_SELECTION = "FITNESS_PROPORTIONAL";
    private static final String LINEAR_RANKING_SELECTION = "LINEAR_RANKING";

    /*
     * Private Member Variables.
     */

    private final int mNumDimensions;
    private final int mNumIndividualsPerDimension;
    private final int mNumIndividualsTotal;

    private final String mNeighborhoodType;
    private final int mNeighborhoodRadius;

    private final int mNumGenes;
    private final int mGeneSize;
    private final int mMinDnaValue;
    private final int mMaxDnaValue;
    private final Random mRandomizer;
    private Chromo[] mIndividuals;

    private String mCrossOverType;
    private double mCrossOverRate;

    private String mMutationType;
    private double mMutationRate;

    private String mSelectionType;

    private ArrayList<ArrayList<Integer>> mNeighborhoodsList;

    /**
     * Constructor
     * @param randomizer Random number generator
     * @param configManager Config Manager for the project
     */
    public Population(Random randomizer, ConfigManager configManager)
    {
        mRandomizer = randomizer;
        mNumIndividualsPerDimension = configManager.getIntParameter("PopulationDimensionSize");
        mNumDimensions = configManager.getIntParameter("NumPopulationDimensions");

        mNeighborhoodType = configManager.getStringParameter("NeighborhoodType");
        mNeighborhoodRadius = configManager.getIntParameter("NeighborhoodRadius");

        mNumGenes = configManager.getIntParameter("NumGenes");
        mGeneSize = configManager.getIntParameter("GeneSize");
        mMinDnaValue = configManager.getIntParameter("MinDnaValue");
        mMaxDnaValue = configManager.getIntParameter("MaxDnaValue");

        mMutationType = configManager.getStringParameter("MutationType");
        mMutationRate = configManager.getDoubleParameter("MutationRate");

        mCrossOverRate = configManager.getDoubleParameter("CrossOverRate");
        mCrossOverType = configManager.getStringParameter("CrossOverType");
        mSelectionType = configManager.getStringParameter("SelectionType");

        /**
         * Number of individuals should be equal to the number of individuals per dimension to the nth power where n is
         * the number of dimensions.
         */
        mNumIndividualsTotal = (int)Math.pow(mNumIndividualsPerDimension, mNumDimensions);
        mIndividuals = new Chromo[mNumIndividualsTotal];

        for(int i = 0; i < mIndividuals.length; i++)
        {
            mIndividuals[i] = new Chromo(mRandomizer, mNumGenes, mGeneSize, mMinDnaValue, mMaxDnaValue);
        }

        //Set neighborhoods
        mNeighborhoodsList = new ArrayList<>();
        for(int i = 0; i < mIndividuals.length; i++)
        {
            mNeighborhoodsList.add(getNeighborhood(i));
        }
    }

    /**
     * Calculates each member's fitness using a Fitness Function.
     * @param function The Fitness Function by which an individual's fitness will be calculated
     * @param statistics The Run Statistics with which to record solutions.
     */
    public void calculationFitnessByFunction(FitnessFunction function, RunStatistics statistics, int generationNum,
        int runNum)
    {
        for(Chromo chromo : mIndividuals)
        {
            function.doRawFitness(chromo);
            statistics.recordSolution(generationNum, runNum, chromo);
        }
    }

    /**
     * Gets the neighborhood of the given individual index. Helper function for the recursive implementation.
     * TODO: Consider saving the neighborhoodList for future uses so this neighborhood is not calculated many times.
     * @param index The index for which a neighborhood should be found.
     * @return An array of individuals within the index's neighborhood
     */
    private ArrayList<Integer> getNeighborhood(int index)
    {
        ArrayList<Integer> neighborhoodLocationIndex = new ArrayList<>();
        int[] initialDimensionalPosition = indexToDimensionalPosition(index);
        getNeighborhood(initialDimensionalPosition, initialDimensionalPosition, neighborhoodLocationIndex);

        return neighborhoodLocationIndex;
    }

    /**
     * Gets the neighborhood of the given individual index.
     * @param initPosition
     * @param position
     * @param neighborhoodList
     */
    public void getNeighborhood(int[] initPosition, int[] position, ArrayList<Integer> neighborhoodList)
    {
        // If the current position is not already in the neighborhood list and it is within the neighborhood...
        //System.out.println(position[0] + " " + position[1]);
        if(!listContains(neighborhoodList, dimensionalPositionToIndex(position)) && isWithinNeighborhood(initPosition, position))
        {
            // Add the current position to the neighborhood
            neighborhoodList.add(dimensionalPositionToIndex(position));

            // Perform the same check recursively for its immediate neighbors in all dimensions
            for (int dimension = 0; dimension < mNumDimensions; dimension++)
            {
                // Check immediate neighbors using an offset in the current dimension
                for(int offset = -1; offset <= 1; offset++)
                {
                    int[] neighbor = position.clone();
                    neighbor[dimension] += offset;
                    getNeighborhood(initPosition, neighbor, neighborhoodList);
                }
            }
        }
    }

    /**
    *
    *If a number's out of bounds moves it to its correct spot
    */

    public void boundaryCheck(int [] position){

        for(int i=0; i<mNumDimensions; i++){

            if(position[i] < 0)
                position[i] += mNumIndividualsPerDimension;
            if(position[i] >= mNumIndividualsPerDimension)
                position[i] -= mNumIndividualsPerDimension;

        }


    }

    public static boolean listContains(ArrayList<Integer> locationList, int potentialLocation)
    {
        for(int location : locationList)
        {
            if(location == potentialLocation)
            {
                return true;
            }
        }

        return false;
    }

    private boolean isWithinNeighborhood(int[] initPosition, int[] position)
    {
        boolean isWithinNeighborhood = true;

        if(isWithinNeighborhood)
        {
            if (mNeighborhoodType.equals(COMPACT_NEIGHBORHOOD))
            {
                isWithinNeighborhood = isWithinCompactNeighborhood(initPosition, position);
            }
            else if (mNeighborhoodType.equals(LINEAR_NEIGHBORHOOD))
            {
                isWithinNeighborhood = isWithinLinearNeighborhood(initPosition, position);
            }
        }

        return isWithinNeighborhood;
    }

    //Needs to be able to check distance over edges, maybe normal than also dist of point to edges combined
    private boolean isWithinCompactNeighborhood(int[] initPosition, int[] position)
    {
        // Gather the sum of squared differences in each dimension
        int sumOfSquaredDifferences = 0;

        for(int dimension = 0; dimension < mNumDimensions; dimension++)
        {
            sumOfSquaredDifferences += (initPosition[dimension] - position[dimension]) *
                (initPosition[dimension] - position[dimension]);
        }

        // Compare the square of the neighborhood radius to the sum of squared differences
        return sumOfSquaredDifferences <= mNeighborhoodRadius * mNeighborhoodRadius;
    }

    //Same problem as above
    private boolean isWithinLinearNeighborhood(int[] initPosition, int[] position)
    {
        // Ensure up to one dimension of the position is different from the initial position
        int differentDimension = -1;

        for(int dimension = 0; dimension < mNumDimensions; dimension++)
        {
            if(initPosition[dimension] != position[dimension])
            {
                if(differentDimension < 0)
                {
                    differentDimension = dimension;
                }
                else
                {
                    // The different dimension has already been found (Not in a linear neighborhood)
                    return false;
                }
            }
        }

        // If there is no different dimension or the difference is less than or equal to the neighborhood radius,
        // the location is in the neighborhood.
        if(differentDimension < 0 ||
            Math.abs(initPosition[differentDimension] - position[differentDimension]) <= mNeighborhoodRadius)
        {
            return true;
        }

        return false;
    }

    /**
     * Returns the dimensional position from the given index.
     * @param index The index from which a dimensional position should be created
     * @return The dimensional position of the given index
     */
    public int[] indexToDimensionalPosition(int index)
    {
        // Dimensional position is represented by n dimensions where n is the number of dimensions defined.
        int[] dimensionalPosition = new int[mNumDimensions];

        // For each dimension, determine the index's dimensional position starting from the least significant dimension.
        for(int dimension = mNumDimensions - 1; dimension >= 0; dimension--)
        {
            // The dimensional position of the current dimension is the index mod the number of individuals per
            // dimension.
            dimensionalPosition[dimension] = index % mNumIndividualsPerDimension;
            // Divide the index by the number of individuals per dimension for the next dimensional position.
            index /= mNumIndividualsPerDimension;
        }

        return dimensionalPosition;
    }

    public int dimensionalPositionToIndex(int[] dimensionalPosition)
    {
        int[] clone = dimensionalPosition.clone();
        boundaryCheck(clone);

        int index = 0;

        for(int dimension = 0; dimension < mNumDimensions; dimension++)
        {
            index += clone[dimension] *
                ((int) Math.pow(mNumIndividualsPerDimension, mNumDimensions - dimension - 1));
        }

        return index;
    }

    /**
     * Performs reproduction by creating a new genetic_algorithm.Population and filling in members by looking at the previous genetic_algorithm.Population.
     * Can be considered to being done as "batch"
     */
    public void performReproduction()
    {
        Chromo[] newPopulation = new Chromo[mNumIndividualsTotal];

        for (int childIndex = 0; childIndex < mIndividuals.length; childIndex++)
        {
            /**
             * Because this section of the Homework does not require mutation or crossover, this has been broken
             * temporarily. TODO: Fix crossover/mutation
             */

            Chromo parent = selectParent(childIndex);

            // Do Crossover
            // TODO: Fix crossover
            if(!mCrossOverType.equalsIgnoreCase(NO_CROSSOVER))
            {
                System.err.println("Invalid Cross Over type: " + mCrossOverType + ". No cross over performed.");
            }

            // Perform mutation
            // TODO: Fix Mutation
            if (!mMutationType.equalsIgnoreCase(NO_MUTATION))
            {
                System.err.println("Invalid Mutation Type: " + mMutationType + ". No mutation performed.");
            }

            // Add the child to the new population
            newPopulation[childIndex] = parent;
        }

        mIndividuals = newPopulation;
    }

    /**
     * Selects a parent from the individual's neighborhood for reproduction.
     * @param individualIndex The index in the raw array for which a parent is being selected.
     * @return
     */
    private Chromo selectParent(int individualIndex)
    {
        Chromo parent = null;

        ArrayList<Integer> localNeighborhoodIndecies = mNeighborhoodsList.get(individualIndex);
        int neighborhoodSize = localNeighborhoodIndecies.size();


        Chromo[] neighborhood = new Chromo[neighborhoodSize];
        for(int i = 0; i < neighborhoodSize; i++)
            neighborhood[i] = mIndividuals[localNeighborhoodIndecies.get(i)];

        if(mSelectionType.equalsIgnoreCase(FITNESS_PROPORTIONAL_SELECTION))
        {
            parent = selectParentFitnessProportional(neighborhood);
        }
        else if(mSelectionType.equalsIgnoreCase(LINEAR_RANKING_SELECTION))
        {
            parent = selectParentLinearRanking(neighborhood);
        }
        else
        {
            System.err.println("Unsupported Selection Type: " + mSelectionType + ". Parent is set to null.");
        }

        return parent;
    }

    /**
     * Selects a parent from the individual's neighborhood for reproduction using Linear Ranking Selection.
     * @param neighborhood The neighborhood from which to select a parent
     */
    private Chromo selectParentLinearRanking(Chromo[] neighborhood)
    {
        // Roll a random number between 0 and 1
        double randomNumber = mRandomizer.nextDouble();

        // Sort the neighborhood by fitness
        Arrays.sort(neighborhood, new FitnessComparator());

        for(int index = 1; index <= neighborhood.length; index++)
        {
            if(randomNumber < (1.0 / (double)(index + 1)))
            {
                return neighborhood[index - 1];
            }

            randomNumber -= 1 / (double)(index + 1);
        }

        return neighborhood[0];
    }

    /**
     * Selects a parent from the individual's neighborhood for reproduction using Fitness Proportional Selection.
     * @param neighborhood The neighborhood from which to select a parent
     */
    private Chromo selectParentFitnessProportional(Chromo[] neighborhood)
    {
        // Calculate the sum of fitnesses from all the chromosomes in the neighborhood
        int sumFitness = 0;

        for(int index = 0; index < neighborhood.length; index++)
        {
            sumFitness += neighborhood[index].getRawFitness();
        }

        // Roll a random number between 0 and the sum to choose a chromosome weighted by its fitness
        int randomNumber = mRandomizer.nextInt(sumFitness);
        int currentFitness = 0;

        for(int index = 0; index < neighborhood.length; index++)
        {
            currentFitness += neighborhood[index].getRawFitness();
            if(currentFitness >= randomNumber)
            {
                return neighborhood[index];
            }
        }

        return neighborhood[0];
    }
}
