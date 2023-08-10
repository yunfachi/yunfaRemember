package moe.yunfachi.yunfaremember;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.william278.desertwell.about.AboutMenu;
import org.jetbrains.annotations.NotNull;

public class YunfaRememberCommand implements SimpleCommand {

    private final AboutMenu aboutMenu;
    private final YunfaRemember plugin;

    public YunfaRememberCommand(final @NotNull YunfaRemember plugin) {
        this.plugin = plugin;
        this.aboutMenu = AboutMenu.builder()
                .title(Component.text(plugin.getDescription().getName().get()))
                .description(Component.text(plugin.getDescription().getDescription().get()))
                .version(plugin.getVersion())
                .credits("Author", AboutMenu.Credit.of("yunfachi").description("Click to visit website").url("https://github.com/yunfachi"))
                .buttons(
                        AboutMenu.Link.of("https://modrinth.com/mod/yunfaremember").text("Modrinth").icon("⛏").color(TextColor.color(0x1bd96a)),
                        AboutMenu.Link.of("https://github.com/yunfachi/yunfaRemember/issues").text("Issues").icon("❌").color(TextColor.color(0xadbac7)),
                        AboutMenu.Link.of("https://discord.gg/xdHXEupjkM").text("Discord").icon("⭐").color(TextColor.color(0x7289da)))
                .build();
    }

    @Override
    public void execute(Invocation invocation) {
        invocation.source().sendMessage(this.aboutMenu.toComponent());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("yunfaremember.command.about");
    }
}
