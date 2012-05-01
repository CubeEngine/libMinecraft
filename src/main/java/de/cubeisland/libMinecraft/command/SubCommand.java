package de.cubeisland.libMinecraft.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * This represents an sub command.
 * You usually don't use this class directly.
 *
 * @author Phillip Schichtel
 */
public class SubCommand
{
    private final Object commandContainer;
    private final Method method;

    public final String name;
    public final String[] aliases;
    public final Permission permission;
    public final String usage;
    public final String description;

    protected SubCommand(Object commandContainer, Method method, String name, String[] aliases, Permission permission, String usage, String description)
    {
        if (commandContainer == null)
        {
            throw new IllegalArgumentException("The command container must not be null!");
        }
        if (method == null)
        {
            throw new IllegalArgumentException("The method must not be null!");
        }
        if (name == null)
        {
            throw new IllegalArgumentException("The name must not be null!");
        }
        if (aliases == null)
        {
            throw new IllegalArgumentException("The aliases must not be null!");
        }

        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 2 || paramTypes[0] != CommandSender.class || paramTypes[1] != Args.class)
        {
            throw new IllegalArgumentException("The methods signature is invalid!");
        }

        this.commandContainer = commandContainer;
        this.method = method;
        this.method.setAccessible(true);

        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
    }

    public boolean execute(CommandSender sender, Args args)
    {
        try
        {
            Object result = this.method.invoke(this.commandContainer, sender, args);
            if (result instanceof Boolean)
            {
                return ((Boolean)result).booleanValue();
            }
            return true;
        }
        catch (IllegalAccessException e)
        {}
        catch (IllegalArgumentException e)
        {}
        catch (InvocationTargetException e)
        {
            Throwable t = e.getCause();
            if (t instanceof CommandException)
            {
                throw (CommandException)t;
            }
            else
            {
                throw new CommandException("Internal error: " + t.getLocalizedMessage(), t);
            }
        }
        return true;
    }
}
