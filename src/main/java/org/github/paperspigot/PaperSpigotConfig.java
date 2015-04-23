package org.github.paperspigot;

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class PaperSpigotConfig
{

    private static final File CONFIG_FILE = new File( "paper.yml" );
    private static final String HEADER = "This is the main configuration file for PaperSpigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to PaperSpigot,\n"
            + "join us at the IRC.\n"
            + "\n"
            + "IRC: #paperspigot @ irc.spi.gt ( http://irc.spi.gt/iris/?channels=PaperSpigot )\n";
    /*========================================================================*/
    static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    /*========================================================================*/

    static {
        init();
    }

    public static void init()
    {
        config = new YamlConfiguration();
        try
        {
            config.load ( CONFIG_FILE );
        } catch ( IOException ex )
        {
        } catch ( InvalidConfigurationException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not load paper.yml, please correct your syntax errors", ex );
            throw Throwables.propagate( ex );
        }
        config.options().header( HEADER );
        config.options().copyDefaults( true );

        commands = new HashMap<String, Command>();

        version = getInt( "config-version", 6 );
        set( "config-version", 6 );
        readConfig( PaperSpigotConfig.class, null );
    }

    public static void registerCommands()
    {
        for ( Map.Entry<String, Command> entry : commands.entrySet() )
        {
            MinecraftServer.getServer().server.getCommandMap().register( entry.getKey(), "PaperSpigot", entry.getValue() );
        }
    }

    static void readConfig(Class<?> clazz, Object instance)
    {
        for ( Method method : clazz.getDeclaredMethods() )
        {
            if ( Modifier.isPrivate( method.getModifiers() ) )
            {
                if ( method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE )
                {
                    try
                    {
                        method.setAccessible( true );
                        method.invoke( instance );
                    } catch ( InvocationTargetException ex )
                    {
                        throw Throwables.propagate( ex.getCause() );
                    } catch ( Exception ex )
                    {
                        Bukkit.getLogger().log( Level.SEVERE, "Error invoking " + method, ex );
                    }
                }
            }
        }

        try
        {
            config.save( CONFIG_FILE );
        } catch ( IOException ex )
        {
            Bukkit.getLogger().log( Level.SEVERE, "Could not save " + CONFIG_FILE, ex );
        }
    }

    private static void set(String path, Object val)
    {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def)
    {
        config.addDefault( path, def );
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def)
    {
        config.addDefault( path, def );
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def)
    {
        config.addDefault( path, def );
        return config.getFloat( path, config.getFloat( path ) );
    }

    private static int getInt(String path, int def)
    {
        config.addDefault( path, def );
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def)
    {
        config.addDefault( path, def );
        return (List<T>) config.getList( path, config.getList( path ) );
    }

    private static String getString(String path, String def)
    {
        config.addDefault( path, def );
        return config.getString( path, config.getString( path ) );
    }

    public static double babyZombieMovementSpeed;
    private static void babyZombieMovementSpeed()
    {
        babyZombieMovementSpeed = getDouble("settings.baby-zombie-movement-speed", 0.5D); // Player moves at 0.1F, for reference
    }

    public static boolean asyncCatcherFeature;
    private static void asyncCatcherFeature()
    {
        asyncCatcherFeature = getBoolean( "settings.async-plugin-bad-magic-catcher", true );
        if (!asyncCatcherFeature) {
            Bukkit.getLogger().log( Level.INFO, "Disabling async plugin bad ju-ju catcher, this may be bad depending on your plugins" );
        }
    }

    public static boolean interactLimitEnabled;
    private static void interactLimitEnabled()
    {
        interactLimitEnabled = getBoolean("settings.limit-player-interactions", true);
        if (!interactLimitEnabled) {
            Bukkit.getLogger().log( Level.INFO, "Disabling player interaction limiter, your server may be more vulnerable to malicious users" );
        }
    }

    public static double strengthEffectModifier;
    public static double weaknessEffectModifier;
    private static void effectModifiers()
    {
        strengthEffectModifier = getDouble( "effect-modifiers.strength", 1.3D );
        weaknessEffectModifier = getDouble("effect-modifiers.weakness", -0.5D);
    }

    public static int maxPacketsPerPlayer;
    private static void maxPacketsPerPlayer()
    {
        maxPacketsPerPlayer = getInt( "max-packets-per-player", 1000 );
    }

    public static int connectionHandlerThreads;
    public static int worldThreads;
    public static int entityThreads;
    public static int lightingThreads;
    public static int doTickThreads;
    public static boolean saferConnectionHandlerProcessing;
    private static void concurrencyThreads()
    {
        if(org.spigotmc.SpigotConfig.playerShuffle > 0) {
            log("--- WARNING ---");
            log("You have Player Shuffle enabled, but Player Shuffle is disabled in this build!");
            log("This build will ignore any Player Shuffle related settings.");
        }
        doTickThreads = getInt("concurrency.do-tick-threads", 1);
        connectionHandlerThreads = getInt("concurrency.connection-handler-threads", 1);
        worldThreads = getInt("concurrency.world-threads", 1);
        entityThreads = getInt("concurrency.entity-threads", 1);
        lightingThreads = getInt("concurrency.lighting-threads", 1);
        saferConnectionHandlerProcessing = getBoolean("concurrency.safer-connection-handler-processing", true);
        log( "Connection processing threads: " + connectionHandlerThreads);
        log( "World processing threads: " + worldThreads);
        log( "Entity & TileEntity & EntityActivation processing threads: " + entityThreads);
        log( "Lighting processing threads: " + lightingThreads);
        log( "Safer async connection processing: " + saferConnectionHandlerProcessing);
    }

    private static void log(String s)
    {
        LogManager.getLogger().info( s );
    }
}
