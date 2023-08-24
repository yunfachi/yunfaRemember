package moe.yunfachi.yunfaremember;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import moe.yunfachi.yunfaremember.commands.YunfaRememberCommand;
import moe.yunfachi.yunfaremember.config.Players;
import moe.yunfachi.yunfaremember.config.Settings;
import net.william278.annotaml.Annotaml;
import net.william278.desertwell.util.UpdateChecker;
import net.william278.desertwell.util.Version;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "yunfaremember",
        name = "yunfaRemember",
        version = "1.1.0",
        description = "A velocity plugin allows you to stay on the same server when you exit",
        url = "https://modrinth.com/plugin/yunfaremember",
        authors = {"yunfachi"}
)
public class YunfaRemember {
    @Inject
    private PluginContainer pluginContainer;
    @Inject
    private Metrics.Factory metricsFactory;
    private static final int METRICS_ID = 19423;
    private static final int SPIGOT_RESOURCE_ID = 111843;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private Settings settings;
    private Players players;

    private Annotaml<Players> players_file;
    public static YunfaRemember instance;

    @Inject
    public YunfaRemember(@NotNull ProxyServer server, @NotNull Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        loadConfig();
        loadPlayers();
        registerServers();
        registerCommands();
        registerMetrics();
        checkForUpdates();
        logger.info("Successfully enabled YunfaRemember");
    }

    @Subscribe
    public void OnServerPreConnect(@NotNull ServerPreConnectEvent event) {
        if (event.getResult().getServer().isPresent()) {
            if (settings.getServerGroups().containsKey(event.getResult().getServer().get().getServerInfo().getName())) {
                while(true) {
                    Optional<RegisteredServer> server = getServer().getServer(
                            players.getLatestServer(
                                    event.getPlayer().getUniqueId(),
                                    event.getResult().getServer().get().getServerInfo().getName()
                            )
                    );
                    if(server.isPresent()) {
                        ServerPreConnectEvent.ServerResult prev_server = event.getResult();
                        event.setResult(
                                ServerPreConnectEvent.ServerResult.allowed(
                                        server.get()
                                )
                        );
                        if(!settings.getServerGroups().containsKey(server.get().getServerInfo().getName()))
                            break;
                        players.setLatestServer(
                                event.getPlayer().getUniqueId(),
                                server.get().getServerInfo().getName(),
                                prev_server.getServer().get().getServerInfo().getName()
                        );
                    }
                }
            } else getConfig().getServerGroups().forEach((k, v) -> {
                if(v.contains(event.getResult().getServer().get().getServerInfo().getName())) {
                    players.setLatestServer(
                            event.getPlayer().getUniqueId(),
                            k,
                            event.getResult().getServer().get().getServerInfo().getName()
                    );
                }
            });
        }
    }

    @Subscribe
    public void OnServerChoose(@NotNull PlayerChooseInitialServerEvent event) {
        if (event.getInitialServer().isPresent()) {
            if (settings.getServerGroups().containsKey(event.getInitialServer().get().getServerInfo().getName())) {
                while(true) {
                    Optional<RegisteredServer> server = getServer().getServer(
                            players.getLatestServer(
                                    event.getPlayer().getUniqueId(),
                                    event.getInitialServer().get().getServerInfo().getName()
                            )
                    );
                    if(server.isPresent()) {
                        RegisteredServer prev_server = server.get();
                        event.setInitialServer(server.get());
                        if(!settings.getServerGroups().containsKey(server.get().getServerInfo().getName()))
                            break;
                        players.setLatestServer(
                                event.getPlayer().getUniqueId(),
                                server.get().getServerInfo().getName(),
                                prev_server.getServerInfo().getName()
                        );
                    }
                }
            } else getConfig().getServerGroups().forEach((k, v) -> {
                if(v.contains(event.getInitialServer().get().getServerInfo().getName())) {
                    players.setLatestServer(
                            event.getPlayer().getUniqueId(),
                            k,
                            event.getInitialServer().get().getServerInfo().getName()
                    );
                }
            });
        }
    }

    public void registerServers() {
        getConfig().getServerGroups().forEach((k, v) -> {
            this.getServer().getServer(k).ifPresentOrElse((action) -> {}, () -> {
                this.getServer().registerServer(
                    new ServerInfo(
                        k, new InetSocketAddress(
                            "127.0.0.1",
                            getConfig().getPort()
                        )
                    )
                );}
            );
        });

    }

    public void loadConfig() {
        try {
            settings = Annotaml.create(
                    new File(dataDirectory.toFile(), "config.yml"),
                    new Settings(this)
            ).get();
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Failed to load config file: " + e.getMessage(), e);
        }
    }

    public void loadPlayers() {
        try {
            players_file = Annotaml.create(
                    new File(dataDirectory.toFile(), "players.yml"),
                    new Players(this)
            );
            players = players_file.get();
        } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Failed to load players file: " + e.getMessage(), e);
        }
    }

    public void savePlayers() {
        players_file = Annotaml.create(players);
        try {
            players_file.save(new File(dataDirectory.toFile(), "players.yml"));
        } catch (IOException e) {
            logger.error("Failed to save players file: " + e.getMessage(), e);
        }


    }

    @NotNull
    public PluginDescription getDescription() {
        return pluginContainer.getDescription();
    }

    @NotNull
    public Version getVersion() {
        return Version.fromString(pluginContainer.getDescription().getVersion().orElseThrow(), "-");
    }

    private void registerMetrics() {
        final Metrics metrics = metricsFactory.make(this, METRICS_ID);
        metrics.addCustomChart(new SimplePie("update_checks", () -> getConfig().isCheckForUpdates() ? "Yes" : "No"));
    }

    private void registerCommands() {
        final Command command = new YunfaRememberCommand(this);
        server.getCommandManager().register(
                "yunfaremember", command, "yremember", "yr"
        );
        server.getCommandManager().register(
                "yunfaremember", command, "yremember", "yr"
        );
    }

    @NotNull
    public UpdateChecker getUpdateChecker() {
        return UpdateChecker.builder()
                .currentVersion(getVersion())
                .endpoint(UpdateChecker.Endpoint.MODRINTH)
                .resource(getDescription().getId())
                .build();
    }

    private void checkForUpdates() {
        if (getConfig().isCheckForUpdates()) {
            getUpdateChecker().check().thenAccept(checked -> {
                if (!checked.isUpToDate()) {
                    log(Level.WARN, "A new version of yunfaRemember is available: v"
                            + checked.getLatestVersion() + " (running v" + getVersion() + ")\nDownload: https://modrinth.com/mod/yunfaremember");
                }
            });
        }
    }

    public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        switch (level) {
            case ERROR -> {
                if (exceptions.length > 0) {
                    logger.error(message, exceptions[0]);
                } else {
                    logger.error(message);
                }
            }
            case WARN -> {
                if (exceptions.length > 0) {
                    logger.warn(message, exceptions[0]);
                } else {
                    logger.warn(message);
                }
            }
            case INFO -> logger.info(message);
        }
    }

    public void log(@NotNull String message) {
        this.log(Level.INFO, message);
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public Settings getConfig() {
        return settings;
    }

    public Players getPlayers() {
        return players;
    }

    public Annotaml<Players> getPlayers_file() {
        return players_file;
    }
}
