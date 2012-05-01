package de.cubeisland.libMinecraft.command;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.permissions.PermissionDefault;

/**
 * Annotates a method as a command
 *
 * @author Phillip Schichtel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command
{
    public String name() default "";
    public String[] aliases() default {};
    public String permission() default "";
    public PermissionDefault permissionDefault() default PermissionDefault.OP;
    public String usage() default "";
    public String desc();
}
