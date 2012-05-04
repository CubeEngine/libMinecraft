package de.cubeisland.libMinecraft.command;

import de.cubeisland.libMinecraft.ChatColor;
import de.cubeisland.libMinecraft.translation.TranslatablePlugin;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * This represents a base command
 *
 * @author Phillip Schichtel
 */
public class BaseCommand implements CommandExecutor
{
    private final Plugin plugin;
    private final PluginManager pm;
    private final Map<Object, Set<String>> objectCommandMap;
    private final Map<String, SubCommand> commands;
    private final Map<String, String> aliases;
    private String defaultCommand;

    private final Permission parentPermission;
    private final String permissionBase;

    public BaseCommand(Plugin plugin, Permission parentPermission)
    {
        this(plugin, parentPermission, null);
    }

    public BaseCommand(Plugin plugin, Permission parentPermission, String permissionBase)
    {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();
        this.objectCommandMap = new HashMap<Object, Set<String>>();
        this.commands = new HashMap<String, SubCommand>();
        this.aliases = new HashMap<String, String>();

        this.registerCommands(this);
        this.defaultCommand = "help";

        this.parentPermission = parentPermission;
        this.registerPermission(parentPermission);

        this.permissionBase = permissionBase;
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
    {
        SubCommand subCommand;
        if (args.length > 0)
        {
            subCommand = this.getCommand(args[0]);
        }
        else
        {
            subCommand = this.getCommandByName(this.defaultCommand);
            args = new String[] {subCommand.getName()};
        }

        if (subCommand != null)
        {
            Permission permission = subCommand.getPermission();
            if (permission != null && !sender.hasPermission(permission))
            {
                sender.sendMessage(this.getTranslation("command_permdenied", "Permission denied!"));
            }
            else
            {
                try
                {
                    return subCommand.execute(sender, new CommandArgs(this, label, subCommand, args));
                }
                catch (CommandException e)
                {
                    sender.sendMessage(e.getLocalizedMessage());
                }
                catch (Throwable t)
                {
                    sender.sendMessage(this.getTranslation("command_internalerror", "&4An internal error occurred!"));
                    t.printStackTrace(System.err);
                }
            }
        }
        else
        {
            sender.sendMessage(this.getTranslation("command_notfound", "Command not found!"));
        }

        return true;
    }

    public BaseCommand setDefaultCommand(String name)
    {
        SubCommand command = this.getCommand(name);
        if (command != null)
        {
            this.defaultCommand = command.getName();
        }
        return this;
    }

    public SubCommand getDefaultCommand()
    {
        return this.getCommandByName(this.defaultCommand);
    }

    public Permission getParentPermission()
    {
        return this.parentPermission;
    }

    public Plugin getPlugin()
    {
        return this.plugin;
    }

    public final BaseCommand registerCommands(Object commandContainer)
    {
        if (commandContainer == null)
        {
            throw new IllegalArgumentException("The command container must not be null!");
        }
        
        try
        {
            Set<String> registeredCommands = new HashSet<String>();
            Command annotation;
            CommandPermission permissionAnnotation;
            String name;
            Permission permission;
            
            for (Method method : commandContainer.getClass().getMethods())
            {
                annotation = method.getAnnotation(Command.class);
                if (annotation != null)
                {
                    name = annotation.name();
                    if ("".equals(name))
                    {
                        name = method.getName();
                    }
                    name = name.toLowerCase();
                    permission = null;
                    boolean addPermissionParent = false;
                    permissionAnnotation = method.getAnnotation(CommandPermission.class);
                    if (permissionAnnotation != null)
                    {
                        String permissionName = permissionAnnotation.value();
                        if (permissionName.length() == 0 && this.permissionBase != null)
                        {
                            permissionName = this.permissionBase + name;
                        }
                        if (permissionName.length() > 0)
                        {
                            permission = new Permission(permissionName, permissionAnnotation.def());
                            addPermissionParent = permissionAnnotation.addParent();
                            this.registerPermission(permission);
                            if (this.parentPermission != null && addPermissionParent)
                            {
                                permission.addParent(this.parentPermission, true);
                            }
                        }
                    }
                    try
                    {
                        this.commands.put(name, new SubCommand(commandContainer, method, name, annotation.aliases(), permission, addPermissionParent, annotation.usage(), annotation.desc()));
                        registeredCommands.add(name);

                        for (String alias : annotation.aliases())
                        {
                            this.aliases.put(alias, name);
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        e.printStackTrace(System.err);
                    }
                }
            }

            this.objectCommandMap.put(commandContainer, registeredCommands);
        }
        catch (Throwable t)
        {}
        
        return this;
    }

    public BaseCommand unregisterCommands(Object commandContainer)
    {
        Set<String> commandsToRemove = this.objectCommandMap.get(commandContainer);
        if (commandsToRemove != null)
        {
            for (String command : commandsToRemove)
            {
                this.unregisterCommand(command);
            }
        }

        return this;
    }

    public BaseCommand unregisterCommand(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("the name must not be null!");
        }

        this.commands.remove(name);
        for (Map.Entry<String, String> entry : this.aliases.entrySet())
        {
            if (name.equals(entry.getValue()))
            {
                this.aliases.remove(entry.getKey());
            }
        }
        return this;
    }

    public BaseCommand unregisterCommand(SubCommand command)
    {
        if (command == null)
        {
            throw new IllegalArgumentException("the command must not be null!");
        }
        this.unregisterCommand(command.getName());
        return this;
    }

    public Collection<SubCommand> getAllSubCommands()
    {
        return this.commands.values();
    }

    public SubCommand getCommandByName(String name)
    {
        if (name == null)
        {
            return null;
        }
        return this.commands.get(name.toLowerCase());
    }

    public SubCommand getCommand(String label)
    {
        if (label == null)
        {
            return null;
        }
        label = label.toLowerCase();
        String name = this.aliases.get(label);
        if (name != null)
        {
            return this.getCommandByName(name);
        }
        else
        {
            return this.getCommandByName(label);
        }
    }

    public BaseCommand clearCommands()
    {
        this.commands.clear();
        this.aliases.clear();
        this.objectCommandMap.clear();

        return this;
    }

    private void registerPermission(Permission permission)
    {
        if (permission != null)
        {
            try
            {
                this.pm.addPermission(permission);
            }
            catch(IllegalArgumentException e)
            {}
        }
    }

    private String getTranslation(String key, String alternative, Object... params)
    {
        if (this.plugin instanceof TranslatablePlugin)
        {
            return ((TranslatablePlugin)this.plugin).getTranslation().translate(key, params);
        }
        else
        {
            return String.format(ChatColor.translateAlternateColorCodes('&', alternative), params);
        }
    }

    @Command(name = "help", desc = "Prints the help page", usage = "[command]")
    public void helpCommand(CommandSender sender, CommandArgs args)
    {
        if (args.size() > 0)
        {
            String commandName = args.getString(0);
            SubCommand command = getCommand(commandName);
            if (command != null)
            {
                sender.sendMessage("/" + args.getBaseLabel() + " " + args.getString(0) + " " + command.getUsage());
                sender.sendMessage("    " + this.getTranslation(command.getName() + "_description", command.getDescription()));
            }
            else
            {
                sender.sendMessage(this.getTranslation("help_cmdnotfound", "&cThe command %s was not found!", args.getString(0)));
            }
        }
        else
        {
            sender.sendMessage(this.getTranslation("help_listofcommands", "Here is a list of the available commands and their usage:"));
            sender.sendMessage(" ");

            List<SubCommand> subCommands = new ArrayList<SubCommand>(args.getBaseCommand().getAllSubCommands());
            Collections.sort(subCommands, SubCommand.COMPARATOR);

            for (SubCommand command : subCommands)
            {
                if (command.getPermission() != null && !sender.hasPermission(command.getPermission()))
                {
                    continue;
                }
                sender.sendMessage("/" + args.getBaseLabel() + " " + command.getName() + " " + command.getUsage());
                sender.sendMessage("    " + this.getTranslation(command.getName() + "_description", command.getDescription()));
                sender.sendMessage(" ");
            }
        }
    }

    @Command(name = "version", desc = "Prints the plugin's version")
    public void versionCommand(CommandSender sender, CommandArgs args)
    {
        sender.sendMessage(this.getTranslation("version_pluginversion", "The plugin version: %s", this.plugin.getDescription().getVersion()));
        sender.sendMessage(" ");
    }

    //@Command(desc = "This command is for testing")
    @CommandPermission("libminecraft.test")
    public void test(CommandSender sender, CommandArgs args)
    {
        sender.sendMessage("Params:");

        for (String param : args.getParams())
        {
            sender.sendMessage(" - '" + param + "'");
        }
        sender.sendMessage(" ");

        sender.sendMessage("Flags:");
        for (String flag : args.getFlags())
        {
            sender.sendMessage(" - '" + flag + "'");
        }
    }
}
