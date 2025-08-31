package HeldItemSaver;

import HeldItemSaver.configs.ConfigHandler;
import HeldItemSaver.configs.ModConfig;
import HeldItemSaver.configs.ModLogger;
import HeldItemSaver.events.BattleListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeldItemSaver implements ModInitializer {

    // Logger instance for logging messages related to HeldItemSaver.
    public static final Logger LOGGER = LoggerFactory.getLogger("HeldItemSaver");

    // Reference to the active Minecraft server instance.
    public static MinecraftServer server = null;

    // Config
    private static ModConfig config;

    /**
     * Called during the mod initialization phase.
     * Handles registration of commands, event listeners, and other initial setup.
     */
    @Override
    public void onInitialize() {
        LOGGER.info("HeldItemSaver Loaded!");
        registerCommands();
        config = ConfigHandler.loadConfig();

        // Execute tasks and listeners that should run when the server starts.
        registerServerStartListeners();

        // Initialize BattleListener and CatchEventListener
        new BattleListener();
    }

    /**
     * Register listeners that should be executed when the server starts.
     * It updates the server variable with the current server instance.
     */
    private void registerServerStartListeners() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            HeldItemSaver.server = server;
        });
    }

    /**
     * Registers the reload command for the mod.
     * Defines the base command 'helditemsaver'.
     * It includes a permission check for executing the 'reload' subcommand -
     * Will add actual permission for this if needed, but for now it's restricted to OPs/Console
     */
    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher,
                                                    registryAccess,
                                                    environment) ->
                dispatcher.register(CommandManager.literal("helditemsaver")
                        .then(CommandManager.literal("reload")
                                .requires(src -> src.hasPermissionLevel(2))
                                .executes(ctx -> {
                                    ModLogger.reloadConfig();
                                    ctx.getSource().sendMessage(Text.literal("HeldItemSaver configuration reloaded.")
                                            .formatted(Formatting.GREEN));
                                    return 1;
                                })
                        )
                ));
    }
}
