
## config.yml
```yaml
discordBotToken: "ENTER DISCORD BOT TOKEN HERE"
# Токен бота дискорд. Как создать бота гугли.
# Так же во вкладке bot надо включить `Presence Intent` и `Server Members Intent`

accountsFilePath: "./plugins/OpenDiscordAuth/accounts.json"
# Путь до файла где будут хранится аккаунты

sessionExpiredDelay: 120
# Время, сколько секунд после выхода человек может зайти назад
# без необходимости вводить код

discordChatIdForTranslation: "ENTER CHAT ID HERE"
# ID чата в который будут отправляться сообщения из чата майнкрафта и обратно
# Если вы указали это, боту требуется выдать право MANAGE_WEBHOOKS

codeCreator:
  ExpiredDelay: 43 # Время сколько действует одноразовый код
  Minimum: 0       # Минимальное число при генерации кода
  Maximum: 1000    # Максимальное число при генерации кода

command:
  Confirm: "confirm" # Команда подтверждения
  Cancel: "cancel"   # Команда отмены

giveTempCodeEvents:
  IsClickEvent: true # Включено ли событие нажатия на сообщение GiveTempCode
  IsHoverEvent: true # Включено ли событие наведение курсора на сообщение GiveTempCode
  ClickMode: 1       # Режим нажатия на сообщение. 0 - Вставить код в чат(SUGGEST_COMMAND); 1 - Скопировать в буфер обмена(COPY_TO_CLIPBOARD) 1.15+
  HoverText: "&9~ Одноразовый код ~\n&rВаш код -&a $code\n\n&d(Нажмите что бы скопировать!)"

message:
  SuccessfulAuthorization: "Успешная авторизация"
  CodeNotFound: "Код не найден."
  NotYoursCode: "Код не пренадлежит вам!"
  GiveTempCode: "Ваш одноразовый код - &a$code&r. &b[Клик]"
  NotFreeTempCodeError: "Не удалось сгенерировать одноразовый код. Попробуйте через минуту."
  RegistrationInstructions: "Для регистрации вашего аккаунта напишите ваш одноразовый код в Личные Сообщения боту Bot#0001. Далее следуйте инструкциям. https://discord.gg/XXXXXXX"
  LoginInstructions: "&cЕсли вы новый игрок - смените ник.\n&rДля входа в ваш аккаунт напишите одноразовый код в Личные Сообщения боту Bot#0001 в дискорде https://discord.gg/XXXXXXX"
  AccountCreatingConfirming: "Подтвердите привязку аккаунта $discord к вашему нику($nickname). Напишие в чат &a'confirm' - для подтверждения&r, &c'cancel' - для отмены&r."
  AuthorizationTimeout: "Время авторизации вышло."
  PlayerKickPluginDisabled: "Вас выгнали, потому что плагин авторизации отключен, и вы не вошли в систему."
  CommandMissingPermissions: "У вас недостаточно прав"
  CommandSuccess: "Команда исполнена"
  CommandError: "Ошибка"
  GlobalMessageFormat: '&7[&bG&7] [&3discord&7] &%color%displayname &9> &7%message'

opIdsList: ["123456789123456789", "123456789123456789"]
# Список ID пользователей которые смогут пользоваться командой /rc
```