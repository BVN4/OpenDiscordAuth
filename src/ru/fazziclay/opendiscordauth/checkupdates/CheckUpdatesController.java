package ru.fazziclay.opendiscordauth.checkupdates;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import ru.fazziclay.opendiscordauth.UpdateChecker;
import ru.fazziclay.opendiscordauth.Utils;
import ru.fazziclay.opendiscordauth.discordbot.Controller;

import java.math.BigDecimal;

public class CheckUpdatesController extends Controller {

    public CheckUpdatesController() {
        super();
    }

    @Override
    public void eventHandle(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (UpdateChecker.isAwaitingForRestart) {
            event.getHook().editOriginal("✅ Обновление уже готово. Ожидание перезапуска сервера").queue();
            return;
        }

        event.getHook().editOriginal("⌛ Поиск обновлений...").queue();
        if (!UpdateChecker.checkUpdates()) {
            event.getHook().editOriginal("✅ Обновления не найдены").queue();
            return;
        }

        BigDecimal fileSize = new BigDecimal(UpdateChecker.lastVersionSize / 1048576);
        String substring = String.format(
            "✅ Обновление найдено `%s -> %s (%sMB)`\n",
            UpdateChecker.thisVersion,
            UpdateChecker.lastVersion,
            fileSize.setScale(2, BigDecimal.ROUND_HALF_UP)
        );

        event.getHook().editOriginal(substring + "⌛ Загрузка обновления...").queue();
        boolean status = Utils.downloadFile("./plugins/OpenDiscordAuth.jar", UpdateChecker.lastVersionDownloadURL);
        String downloadStatusString =
            status
                ? "✅ Обновление загружено\nℹ Перезапустите сервер для применения обновлений"
                : "⛔ Не удалось загрузить обновление";
        if (status) UpdateChecker.isAwaitingForRestart = true;

        event.getHook().editOriginal(substring + downloadStatusString).queue();
    }
}
