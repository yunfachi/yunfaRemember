package moe.yunfachi.yunfaremember.commands;

import com.velocitypowered.api.command.SimpleCommand;
import moe.yunfachi.yunfaremember.YunfaRemember;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.william278.desertwell.about.AboutMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class YunfaRememberCommand implements SimpleCommand {

    private final AboutMenu aboutMenu;
    private final YunfaRemember plugin;

    public YunfaRememberCommand(final @NotNull YunfaRemember plugin) {
        this.plugin = plugin;
        this.aboutMenu = AboutMenu.builder()
                .title(Component.text(plugin.getDescription().getName().get()))
                .description(Component.text(plugin.getDescription().getDescription().get()))
                .version(plugin.getVersion())
                .themeColor(TextColor.color(0xb3bde1))
                .credits("Author", AboutMenu.Credit.of("yunfachi").description("Click to visit website").url("https://github.com/yunfachi"))
                .buttons(
                        AboutMenu.Link.of("https://modrinth.com/mod/yunfaremember").text("Modrinth").icon("⛏").color(TextColor.color(0x1bd96a)),
                        AboutMenu.Link.of("https://github.com/yunfachi/yunfaRemember/issues").text("Issues").icon("❌").color(TextColor.color(0xadbac7)),
                        AboutMenu.Link.of("https://discord.gg/xdHXEupjkM").text("Discord").icon("⭐").color(TextColor.color(0x7289da)))
                .build();
    }

    @Override
    public void execute(Invocation invocation) {
        if(invocation.arguments().length >= 1) {
            switch (invocation.arguments()[0].toLowerCase(Locale.ROOT)) {
                case "about", "info" -> invocation.source().sendMessage(aboutMenu.toComponent());
                case "reload" -> {
                    plugin.loadConfig();
                    plugin.loadPlayers();
                    invocation.source().sendMessage(Component.text().content("yunfaRemember").color(TextColor.color(0xb3bde1)).decoration(TextDecoration.BOLD, true).append(Component.text().content(" -=- Reloaded config & players files.")));
                }
                default -> invocation.source().sendMessage(Component.text().content("yunfaRemember").color(TextColor.color(0xb3bde1)).decoration(TextDecoration.BOLD, true).append(Component.text().content(" -=- Invalid syntax.")));
            }
            return;
        }

        invocation.source().sendMessage(this.aboutMenu.toComponent());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("yunfaremember.command.yunfaremember");
    }
}
