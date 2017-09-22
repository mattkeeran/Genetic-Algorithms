//============================================================================**
// genetic_algorithm.DecimalValueFitnessFunction
// Description: This class serves as a simple fitness function that
// determines the fitness of a chromosome by computing the sum of the decimal
// value represented by the chromosome's genes.
//============================================================================**

public class DecimalValueFitnessFunction extends FitnessFunction
{
    //============================================================================**
    // genetic_algorithm.DecimalValueFitnessFunction()
    //============================================================================**

    public DecimalValueFitnessFunction()
    {
        super("Decimal Value");
    }

    //============================================================================**
    // doRawFitness()
    // Description: Simply calculates the sum of all the dna in every gene.
    //============================================================================**

    @Override
    public void doRawFitness(Chromo X)
    {
        int sumFitness = 0;

        for (int geneIndex = 0; geneIndex < X.getNumGenes(); geneIndex++)
        {
            for (int dnaIndex = 0; dnaIndex < X.getGeneSize(); dnaIndex++)
            {
                sumFitness += X.getGeneList()[geneIndex][dnaIndex];
            }
        }

        X.setRawFitness(sumFitness);
    }
}
