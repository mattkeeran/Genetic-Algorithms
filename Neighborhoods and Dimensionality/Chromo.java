
//============================================================================**
// Imports
//============================================================================**

import java.util.Random;

//============================================================================**
// genetic_algorithm.Chromo Class
//============================================================================**

class Chromo
{
    //----------------------------------------------------------------------------**
    // Private member variables
    //----------------------------------------------------------------------------**

	private int[][] mGeneList;
	private int mNumGenes;
    private int mGeneSize;
    private int mMinDnaValue;
    private int mMaxDnaValue;

	private double mRawFitness;
	private Random mRandomizer;

    //============================================================================**
    // genetic_algorithm.Chromo()
    //============================================================================**

	/**
	 * Constructor
	 * @param randomizer Random number generator
	 * @param numGenes Number of Genes in the Chromosome
	 * @param geneSize Size of the Genes
	 * @param minDnaValue Minimum value to be stored per gene value
     * @param maxDnaValue Maximum value to be stored per gene value
     */
    Chromo(Random randomizer, int numGenes, int geneSize, int minDnaValue, int maxDnaValue)
	{
		mRandomizer = randomizer;
        mNumGenes = numGenes;
        mGeneSize = geneSize;

        mMinDnaValue = minDnaValue;
        mMaxDnaValue = maxDnaValue;

		//  Set gene list to a sequence of random keys
		mGeneList = new int[mNumGenes][mGeneSize];

		if (mRandomizer != null)
		{
			for (int geneIndex = 0; geneIndex < mNumGenes; geneIndex++)
			{
				for (int dnaIndex = 0; dnaIndex < mGeneSize; dnaIndex++)
				{
					mGeneList[geneIndex][dnaIndex] = getRandomValueWithinBounds();
				}
			}
		}
	}

    //============================================================================**
    // copyDna()
    //============================================================================**

    void copyDna(Chromo other)
	{
		// Copy all genes from other
		for(int geneIndex = 0; geneIndex < mNumGenes; geneIndex++)
		{
			mGeneList[geneIndex] = other.mGeneList[geneIndex].clone();
		}
	}

    //============================================================================**
    // doMutation()
    //============================================================================**

	void doMutation(double mutationRate)
	{
        for (int geneIndex = 0; geneIndex < mNumGenes; geneIndex++)
        {
            for(int dnaIndex = 0; dnaIndex < mGeneSize; dnaIndex++)
            {
                if (mRandomizer.nextDouble() < mutationRate)
                {
                    // Flip the DNA value
					mGeneList[geneIndex][dnaIndex] = getRandomValueWithinBounds();
                }
            }
        }
	}

	/**
	 * Checks equality of Chromos
	 * @return True if the Chromos are equal; false otherwise
     */
	public boolean equals(Object other)
	{
		boolean isEqual = false;

		if(other != null && other instanceof Chromo)
		{
            isEqual = toString().equals(other.toString());
		}

		return isEqual;
	}

    //============================================================================**
    // getGeneList()
    //============================================================================**

	int[][] getGeneList()
	{
		return mGeneList;
	}

    //============================================================================**
    // getGeneSize()
    //============================================================================**

	int getGeneSize()
	{
		return mGeneSize;
	}

    //============================================================================**
    // getNumGenes()
    //============================================================================**

	int getNumGenes()
	{
		return mNumGenes;
	}

    //============================================================================**
    // getRandomValueWithinBounds()
    //============================================================================**

    private int getRandomValueWithinBounds()
    {
        return mMinDnaValue + mRandomizer.nextInt(mMaxDnaValue - mMinDnaValue + 1);
    }

    //============================================================================**
    // getRawFitness()
    //============================================================================**

	double getRawFitness()
	{
		return mRawFitness;
	}

    //============================================================================**
    // performOnPointCrossOver()
    //============================================================================**

	void performOnePointCrossover(Chromo parentA, Chromo parentB, int crossOverGeneIndex, int crossOverDnaIndex)
	{
		// Take the genes before the cross over point from parent A
		for(int geneIndex = 0; geneIndex < crossOverGeneIndex; geneIndex++)
		{
			mGeneList[geneIndex] = parentA.mGeneList[geneIndex].clone();
		}

		// Take the DNA before the DNA cross over point in the cross over gene from parent A
		for(int dnaIndex = 0; dnaIndex < crossOverDnaIndex; dnaIndex++)
		{
			mGeneList[crossOverGeneIndex][dnaIndex] = parentA.mGeneList[crossOverGeneIndex][dnaIndex];
		}

		// Take the DNA at and after the DNA cross over in the cross over gene from parent B
		for(int dnaIndex = crossOverDnaIndex; dnaIndex < mGeneSize; dnaIndex++)
		{
			mGeneList[crossOverGeneIndex][dnaIndex] = parentB.mGeneList[crossOverGeneIndex][dnaIndex];
		}

		// Take the rest of the genes from parent B
		for(int geneIndex = crossOverGeneIndex; geneIndex < mNumGenes; geneIndex++)
		{
			mGeneList[geneIndex] = parentB.mGeneList[geneIndex].clone();
		}
	}

    //============================================================================**
    // setRawFitness()
    //============================================================================**

	void setRawFitness(double fitness)
	{
		mRawFitness = fitness;
	}

    //============================================================================**
    // toString()
    //============================================================================**

    public String toString()
    {
        String stringRepresentation = "";

        for (int geneIndex = 0; geneIndex < mNumGenes; geneIndex++)
        {
            for (int dnaIndex = 0; dnaIndex < mGeneSize; dnaIndex++)
            {
                stringRepresentation += (mGeneList[geneIndex][dnaIndex] + " ");
            }
        }

        return stringRepresentation;
    }

}
