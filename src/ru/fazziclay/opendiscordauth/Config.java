package ru.fazziclay.opendiscordauth;

import java.util.List;

public class Config {

    public static Boolean isDebugEnable = false;

    public static String discordBotToken = Main.pluginConfig.getString("discordBotToken");
    public static String accountsFilePath = Main.pluginConfig.getString("accountsFilePath");
    public static int sessionExpiredDelay = Main.pluginConfig.getInt("sessionExpiredDelay");

    public static int codeCreatorExpiredDelay = Main.pluginConfig.getInt("codeCreator.ExpiredDelay");
    public static int codeCreatorMinimum = Main.pluginConfig.getInt("codeCreator.Minimum");
    public static int codeCreatorMaximum = Main.pluginConfig.getInt("codeCreator.Maximum");

    public static String commandConfirm = Main.pluginConfig.getString("command.Confirm");
    public static String commandCancel = Main.pluginConfig.getString("command.Cancel");

    public static boolean giveTempCodeEventsIsClickEvent = Main.pluginConfig.getBoolean("giveTempCodeEvents.IsClickEvent");
    public static boolean giveTempCodeEventsIsHoverEvent = Main.pluginConfig.getBoolean("giveTempCodeEvents.IsHoverEvent");
    public static Integer giveTempCodeEventsClickMode = Main.pluginConfig.getInt("giveTempCodeEvents.ClickMode");
    public static String giveTempCodeEventsHoverText = Main.pluginConfig.getString("giveTempCodeEvents.HoverText");

    public static String messageSuccessfulAuthorization = Main.pluginConfig.getString("message.SuccessfulAuthorization");
    public static String messageCodeNotFound = Main.pluginConfig.getString("message.CodeNotFound");
    public static String messageNotYoursCode = Main.pluginConfig.getString("message.NotYoursCode");
    public static String messageGiveTempCode = Main.pluginConfig.getString("message.GiveTempCode");
    public static String messageNotFreeTempCodeError = Main.pluginConfig.getString("message.NotFreeTempCodeError");
    public static String messageRegistrationInstructions = Main.pluginConfig.getString("message.RegistrationInstructions");
    public static String messageLoginInstructions = Main.pluginConfig.getString("message.LoginInstructions");
    public static String messageAccountCreatingConfirming = Main.pluginConfig.getString("message.AccountCreatingConfirming");
    public static String messageAuthorizationTimeout = Main.pluginConfig.getString("message.AuthorizationTimeout");
    public static String messagePlayerKickPluginDisabled = Main.pluginConfig.getString("message.PlayerKickPluginDisabled");
    public static String discordChatIdForTranslation = Main.pluginConfig.getString("discordChatIdForTranslation");
    public static String messageCommandMissingPermissions = Main.pluginConfig.getString("message.CommandMissingPermissions");
    public static String messageCommandSuccess = Main.pluginConfig.getString("message.CommandSuccess");
    public static String messageCommandError = Main.pluginConfig.getString("message.CommandError");
    public static String globalMessageFormat = Main.pluginConfig.getString("message.GlobalMessageFormat");
    public static List<?> opUserIdList = Main.pluginConfig.getList("opIdsList");
}