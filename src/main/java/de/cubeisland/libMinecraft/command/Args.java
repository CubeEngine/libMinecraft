package de.cubeisland.libMinecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Phillip Schichtel
 * @author Faithcaio
 */
public class Args
{
    private final BaseCommand baseCommand;
    private final SubCommand subCommand;
    private final String baseLabel;
    private final String label;
    private final List<String> flags;
    private final Map<String, String> params;
    private final boolean empty;
    private final int size;

    /**
     * Initializes the Args object with an array of arguments
     *
     * @param args the arguments
     * @throws IllegalArgumentException if the args array is empty
     */
    public Args(BaseCommand baseCommand, String baseLabel, SubCommand subCommand, String[] args)
    {
        this.baseCommand = baseCommand;
        this.subCommand = subCommand;
        this.baseLabel = baseLabel;
        this.flags = new ArrayList<String>();
        this.params = new HashMap<String, String>();

        if (args.length > 0)
        {
            this.label = args[0];
            String name;
            for (int i = 1; i < args.length; ++i)
            {
                if (args[i].charAt(0) == '-')
                {
                    name = args[i].substring(1);
                    if (i + 1 < args.length)
                    {
                        this.params.put(name, args[++i]);
                    }
                    else
                    {
                        this.flags.add(name);
                    }
                }
                else
                {
                    this.flags.add(args[i]);
                }
            }
        }
        else
        {
            throw new IllegalArgumentException("There need to be at least 1 argument!");
        }
        this.empty = (this.flags.isEmpty() && this.params.isEmpty());
        this.size = this.flags.size() + this.params.size();
    }

    /**
     * Checks whether there are arguments
     *
     * @return true if empty
     */
    public boolean isEmpty()
    {
        return this.empty;
    }

    /**
     * Returns the number of arguments
     *
     * @return the numbers of arguments
     */
    public int size()
    {
        return this.size;
    }

    /**
     * Returns the base command
     *
     * @return the label
     */
    public BaseCommand getBaseCommand()
    {
        return this.baseCommand;
    }

    /**
     * Returns the executed sub command object
     *
     * @return the label
     */
    public SubCommand getSubCommand()
    {
        return this.subCommand;
    }

    /**
     * Returns the label of the command
     *
     * @return the label
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * Returns the label of the base command
     *
     * @return the label
     */
    public String getBaseLabel()
    {
        return this.baseLabel;
    }

    /**
     * Checks whether the given flag exists
     *
     * @param flag the flag name
     * @return true if it exists
     */
    public boolean hasFlag(String flag)
    {
        return this.flags.contains(flag);
    }

