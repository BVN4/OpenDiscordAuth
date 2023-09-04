package ru.fazziclay.opendiscordauth.checkupdates;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import org.jetbrains.annotations.NotNull;
import ru.fazziclay.opendiscordauth.UpdateChecker;
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
            event.getHook().editOriginal(String.format("✅ Обновления не найдены\nТекущая версия: `%s`", UpdateChecker.thisVersion))
                .setActionRow(
                    Button.secondary("showPatchNotes", "Что нового?"),
                    Button.link(UpdateChecker.lastVersionDownloadPageURL, "Страница релиза")
                )
                .queue();
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
        boolean status = UpdateChecker.update();
        String downloadStatusString =
            status
                ? "✅ Обновление загружено\nℹ Перезапустите сервер для применения обновлений"
                : "⛔ Не удалось загрузить обновление";
        event.getHook().editOriginal(substring + downloadStatusString)
            .setActionRow(
                status ? Button.secondary("showPatchNotes", "Что нового?") : null,
                status ? Button.link(UpdateChecker.lastVersionDownloadPageURL, "Страница релиза") : null
            )
            .queue();
}

    public void eventHandle(@NotNull ButtonInteractionEvent event) {
        String cid = event.getComponentId();
        if ("showPatchNotes".equals(cid)) {
            event.reply(UpdateChecker.lastVersionPathNotes)
                .setEphemeral(true)
                .queue();
        }
    }
}
