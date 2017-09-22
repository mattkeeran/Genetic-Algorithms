

//============================================================================**
// RunStatistics Class
//============================================================================**

public class RunStatistics
{
	//----------------------------------------------------------------------------**
	// Private member variables
	//----------------------------------------------------------------------------**

	private Chromo mBestOfGenChromo;

    private int mNumPopulation;
	private int mNumBestFitnessInPopulation;

	//============================================================================**
	// RunStatistics()
	//============================================================================**

	public RunStatistics()
	{
	}

	//============================================================================**
	// recordSolution()
	//============================================================================**

	public void recordSolution(int generationNum, int runNum, Chromo solution)
	{
		Chromo clone = new Chromo(null, solution.getNumGenes(), solution.getGeneSize(), 0, 0);
		clone.copyDna(solution);
        clone.setRawFitness(solution.getRawFitness());

        mNumPopulation++;

		// Check if its the best of the generation.
		if(mBestOfGenChromo == null ||
			clone.getRawFitness() >= mBestOfGenChromo.getRawFitness())
		{
            if(clone.equals(mBestOfGenChromo))
            {
                mNumBestFitnessInPopulation++;
            }
            else
            {
                mNumBestFitnessInPopulation = 1;
            }

			mBestOfGenChromo = clone;
		}
	}

	//============================================================================**
	// printGeneration()
	//============================================================================**

	public void printGeneration()
	{
		System.out.println(mNumBestFitnessInPopulation / (double)mNumPopulation);

		resetGeneration();
	}

	//============================================================================**
	// resetGeneration()
	//============================================================================**

	private void resetGeneration()
	{
        mNumPopulation = 0;
        mNumBestFitnessInPopulation = 0;

		mBestOfGenChromo = null;
	}
}