    /**
     * Checks whether all the given flags exist
     *
     * @param flags the flags to check
     * @return true if all flags exist
     */
    public boolean hasFlags(String... flags)
    {
        for (String flag : flags)
        {
            if (!this.hasFlag(flag))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given parameter exists
     *
     * @param param the parameter name
     * @return true if it exists
     */
    public boolean hasParam(String param)
    {
        return this.params.containsKey(param);
    }

    /**
     * Checks whether all the given params exist
     *
     * @param params the params to check
     * @return true if all params exist
     */
    public boolean hasParams(String... params)
    {
        for (String param : params)
        {
            if (!this.hasParam(param))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the requested value as a String
     *
     * @param i the index of the flag
     * @return the value as String or null
     */
    public String getString(int i)
    {
        return this.getString(i, null);
    }

    /**
     * Returns the requested value as a String
     *
     * @param i the index of the flag
     * @param def the default value
     * @return the value as String or the given default value
     */
    public String getString(int i, String def)
    {
        if (i >= 0 && this.flags.size() > i)
        {
            return this.flags.get(i);
        }
        return def;
    }

    /**
     * Returns the requested value as a String
     *
     * @param param the name of the param
     * @return the value as String or null
     */
    public String getString(String param)
    {
        return this.getString(param, null);
    }

    /**
     * Returns the requested value as a String
     *
     * @param param the name of the param
     * @param def the default value
     * @return the value as String or the given default value
     */
    public String getString(String param, String def)
    {
        if (this.params.containsKey(param))
        {
            return this.params.get(param);
        }
        return def;
    }

    /**
     * Returns the requested value as an Integer
     *
     * @param i the index of the flag
     * @return the value as Integer or null
     */
    public Integer getInt(int flag)
    {
        return this.getInt(flag, null);
    }

    /**
     * Returns the requested value as an Integer or the given default value
     *
     * @param i the index of the flag
     * @param def the default value
     * @return the value as Integer or the default value
     */
    public Integer getInt(int flag, Integer def)
    {
        String value = this.getString(flag);
        if (value != null)
        {
            try
            {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as an Integer
     *
     * @param param the name of the parameter
     * @return the value as Integer or null
     */
    public Integer getInt(String param)
    {
        return this.getInt(param, null);
    }

    /**
     * Returns the requested value as an Integer or the given default value
     *
     * @param param the name of the parameter
     * @param def the default value
     * @return the value as Integer or the default value
     */
    public Integer getInt(String param, Integer def)
    {
        String value = this.getString(param);
        if (value != null)
        {
            try
            {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as a Double
     *
     * @param i the index of the flag
     * @return the value as Double or null
     */
    public Double getDouble(int flag)
    {
        return this.getDouble(flag, null);
    }

    /**
     * Returns the requested value as a Double or the given default value
     *
     * @param i the index of the flag
     * @param def the default value
     * @return the value as Double or the default value
     */
    public Double getDouble(int flag, Double def)
    {
        String value = this.getString(flag);
        if (value != null)
        {
            try
            {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as a Double
     *
     * @param param the name of the parameter
     * @param def the default value
     * @return the value as Double or null
     */
    public Double getDouble(String param)
    {
        return this.getDouble(param, null);
    }

    /**
     * Returns the requested value as a Double or the given default value
     *
     * @param param the name of the parameter
     * @param def the default value
     * @return the value as Double or the default value
     */
    public Double getDouble(String param, Double def)
    {
        String value = this.getString(param);
        if (value != null)
        {
            try
            {
                return Double.parseDouble(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as a Long
     *
     * @param i the index of the flag
     * @return the value as Long or null
     */
    public Long getLong(int flag)
    {
        return this.getLong(flag, null);
    }

    /**
     * Returns the requested value as a Long or the given default value
     *
     * @param i the index of the flag
     * @param def the default value
     * @return the value as Long or the default value
     */
    public Long getLong(int flag, Long def)
    {
        String value = this.getString(flag);
        if (value != null)
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as a Long
     *
     * @param param the name of the parameter
     * @param def the default value
     * @return the value as Long or null
     */
    public Long getLong(String param)
    {
        return this.getLong(param, null);
    }

    /**
     * Returns the requested value as a Long or the given default value
     *
     * @param param the name of the parameter
     * @param def the default value
     * @return the value as Long or the default value
     */
    public Long getLong(String param, Long def)
    {
        String value = this.getString(param);
        if (value != null)
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (NumberFormatException e)
            {}
        }
        return def;
    }

    /**
     * Returns the requested value as a boolean
     *
     * enable --> true
     * true --> true
     * yes --> true
     * on --> true
     * 1 --> true
     *
     * everything else --> false
     *
     * @param i the index of the flag
     * @return true or false
     */
    public boolean getBoolean(int flag)
    {
        return parseBoolean(this.getString(flag));
    }

    /**
     * Returns the requested value as a boolean
     *
     * @param param the name of the parameter
     * @return true or false
     */
    public boolean getBoolean(String param)
    {
        return parseBoolean(this.getString(param));
    }

    private static boolean parseBoolean(String string)
    {
        if ("true".equalsIgnoreCase(string) || "yes".equalsIgnoreCase(string) || "on".equalsIgnoreCase(string) || "1".equals(string) || "enable".equalsIgnoreCase(string))
        {
            return true;
        }
        return false;
    }
}
