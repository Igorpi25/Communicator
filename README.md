Communicator
============
Обеспечивает постоянное соединение с сервером для обмена JSON сообщениями, используя Websocket. Биндинг входящих JSON сообщений в Sqlite БД. Service для фоновой работы

Концепция
---------
Клиент-серверная часть полностью отделена от остальной части. Весь протокол JSON сообщений, все константы находятся в одном месте.

Эта штука, позволяет каждому модулю самостоятельно реализовать свой транспорт. Такой подход позволяет логически отделить клиент-серверную часть, от остального в пределах модуля. И одновременно, объединить клиент-серверный код со всех модулей используя одинаковый интерфейс. 

Модульный подход позволяет в случае необходимости быстро добавить, или удалить целые разделы. Изменения делаются только в одном файле `ServiceCommunicator`


По рекомендованному поведению Communicator берет входящие по Websocket-у сообщения и записывает их в БД. Логика записывается на уровне Transport-а. Каждый модуль сам реализовывающий Transport, самостоятельно определяет и обрабатывает входящие сообщения адресованные ему.

Исходящие сообщения, в конечном виде формируются на Transport-е.

Примеры использования
------------------
1. [Profile][31] 
2. [Chat][32]
3. [Map][33]
4. [Messenger][34]

Установка RxAndroid в Eclipse
-----------------------------
Для работы RxAndroid требуется установить RxJava: 

1. Заходим на страницу Maven репозитория <a href='http://search.maven.org/#search%7Cga%7C1%7Crxjava'><img src='http://img.shields.io/maven-central/v/io.reactivex/rxjava.svg'></a>
2. Находим репозиторий с параметрами: GroupId = io.rectivex, ArtifactId = rxjava
3. Скачиваем jar файл (ВНИМАНИЕ! Не перепутайте с javadoc.jar или sources.jar)
4. Копируем скачанный файл в папку Workspaсe/AndroidProject/lib   
5. Заходим в Eclipse. В окне Packaje Explorer, в папке /lib нажимаем правую кнопку и делаем Refresh

Теперь устанавливаем RxAndroid:

1. Заходим на страницу Maven репозитория <a href='http://search.maven.org/#search%7Cga%7C1%7Crxandroid'><img src='http://img.shields.io/maven-central/v/io.reactivex/rxandroid.svg'></a>
2. Находим репозиторий с параметрами: GroupId = io.rectivex, ArtifactId = rxandroid (ВНИМАНИЕ! Не путайте ArtifactId с rxandroid-api и rxandroid-framework)
3. Скачиваем aar файл. Если вместо aar есть jar, то скачиваем его и пропускаем шаг 4 (ВНИМАНИЕ! Не перепутайте с javadoc.jar или sources.jar). 
4. Меняем расширение файла с aar на zip. Открываем zip-файл любым архиватором. Внутри вы увидите подобие структуры Android проекта. Находим и извлекаем файл class.jar. Переименовываем его в rxandroid.jar. Теперь у нас есть jar-файл библиотеки, далее будем называть его прото "jar файлом". 
4. Копируем jar файл в папку Workspaсe/AndroidProject/lib   
5. Заходим в Eclipse. В окне Packaje Explorer, в папке /lib нажимаем правую кнопку и делаем Refresh

ВНИМАНИЕ! Если внутри архива aar в папках res,assets,lib что-нибудь есть, или в файле AndroidManifest.xml написано что-нибудь кроме названия пакета и версии sdk, то [здесь][21] инструкция

[21]: http://www.iphonedroid.com/blog/en/utilizar-ficheros-aar-en-eclipse/#.VwbcWTEiYdI
[31]: https://github.com/Igorpi25/Profile
[32]: https://github.com/Igorpi25/Chat
[33]: https://github.com/Igorpi25/Map
[34]: https://github.com/Igorpi25/Messenger

