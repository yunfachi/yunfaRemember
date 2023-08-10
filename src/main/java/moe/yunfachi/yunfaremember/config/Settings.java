package moe.yunfachi.yunfaremember.config;

import moe.yunfachi.yunfaremember.YunfaRemember;
import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@YamlFile(header = """
        ┌─────────────────────────────────────────────────────┐
        │                yunfaRemember Config                 │
        ├─────────────────────────────────────────────────────┤
        │ Author: yunfachi                                    │
        │ Site: https://yunfachi.dev                          │
        │ Github: https://github.com/yunfachi/yunfaRemember   │
        │ Modrinth: https://modrinth.com/plugin/yunfaremember │
        │ Spigot: https://www.spigotmc.org/resources/111843   │
        └─────────────────────────────────────────────────────┘""")
public class Settings {

    public Settings(@NotNull YunfaRemember plugin) {
        this.serverGroups = Map.of("default_group",
                plugin.getServer().getAllServers().stream().map(server -> server.getServerInfo().getName()).collect(Collectors.toList())
        );
    }

    @SuppressWarnings("unused")
    public Settings() {
    }

    @YamlComment("Whether to automatically check for plugin updates on startup")
    @YamlKey("check_for_updates")
    private boolean checkForUpdates = true;

    @YamlComment("Server groups will be created on this port.\n# if you want to connect to groups immediately after logging in, then in velocity.toml add a server with the name of the server group, ip 127.0.0.1 and the port that you specified here, now you can add the name of the group you need to \"try\".\n# you will not be able to join the group by ip, so all groups are on the same port")
    @YamlKey("groups_port")
    private int groups_port = 65483;


    @YamlComment("Type of storage to use (FILE)")
    @YamlKey("storage_type")
    private String storageType = "FILE";

    @YamlComment("Groups with servers that will be connected, the group name must be different from the name of the servers, because for correct work it will be necessary to connect to the name of the group, instead of the name of the server.\n# keep order, because new players will go to the first server from the group")
    @YamlKey("server_groups")
    private Map<String, List<String>> serverGroups;

    public boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public int getPort() {return groups_port;}

    public String getStorageType() {
        return storageType;
    }

    public Map<String, List<String>> getServerGroups() {
        return serverGroups;
    }

    public void setCheckForUpdates(boolean idk) {
        checkForUpdates = idk;
    }

}
