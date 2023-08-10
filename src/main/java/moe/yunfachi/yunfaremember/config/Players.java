package moe.yunfachi.yunfaremember.config;

import com.google.common.collect.HashBiMap;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import moe.yunfachi.yunfaremember.YunfaRemember;
import net.william278.annotaml.Annotaml;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

@YamlFile(header = """
                    ┌─────────────────────────────────────────────────────┐
                    │                yunfaRemember Players                │
                    ├─────────────────────────────────────────────────────┤
                    │ Author: yunfachi                                    │
                    │ Site: https://yunfachi.dev                          │
                    │ Github: https://github.com/yunfachi/yunfaRemember   │
                    │ Modrinth: https://modrinth.com/plugin/yunfaremember │
                    │ Spigot: https://www.spigotmc.org/resources/111843   │
                    └─────────────────────────────────────────────────────┘""")
public class Players {

    public Players(@NotNull YunfaRemember plugin) {

    }

    @SuppressWarnings("unused")
    public Players() {
    }


    @YamlKey("players")
    //private Map<String, List<String>> players = Collections.emptyMap();
    public Map<String, Map<String, String>> players = Collections.emptyMap();

    public String getLatestServer(@NotNull UUID uuid, String group) {
        try {
            if (players.containsKey(uuid.toString()))
                if (((Section) players.get(uuid.toString())).contains(group))
                    return (String) ((Section) players.get(uuid.toString())).get(group);
        } catch (ClassCastException e) {
            if (players.containsKey(uuid.toString()))
                if (players.get(uuid.toString()).containsKey(group))
                    return players.get(uuid.toString()).get(group);
        }
        return YunfaRemember.instance.getConfig().getServerGroups().get(group).get(0);
    }
    public void setLatestServer(UUID uuid, String group, String server) {
        try {
            if (players.containsKey(uuid.toString()))
                ((Section) players.get(uuid.toString())).set(group, server);
            else
                players.put(uuid.toString(), Map.of(group, server));
        } catch (ClassCastException e) {
            if(players.containsKey(uuid.toString()))
                players.get(uuid.toString()).put(group, server);
            else
                players.put(uuid.toString(), Map.of(group, server));
        }
        YunfaRemember.instance.savePlayers();
    }
}