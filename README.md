# Steamgifter

This project is a multiuser Steamgifts Bot with an interface. It regularly participates in selected giveaways on [steamgifts.com](https://www.steamgifts.com/) based on the win chance and notifies the users if a game is won via mail.

## Server

Runnable Jar, that runs the choosen algorithm from each user.

- Random UserAgent selection
- DLC Detection Engine with Caching
- Auto Sync
- Notify won giveaway only on Key/Link set
- Steam & Steamgifts giveaway activation check
- Warn/Suspend on giveaway activation duration end
- Notify on suspension
- "Prefer Steam Wishlist" implementation
- "Skip Packages" implementation
- "Skip DLC" implementation
- Watchdog for self-surveillance

### 6x unique algorithms:

- **Default**: Enters giveaways, sorted by remaining time
- **Snitch**: Enters giveaways with winchance > 0.5%, sorted by remaining time [checks & removes entered giveaways < 0.5%]
- **Grabber**: Enters giveaways with remaining time < 1h, sorted by winchance
- **Snatcher**: Enters giveaways with remaining time < 1h and winchance > 0.5%, sorted by remaining time
- **Chancer**: Enters giveaways with a calculated minimum winchance depending on available points
- **Faker**: Enters no giveaways at all

## Client (WebGUI with ZK 8.5.0)

- Dashboard with achievements
- Won Giveaways list
- Not activated Giveaway Reminder list
- Info Log with summary about the poll
- User Login with settings & personal statistics
- Admin Panel with logs displayed (Error, Info, Tomcat)

# How to install:

## Requirements
- [Tomcat 9](https://tomcat.apache.org/download-90.cgi)
- [Java 1.8](https://java.com/de/download/)
- [MariaDB 5.5](https://mariadb.com/downloads/)
- [Maven](https://maven.apache.org/download.cgi)

Runs on Linux and Windows

## Build
  - Edit **FILEPATH** in Steamgifter-GUI's [AdminPanelController.java](/Steamgifter-GUI/src/main/java/com/helloingob/gifter/AdminPanelController.java) file
  - Edit **fileName** in log4j2.xml ([Steamgifter-Central](/Steamgifter-Central/src/main/resources/log4j2.xml), [Steamgifter-GUI](/Steamgifter-GUI/src/main/resources/log4j2.xml), [Steamgifter-Shared](/Steamgifter-Shared/src/resources/log4j2.xml))
  - Run "**mvn clean package**"
```
Output:
../Steamgifter/Steamgifter-Central/target/sgserver-jar-with-dependencies.jar
../Steamgifter/Steamgifter-GUI/target/gifterclient.war
```
## Database
Add user "**gifter**" with "**helloingob**" password & create **database**.

  ```
  CREATE USER 'gifter'@'localhost' IDENTIFIED BY 'helloingob';
  GRANT ALL PRIVILEGES ON gifter.* TO 'gifter'@'localhost';
  FLUSH PRIVILEGES;

  CREATE DATABASE gifter;
  ```
  Execute [schema.sql](/Steamgifter-Shared/sql/schema.sql)

## Setup Server:

1) Add following files in the server folder:

   Add **useragents.xml** for agent shuffling (*OPTIONAL!*)
   ```
   <useragents>
     <useragent description="Chrome 123 (Win 8.1 - 64 bit)" useragent="Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/123 (KHTML, like Gecko) Chrome/123 Safari/123"/>
     ...
   </useragents>
   ```

   Add **mail.properties** file and update
   ```
     mail.disabled=false
     mail.transport.protocol=smtp
     mail.smtp.auth=true
     mail.smtp.starttls.enable=true
     mail.smtp.host=smtp.host.com
     mail.smtp.port=587
     mail.smtp.user=gifter@host.com
     mail.smtp.password=1337
   ```  
2) Setup **cronjob/task-scheduler** to execute **sgserver.jar** hourly.

## Setup Client:
1) Copy gifterclient.war file to tomcat webapps directory
2) Start Tomcat server
3) Access => http://localhost:8080/gifterclient
4) Login with "hello"/"ingob"
5) Update settings (password, email, phpsessionid, algorithm, ...)

## TODO/Known Bugs
- Logging
- Datehandling (Summer/Winter)
- Bug if old giveaway gets new winner (you) and enddate + BAN_TIME > now()
