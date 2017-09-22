//============================================================================**
// Imports
//============================================================================**

import java.io.*;
import java.util.HashMap;

//============================================================================**
// genetic_algorithm.ConfigManager Class
//============================================================================**

public class ConfigManager
{
    //----------------------------------------------------------------------------**
    // Private member variables
    //----------------------------------------------------------------------------**

    private static final char COMMENT_INDICATOR = '#';
	private static final char ASSIGNMENT_INDICATOR = '=';
	private HashMap<String, String> mParameterMap;

    //============================================================================**
    // genetic_algorithm.ConfigManager()
    //============================================================================**

	public ConfigManager()
	{
		mParameterMap = new HashMap<>();
	}

    //============================================================================**
    // decodeFile()
    //============================================================================**

	void decodeFile(String fileName)
	{
		BufferedReader paramInput = null;

		try
		{
			paramInput = new BufferedReader(new FileReader(fileName));

			String line = paramInput.readLine();

			while(line != null)
			{
				// Ignore Comments
				if (line.charAt(0) != COMMENT_INDICATOR)
				{
					int equalSignIndex = line.indexOf(ASSIGNMENT_INDICATOR);

					if(equalSignIndex >= 0 && equalSignIndex != line.length())
					{
						String key = line.substring(0, equalSignIndex).trim();
						String value = line.substring(equalSignIndex + 1).trim();

						mParameterMap.put(key, value);
					}
				}

                line = paramInput.readLine();
			}
		}
		catch (IOException exception)
		{
			System.err.println(exception.getMessage());
		}

		if(paramInput != null)
		{
			try
			{
				paramInput.close();
			}
			catch (IOException exception)
			{
				System.err.println(exception.getMessage());
			}
		}
	}

    //============================================================================**
    // getIntParameter()
    //============================================================================**

    int getIntParameter(String key)
    {
        int returnValue = 0;

        try
        {
            if (mParameterMap.containsKey(key))
            {
                returnValue = Integer.parseInt(mParameterMap.get(key));
            }
        }
        catch (NumberFormatException exception)
        {
            // Ignore error
        }

        return returnValue;
    }

    //============================================================================**
    // setIntParameter()
    //============================================================================**

    public void setIntParameter(String key, int value)
    {
        mParameterMap.put(key, value + "");
    }

    //============================================================================**
    // getStringParameters()
    //============================================================================**

    String getStringParameter(String key)
    {
        String returnValue = "";

        try
        {
            if (mParameterMap.containsKey(key))
            {
                returnValue = mParameterMap.get(key);
            }
        }
        catch (NumberFormatException exception)
        {
            // Ignore error
        }

        return returnValue;
    }

    //============================================================================**
    // setStringParameter()
    //============================================================================**

    public void setStringParameter(String key, String value)
    {
        mParameterMap.put(key, value);
    }

    //============================================================================**
    // getCsvParameter()
    //============================================================================**

    String[] getCsvParameter(String key)
    {
        String[] returnValue = null;

        try
        {
            if (mParameterMap.containsKey(key))
            {
                returnValue = mParameterMap.get(key).split(",");

                for (int i = 0; i < returnValue.length; i++)
                {
                    returnValue[i] = returnValue[i].trim();
                }
            }
        }
        catch (NumberFormatException exception)
        {
            // Ignore error
        }

        return returnValue;
    }

    //============================================================================**
    // getDoubleParameter()
    //============================================================================**

    double getDoubleParameter(String key)
    {
        double returnValue = 0;

        try
        {
            if (mParameterMap.containsKey(key))
            {
                returnValue = Double.parseDouble(mParameterMap.get(key));
            }
        }
        catch (NumberFormatException exception)
        {
            // Ignore error
        }

        return returnValue;
    }

}