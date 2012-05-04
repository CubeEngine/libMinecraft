package de.cubeisland.libMinecraft.database;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a condition
 *
 * @author Phillip Schichtel
 */
public class Condition
{
    public final String condition;
    public final List<Object> params;
    
    public Condition(String condition, Object... params)
    {
        if (condition == null)
        {
            throw new IllegalArgumentException("condition must not be null!");
        }
        this.condition = condition;
        this.params = Arrays.asList(params);
    }
}
