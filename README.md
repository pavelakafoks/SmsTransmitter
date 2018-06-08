# SMS Transmitter
Automatic sms sender from Android device, android sms gateway

Описание на русском языке будет ниже.

GitHub: [https://github.com/pavelakafoks/SmsTransmitter](https://github.com/pavelakafoks/SmsTransmitter "https://github.com/pavelakafoks/SmsTransmitter")

Google Play: [https://play.google.com/store/apps/details?id=info.ininfo.smstransmitter](https://play.google.com/store/apps/details?id=info.ininfo.smstransmitter "https://play.google.com/store/apps/details?id=info.ininfo.smstransmitter")

This android application gets text messages from your web site via API and send them as smses via sim card. You can send message in manual mode or automatic mode accroding to scheduller. Don't forget to inform your customers that need to set in settings the url to API and the secret key..

Please, feel free to contact me. Together we can make this application better.

Application wase developed special for site [TimePlan.me](https://timeplan.me "TimePlan.me"). But after I decided to open this aplication for all.

###### API
Android application makes request for data via POST request only with one parameter - "privateKey".

On success server must return response in JSON, UTF-8:
```json
{
	"messages" : [{
			"messageId" : 123,
			"name" : "Pavel",
			"text" : "Reminder. Beauty Studio FOX. We are waiting for you tomorow, 06/06/18 at 1:00 pm",
			"phone" : "10123456789",
			"dtEvent" : "2018-06-06 13:00:00"
		},{
			"messageId" : 124,
			"name" : "Vika",
			"text" : "Reminder. Beauty Studio FOX. We are waiting for you tomorow, 06/06/18 at 2:00 pm",
			"phone" : "10123456780",
			"dtEvent" : "2018-06-06 14:00:00"
		},
	],
	"wrongKey" : false
}
```
On key error the response must be:
```json
{
	"wrongKey" : true
}
```

###### Limitations of application:
- We are sending sms from 9 am to 9 pm (according to phone's time)
- Depends on phone and Android version may be need to change additional settings of phone - energy saving mode for selected applications, as option, for correct working application in automatic mode(by scheduller).  For example, Huawei phones need to set Settings -> Extended settings -> Batary Manager -> add application "SMS Transmitter" in the protected list. Also have a look at settings - "Always use data in mobile networks", "Connectivity support WiFi in sleep mode" and so on. Please, search more information about this moment in internet if you have problems. Native android phones work fine.
For example, problems with Huawei phones described here: [https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled](https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled "https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled")

###### What must be realized on server side:
- Protection from flood/brute force attack, as option - make delay on wrong key
- Give not more than 30 sms per request
- Doesn't give expired messages We have protection on application side by DtEvent, but better don't send them to the phone at all
- Use only hard keys(passwords)
- Use only ssl (http**S**)
- Make limitation to send sms at night. Application will send sms only from 9 am to 9pm, thats why your server must collect smses and send them in the morning
- Log errors on server side. Android application doesn't show error messages on server side by design.



### Русский язык

Данное приложение позволяет забирать сообщения с вашего сайта через api, и рассылать через сим карту телефона. Забор сообщений либо в ручном режиме, либо по расписанию. Не забывайте информировать пользователей приложения о необходимости указать адрес к api в настройках, и ключ.

Обращайтесь по любым вопросам, вместе сделаем приложение лучше.

Программа была разработана специально для сайта [NaPriem.com](https://napriem.com "NaPriem.com"). Но потом я решил сделать ей доступной для всех.

###### API
Данные запрашиваются приложением через POST запрос с единственным параметром - "privateKey".

При успехе сервер должен вернуть ответ:
```json
{
	"messages" : [{
			"messageId" : 123,
			"name" : "Павел",
			"text" : "Напоминание от Студия Красоты ЛИС. Вы записаны на завтра, 6 июня в 13:00",
			"phone" : "70123456789",
			"dtEvent" : "2018-06-06 13:00:00"
		},{
			"messageId" : 124,
			"name" : "Виктория",
			"text" : "Напоминание от Студия Красоты ЛИС. Вы записаны на завтра, 6 июня в 14:00",
			"phone" : "70123456780",
			"dtEvent" : "2018-06-06 14:00:00"
		},
	],
	"wrongKey" : false
}
```
При ошибке ключа сервер должен вернуть ответ:
```json
{
	"wrongKey" : true
}
```

###### Ограничение приложения:
- Cмс отправляются только с 9 до 21 часа по времени телефона
- В зависимости от производителя телефона и версии Android возможно потребуется произвести дополнительные настройки в настройках энергосбережения вашего телефона для корректной работы программы в автоматическом режиме.  Например, для Huawei телефонов нужно в Настройки -> Расширенные настройки -> Диспетчер батареи добавить приложение "SMS Transmitter" в список защищённых. Также к таким настройкам относится - "Постоянная передача данных в мобильной сети", "Поддержка соединения WiFi в спящем режиме". При восстановлении соединения приложение может восстановить работу в автоматическом режиме. Если телефон стоит на зарядке, то SMS Transmitter в автоматическом режиме работает более надёжно. 
Наблюдаются проблемы с Huawei телефонами, описание: [https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled](https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled "https://stackoverflow.com/questions/40276458/foreground-service-killed-on-huawei-gra-ul00-protected-apps-enabled")

###### Что должно быть реализовано на серверной стороне:
- Защита от флуда/подбора пароля, как вариант - при неправильном коде - сделать задержку
- Отдавать за раз не более 30 смс
- Не отдавать устаревшие записи (просроченные). На стороне приложения есть защита по DtEvent, но лучше их на телефон не передавать вовсе
- Использовать только сложные пароли
- Настоятельно рекомендуется использовать ssl (http**S**)
- Ограничение на отправку смс в ночное время, если это не нужно получателям. Также базово стоит запрет отправки в ночное время (с 21 до 9) на стороне приложения, так что сервер должен передавать ночные сообщения на отправку в утреннее время.
- Протоколирование ошибок, передача дополнительной информации в приложение android о проблемах в серверной части - не предусмотрено.