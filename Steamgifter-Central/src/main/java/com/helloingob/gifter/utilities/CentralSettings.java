package com.helloingob.gifter.utilities;

public class CentralSettings {

    public static class General {
        public static final int TIMEOUT = 30000;
        public static final int PAGES_TO_PARSE = 1;
        public static final int MAX_RECONNECTS = 2;
    }

    public static class Security {
        public static final int MIN_RANDOM_SLEEP_RANGE = 1;
        public static final int MAX_RANDOM_SLEEP_RANGE = 600000;

        public static final int MIN_RANDOM_SYNC_RANGE = 2;
        public static final int MAX_RANDOM_SYNC_RANGE = 15;
    }

    public static class Penalties {
        //5 days        
        public static final int STEAM_ACKNOWLEDGE_REMINDER_BAN = 120;
        //3 days
        public static final int STEAM_ACKNOWLEDGE_REMINDER_WARNING = 72;
        //10 days
        public static final int STEAMGIFTS_ACKNOWLEDGE_REMINDER_BAN = 240;
        //7,5 days
        public static final int STEAMGIFTS_ACKNOWLEDGE_REMINDER_WARNING = 180;

        //20 days        
        public static final int MAX_REMINDER_DAYS = 480;
    }

    public static class Developer {
        //the last 2 polls must have at least X entered giveaways
        public static final int WATCHDOG_LATEST_POLLS_COUNT = 5;
    }

    public static class Files {
        public static final String MAIL = "mail.properties";
        public static final String USERAGENTS = "useragents.xml";
    }

    public static class Threads {
        private final static double WAIT_COMPUTE_TIME_RATIO = 1;
        private final static double CPU_UTILIZATION = 90;
        private final static double CPU = Runtime.getRuntime().availableProcessors();
        public final static int MAX_THREADS = (int) (CPU * (CPU_UTILIZATION / 100) * (1 + WAIT_COMPUTE_TIME_RATIO));
    }

    public static class DLC {
        public static final boolean ASSUME_EVERY_GAME_IS_DLC = true;
        public static final String STEAMAPI_BASE_URL = "http://store.steampowered.com/api";

        public static final String STEAMAPI_APPID = STEAMAPI_BASE_URL + "/appdetails?appids=";
        public static final String STEAMAPI_SUBID = STEAMAPI_BASE_URL + "/packagedetails?packageids=";
    }

    public static class Url {
        public static final String STEAMGIFTS_BASE_URL = "https://www.steamgifts.com";
        public static final String STEAMSTORE_BASE_URL = "https://store.steampowered.com";

        public static final String INDEX_URL = STEAMGIFTS_BASE_URL + "/giveaways/search?page=1";
        public static final String GIVEAWAY_URL = STEAMGIFTS_BASE_URL + "/giveaways/search?page=";
        public static final String WISHLIST_GIVEAWAY_URL = STEAMGIFTS_BASE_URL + "/giveaways/search?type=wishlist";
        public static final String SYNCEDGAME_URL = STEAMGIFTS_BASE_URL + "/account/steam/games/search?page=";
        public static final String WONGIVEAWAY_URL = STEAMGIFTS_BASE_URL + "/giveaways/won/search?page=";
        public static final String ENTEREDGIVEAWAY_URL = STEAMGIFTS_BASE_URL + "/giveaways/entered/search?page=";
        public static final String SUSPENSION_URL = STEAMGIFTS_BASE_URL + "/suspensions";
        public static final String USER_URL = STEAMGIFTS_BASE_URL + "/user/";

        public static final String COMMAND_URL = STEAMGIFTS_BASE_URL + "/ajax.php";
    }

    public static class SteamActivation {
        public static final String STEAMCOMMUNITY_BASE_URL = "http://steamcommunity.com";

        public static final String PROFILE_URL = STEAMCOMMUNITY_BASE_URL + "/profiles/";
        public static final String PROFILE_APPENDIX = "/games?tab=all&xml=1";
    }

}
