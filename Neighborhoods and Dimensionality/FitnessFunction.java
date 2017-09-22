//============================================================================**
// genetic_algorithm.FitnessFunction Class
// Description: This class is meant to be the base class for any fitness
// function that could be utilized within the GA.
//============================================================================**

public abstract class FitnessFunction
{
    //----------------------------------------------------------------------------**
    // Private member variables
    //----------------------------------------------------------------------------**

    private String mName;

    //============================================================================**
    // genetic_algorithm.FitnessFunction()
    //============================================================================**

    public FitnessFunction(String name)
	{
		mName = name;
		System.out.println("Setting up Fitness Function....." + mName);
	}

    //============================================================================**
    // doRawFitness()
    // Description: Calculates and sets the raw fitness of the genetic_algorithm.Chromo parameter X.
    //============================================================================**

	public abstract void doRawFitness(Chromo X);
}

