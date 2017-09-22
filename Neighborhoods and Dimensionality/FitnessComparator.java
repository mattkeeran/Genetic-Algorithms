

import java.util.Comparator;

public class FitnessComparator implements Comparator<Chromo>
{
    public FitnessComparator()
    {
    }

    @Override
    public int compare(Chromo o1, Chromo o2)
    {
        return (int)(o2.getRawFitness() - o1.getRawFitness());
    }

}