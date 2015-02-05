package com.coretronic.bdt;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Morris on 2014/8/11.
 */
public class AppConfig {


    // Android Download path
    public static final String GOOGLE_PLAY_APP_DOWNLOAD_PATH = "http://goo.gl/UU8mti";

    // HTTP URL
    public static final String DOMAIN_SITE_PATE = "http://cloudap.coretronic.com/health/";
    public static final String FILE_UPLOAD_PATH = "http://cloudap.coretronic.com/health/community/upload_file.php";
    public static final String TONY_COPYRIGHT_WEBSITEPATH = "http://www.tonyhuang39.com";
    public static final String REQUEST_MEMBER_INFO = "requestMemberInfo";
    public static final String REQUEST_DOCTOR_DETAILS = "requestDoctorDetails";
    public static final String REQUEST_DIVSION = "requestDivision";
    public static final String REQUEST_DOCTOR_LIST = "requestDoctorList";
    public static final String REQUEST_DOCTOR_COMMENT = "requestRecommend";
    public static final String CREATE_FIRST_TIME_USE = "createFristTimeUse";
    public static final String REQUEST_DRUGSTORE_TYPE = "requestDrugstoreType";
    public static final String REQUEST_DRUGSTORE_LIST = "requestDrugstoreList";
    public static final String REQUEST_DRUGSTORE_DETAILS = "requestDrugstoreDetails";
    public static final String REQUEST_DRUGSTORE_LOCATION = "requestDrugstoreLocation";
    public static final String REQUEST_DRUGSTORE_RECOMMEND = "requestDrugstoreRecommend";
    public static final String REQUEST_RECOMMEND_DOCTOR = "requestRecommendDoctor";
    public static final String REQUEST_RECOMMEND_QUERY_DOCTOR = "requestRecommendQueryDoctor";
    public static final String INSERT_RECOMMEND_DOCTOR = "insertRecommendDoctor";
    public static final String INSERT_ADD_RECOMMEND_DOCTOR = "insertAddRecommendDoctor";
    public static final String REQUEST_RECOMMEND_QUERY_DRUGSTORE = "requestRecommendQueryDrugstore";
    public static final String REQUEST_RECOMMEND_DRUGSTORE = "requestRecommendDrugstore";
    public static final String INSERT_RECOMMEND_DRUGSTORE = "insertRecommendDrugstore";
    public static final String INSERT_RECOMMEND_DRUG_BOUNS = "insertRecommendDrugBonus";
    public static final String INSERT_ADD_RECOMMEND_DRUGSTORE = "insertAddRecommendDrugStore";
    public static final String UPDATE_REG_ID = "updateRegId";
    public static final String REQUEST_NEWS = "requestNews";
    public static final String REQUEST_NEW_DETAIL = "requestNewDetail";
    public static final String REQUEST_SEARCH_RESULT = "requestSearchResult";
    public static final String REQUEST_FAVORITES_ARTICLES = "requestFavoritesArticles";
    public static final String UPDATE_FAVOR_ARTICLE = "updateFavorArticle";
    public static final String REQUEST_WATCHED_ARTICLES = "requestWatchedArticles";
    public static final String REQUEST_QUESTION_REF = "requestQuestionReference";
    public static final String INSERT_RESTAURANT_QUESTION = "insertRestaurantQuestion";
    public static final String REQUEST_LOCATE_WEATHER = "requestLocateWeather";
    public static final String REQUEST_CURRENT_TEMPAUREATE = "requestCurrentTemp";
    public static final String REQUEST_CURRENT_TEMPAUREATE_LATWITHLOCATE = "requestCurrentTemplatWithLocate";
    public static final String REQUEST_HOME = "requestHome";
    public static final String REQUEST_HOMENEWS = "requestHomeNews";
    public static final String REQUEST_COMMUNITY_DETAIL = "requestCommunityDetail";
    public static final String REQUEST_HOME_MSG = "requestHomeMegs";
    public static final String INSERT_MESSAGE_READ = "insertMessageRead";
    public static final String UPDATE_RECOVER_GOOD = "updateRecoverGood";
    public static final String REQUEST_IN_APP_NOTIFICATION = "requestInAppNotification";
    public static final String UPDATE_IN_APP_NOTIFICATION_STATE = "updateInAppNotificationState";

    // qa method
    public static final String REQUEST_GAME_FRIENDS_RANKS = "requestGameFriendsRanks";
    public static final String REQUEST_GAME_ALLUSERS_RANKS = "requestGameAllUsersRanks";
    public static final String REQUEST_QUESTION = "requestQuestion";
    public static final String REQUEST_INCORRECT = "requestIncorrect";
    public static final String REQUEST_CORRECT = "requestCorrect";

    // step count
    public static final String INSERT_WAY_WALK_COUNT = "insertWayWalkCountB";
    public static final String REQUEST_WAY_WALK_COUNT = "requestWayWalkCount";
    public static final String INSERT_DAILY_WALK_COUNT = "insertDailyWalkCountB";
    public static final String REQUEST_DAILY_WALK_COUNT = "requestDailyWalkCount";

    // near store and restaurant
    public static final String REQUEST_NEAR_RESTAURANTS = "requestNearRestaurants";
    public static final String REQUEST_NEAR_CONVENIENCE = "requestNearConvenience";

    // online doctor
    public static final String requestPatientInfoList = "requestPatientInfoList";
    public static final String requestPatientInfo = "requestPatientInfo";
    public static final String requestBodyInfo = "requestBodyInfo";
    public static final String requestBodyDetailInfo = "requestBodyDetailInfo";
    public static final String requestSymptomsInfo = "requestSymptomsInfo";
    public static final String requestRelativeSymptomsInfo = "requestRelativeSymptomsInfo";
    public static final String requestRelativeQuestion = "requestRelativeQuestion";
    public static final String requestConditionInfo = "requestConditionInfo";

    // health knowledge
    public static final String REQUEST_ALL_ARITCLE = "requestArticle";
    public static final String REQUEST_FAVOR_ARTICLE = "requestFavorArticle";
    public static final String REQUEST_REVIEWED = "requestReviewed";

    // WalkWay method
    public static final String REQUEST_WALKWAYS_LOCATION = "requestWalkwayLocation";
    public static final String REQUEST_WALKWAYS_FEATURE = "requestWalkwaySearchInfo";
    public static final String REQUEST_WALKWAY_DETAILS = "requestWalkwayDetails";
    public static final String REQUEST_WALKWAYS_LIST = "requestWalkwayList";
    public static final String REQUEST_WALKWAYS_AREA_LIST = "requestWalkwayAchievementAreaList";
    public static final String REQUEST_WALKWAYS_AREA_FRIEND_DETAIL = "requestWalkwayAchievement";
    public static final String REQUEST_WALKWAYS_AREA_USER_VISITED = "requestWalkwayAchievementDetails";
    public static final String WALKWAYS_CURRENT_WALKWAYID = "walkWaysCurrentWalkWayID";
    public static final String WALKWAYS_CURRENT_WALKWAYNAME = "walkWaysCurrentWalkWayName";
    public static final String REQUEST_WALKWAYS_FRIEND_RANKS = "requestFriendRanks";
    public static final String REQUEST_WALKWAYS_ALL_USERS_RANKS = "requestAllUsersRanks";
    public static final String INSERT_WALKWAYS_RECORD = "insertWalkwayRecord";
    public static final String REQUEST_WALKWAYS_VISITS = "requestWalkwayVisits";


    // Diary method
    public static final String INSERT_DAILY_NOTE = "insertDailyNote";

    // Wall Message
    public static final String REQUEST_WALL_MESSAGE_LIST = "requestCommunityList";
    public static final String REQUEST_WALL_COMMUNITY_DETAIL = "requestCommunityDetail";
    public static final String REQUEST_ARTICLE_MESSAGES = "requestUserMessages";
    public static final String REQUEST_ARTICLE_GOODS = "requestUserGoods";
    public static final String INSERT_GOOD = "insertGood";
    public static final String REMOVE_GOOD = "updateRecoverGood";

    public static final String INSERT_MESSAGE = "insertMessage";
    public static final String INSERT_COMMENT = "insertComment";



    // Account authentication
    // 會員註冊
//    public static final String USER_AUTH_SITE_PATE = "http://houspital.coretronic.com/health/api";
    public static final String USER_AUTH_SITE_PATE = "http://cloudap.coretronic.com/health/api";
    public static final String USER_AUTH_REGISTER = "/user/register";
    public static final String USER_AUTH_LOGIN = "/user/login";
    public static final String USER_AUTH_SMS = "/user/sms";
    public static final String USER_AUTH_VERIFY_REGISTER = "/user/verify/register";
    public static final String USER_AUTH_VERIFY_LOGIN = "/user/verify/login";
    public static final String USER_INFO = "/user/info";
    public static final String USER_UPDATE = "/user/update";

    // 好友列表
    public static final String INVITE_TYPE_INVITE_ME = "InviteMe";
    public static final String INVITE_TYPE_INVITED = "Invited";
    public static final String INVITE_TYPE_MY_FRIEND = "MyFriend";
    public static final String API_USER_THUMB = "api/user/thumb";
    public static final String API_FRIEND_LIST = "api/friend/list";
    public static final String API_FRIEND_SEARCH = "api/friend/search";
    public static final String API_FRIEND_BREAK = "api/friend/break";
    public static final String API_FRIEND_INVITE_SEND = "api/friend/invite/send";
    public static final String API_FRIEND_INVITE_ACCEPT = "api/friend/invite/accept";
    public static final String API_FRIEND_INVITE_CANCEL = "api/friend/invite/cancel";
    public static final String API_FRIEND_INVITE_REJECT = "api/friend/invite/reject";
    public static final String API_FRIEND_INVITE_AUTO = "api/friend/invite/auto";
    public static final String BUNDLE_ARGU_UID = "uid";
    public static final String BUNDLE_ARGU_FID = "fid";
    public static final String BUNDLE_ARGU_TEL = "tel";
    public static final String BUNDLE_ARGU_TEL_LIST = "tel_list";
    public static final String BUNDLE_ARGU_THUMB = "thumb";
    public static final String BUNDLE_ARGU_NAME = "name";
    public static final String BUNDLE_ARGU_STATE = "state";
    public static final String PREF_IS_ENABLED_AUTO_INVITE = "AUTO_INVITE";
    public static final String PREF_LAST_LOCAL_PHONE_COUNT = "LAST_LOCAL_PHONE_COUNT";

    // bundle key
    public static final String ARTICLE_BUNDLE_KEY = "allArticleKey";
    public static final String NEWS_ID_KEY = "newsIdKey";
    public static final String NEWS_STATUS = "NEWS_STATUS";
    public static final String ENTERAPP_SOURCES = "enterAppSources";
    public static final String PUSHNOTIFY = "pushNotify";
    public static final String PHONENUM = "phoneNum";
    public static final String FRAGMENT_NAME = "fragmentName";
    public static final String PERSON_INFO = "personInfo";
    public static final String MAIN_LAT_KEY = "MAIN_LAT";
    public static final String MAIN_LNG_KEY = "MAIN_LNG";
    public static final int TIMEOUT = 10000;
    //Paras Config
    public static final String REG_ID = "regId";
    public static final String SHAREDPREFERENCES_NAME = "bdt";
    public static final String PREF_IS_LOGIN = "PREF_IS_LOGIN";
    public static final String PREF_IS_CREATED = "PREF_IS_CREATED";
    public static final String PREF_IS_LOG_CREATED = "PREF_IS_LOG_CREATED";

    //    public static final String PREF_USER_BIRTHDAY = "PREF_USER_BIRTHDAY";
//    public static final String PREF_USER_SEX = "PREF_USER_SEX";
    public static final String PREF_USER_GRADE = "PREF_USER_GRADE";
    public static final String PREF_APP_VERSION = "PREF_APP_VERSION";
    public static final String PREF_FIRST_USED = "PREF_FIRST_USED";

    public static final String PREF_DIVITION_INFO = "PREF_DIVITION_INFO";
    public static final String PREF_DRUGSTORE_TYPE = "PREF_DRUGSTORE_TYPE";
    public static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public static final String PREF_DEVICE_ID = "PREF_DEVICE_ID";
    public static final String PREF_FIRST_TIME_USE = "PREF_FIRST_TIME_USE";
    public static final String PREF_RECOMMEND_DOCTOR_NAME = "PREF_RECOMMEND_DOCTOR_NAME";
    public static final String PREF_RECOMMEND_DOCTOR_ID = "PREF_RECOMMEND_DOCTOR_ID";
    public static final String PREF_RECOMMEND_HOSPITAL_NAME = "PREF_RECOMMEND_HOSPITAL_NAME";
    public static final String PREF_RECOMMEND_AREA_NAME = "PREF_RECOMMEND_AREA_NAME";
    public static final String PREF_RECOMMEND_AREA_ID = "PREF_RECOMMEND_AREA_ID";
    public static final String PREF_RECOMMEND_DIVISION_NAME = "PREF_RECOMMEND_DIVISION_NAME";
    public static final String PREF_RECOMMEND_PHARMACY_NAME = "PREF_RECOMMEND_PHARMACY_NAME";
    public static final String PREF_RECOMMEND_PHARMACY_ID = "PREF_RECOMMEND_PHARMACY_ID";
    public static final String PREF_RECOMMEND_PHARMACY_TYPE = "PREF_RECOMMEND_PHARMACY_TYPE";
    public static final String PREF_SCREEN_HEIGHT = "PREF_SCREEN_HEIGHT";
    public static final String PREF_SCREEN_WIDTH = "PREF_SCREEN_WIDTH";
    public static final String PREF_DATE_KEY = "PREF_DATE_KEY";
    public static final String PREF_STEP_KEY = "PREF_STEP_KEY";
    public static final String PREF_STEP_KEY_FOR_WIDGET = "PREF_STEP_KEY_FOR_WIDGET";
    public static final String PREF_TIME_KEY = "PREF_TIME_KEY";

    // User info
    public static final String PREF_USER_NANE = "PREF_USER_NAME";
    public static final String PREF_USER_THUMB = "PREF_USER_THUMB";
    public static final String PREF_USER_BIRTHDAY = "PREF_USER_BIRTHDAY";
    public static final String PREF_USER_ADDRESS = "PREF_USER_ADDRESS";
    public static final String PREF_USER_SEX = "PREF_USER_SEX";
    public static final String PREF_USER_PHONE = "PREF_USER_PHONE";
    public static final String PREF_USER_AGE = "PREF_USER_AGE";

    // health qa
    public static final String PREF_FRIENDS_RANK_DATA_KEY = "PREF_FRIENDS_RANK_DATA_KEY";
    public static final String PREF_ALLMEMBERS_RANK_DATA_KEY = "PREF_ALLMEMBERS_RANK_DATA_KEY";
    public static final String PREF_CHALLENGE_ID = "PREF_CHALLENGE_ID";

    //widget
    public static final String PREF_LOCATION_TEMP = "PREF_LOCATION_TEMP";
    public static final String PREF_LOCATION_COUNTRY = "PREF_LOCATION_COUNTRY";
    public static final String PREF_LOCATION_RAIN = "PREF_LOCATION_RAIN";
    public static final String PREF_MSG_COUNT = "PREF_MSG_COUNT";
    public static final String PREF_STEP_KEY_WIDGET = "PREF_STEP_KEY_WIDGET";
    public static final String PREF_STEP_KEY_WIDGET_FOR_UPLOAD = "PREF_STEP_KEY_WIDGET_FOR_UPLOAD";
    public static final String PREF_STEP_KEY_WIDGET_FOR_VIEW = "PREF_STEP_KEY_WIDGET_FOR_VIEW";
    public static final String PREF_WIDGET_LAT = "PREF_WIDGET_LAT";
    public static final String PREF_WIDGET_LNG = "PREF_WIDGET_LNG";

    public final static String EAT_STATUS_KEY = "EAT_STATUS_KEY";
    public final static String EAT_TIME_KEY = "EAT_TIME_KEY";
    public static final String PREF_GOOGLE_SERVICE = "PREF_GOOGLE_SERVICE";
    public static final String PREF_CURRENT_LATITUDE = "PREF_CURRENT_LATITUDE";
    public static final String PREF_CURRENT_LONGITUDE = "PREF_CURRENT_LONGITUDE";
    public static final String PREF_LAST_COUNTRY = "PREF_LAST_COUNTRY";
    public static final String PREF_SHORTCUT = "PREF_SHORT_CUT";


    public final static String CLEAN_STATUS_KEY = "CLEAN_STATUS_KEY";
    public final static String CLEAN_TIME_KEY = "CLEAN_TIME_KEY";
    public final static String PLAY_TIME_KEY = "PLAY_TIME_KEY";
    public final static String PHOTO_CATEGORY_SOURCE = "PHOTO_CATEGORY_SOURCE";

    public final static String SELECT_PIC_SELECT_PATH_KEY = "SELECT_PIC_KEY";
    public final static int CAMERA_REQUEST = 66;
    public final static int ALBUMS_REQUEST = 99;
    public final static int DIRECT_REQUEST_RECORD = 77;
    public final static int ALBUMS_REQUEST_KITKAT = 98;

    public final static String APP_TRANSPORT_PATH_SD_CARD = "transport";
    public final static String APP_PATH_SD_CARD = "/BDT/";
    public static final String JPEG_FILE_FOLDER_NAME = "BDT";
    public static final String JPEG_FILE_PREFIX = "BDT";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String JPEG_TRANSFILE_NAME_STEP1_KEY = "JPEG_TRANSFILE_NAME_STEP1_KEY";
    public static final String JPEG_TRANSFILE_NAME_STEP2_KEY = "JPEG_TRANSFILE_NAME_STEP2_KEY";
    public static final String JPEG_TRANSFILE_NAME_STEP3_KEY = "JPEG_TRANSFILE_NAME_STEP3_KEY";
    public static final String JPEG_TRANSFILE_NAME_STEP4_KEY = "JPEG_TRANSFILE_NAME_STEP4_KEY";
    public static final String JPEG_TRANSFILE_NAME_STEP5_KEY = "JPEG_TRANSFILE_NAME_STEP5_KEY";
    public static final String WALKWAY_CAMERA_DATE = "WALKWAY_CAMERA_DATE";
    public static final String JPEG_USER_LOGO_FOLDER = "/BDT/UserLogo";
    public static final String JPEG_WALL_IMAGE_FOLDER = "/BDT/WallImage";

    public static final int PHOTO_SIZE = 768;
    public static final int PHOTO_SIZE_256 = 256;
    public static final int PHOTO_SIZE_512 = 512;
    public static final int PHOTO_SIZE_1024 = 1024;


    // Diary stackshare preference key
    // current status: 11 -> mission1 select photo page    12-> edit text page
    // current status: 21 -> mission2 select photo page    22-> edit text page
    // 儲存現在在哪個頁面的Key
    public static final String PREF_DIARY_CURRENT_STATUS_KEY = "PREF_DIARY_CURRENT_STATUS_KEY";

    public static final String PREF_DIARY_PRE_WALKWAY_NAME_KEY = "PREF_DIARY_PRE_WALKWAY_NAME_KEY";
    public static final String PREF_DIARY_PRE_WALKWAY_ID_KEY = "PREF_DIARY_PRE_WALKWAY_ID_KEY";
    // 儲存任務1~5頁面文字內容的Key
    public static final String PREF_DIARYSTEP1_EDIT_CONTENT_KEY = "PREF_DIARYSTEP1_EDIT_CONTENT_KEY";
    public static final String PREF_DIARYSTEP2_EDIT_CONTENT_KEY = "PREF_DIARYSTEP2_EDIT_CONTENT_KEY";
    public static final String PREF_DIARYSTEP3_EDIT_CONTENT_KEY = "PREF_DIARYSTEP3_EDIT_CONTENT_KEY";
    public static final String PREF_DIARYSTEP4_EDIT_CONTENT_KEY = "PREF_DIARYSTEP4_EDIT_CONTENT_KEY";
    public static final String PREF_DIARYSTEP5_EDIT_CONTENT_KEY = "PREF_DIARYSTEP5_EDIT_CONTENT_KEY";

    //    儲存任務1~5頁面是否是略過(False)還是有進入編輯(True)
    public static final String PREF_DIARYSTEP1_EDIT_KEY = "PREF_DIARYSTEP1_EDIT_KEY";
    public static final String PREF_DIARYSTEP2_EDIT_KEY = "PREF_DIARYSTEP2_EDIT_KEY";
    public static final String PREF_DIARYSTEP3_EDIT_KEY = "PREF_DIARYSTEP3_EDIT_KEY";
    public static final String PREF_DIARYSTEP4_EDIT_KEY = "PREF_DIARYSTEP4_EDIT_KEY";
    public static final String PREF_DIARYSTEP5_EDIT_KEY = "PREF_DIARYSTEP5_EDIT_KEY";
    public static final String PREF_DIARYSTEP_LAST_KEY = "PREF_DIARYSTEP_LAST_KEY";


    public final static int PREF_DIARYSTEP1_STATUS_ID = 11;
    public final static int PREF_DIARYSTEP1EDIT_STATUS_ID = 12;
    public final static int PREF_DIARYSTEP2_STATUS_ID = 21;
    public final static int PREF_DIARYSTEP2EDIT_STATUS_ID = 22;
    public final static int PREF_DIARYSTEP3_STATUS_ID = 31;
    public final static int PREF_DIARYSTEP3EDIT_STATUS_ID = 32;
    public final static int PREF_DIARYSTEP4_STATUS_ID = 41;
    public final static int PREF_DIARYSTEP4EDIT_STATUS_ID = 42;
    public final static int PREF_DIARYSTEP5_STATUS_ID = 51;
    public final static int PREF_DIARYSTEP5EDIT_STATUS_ID = 52;
    public final static int PREF_DIARYSTEP_LAST_ID = 61;


    public static final String[] STEP1_SENTENCE_ARY = new String[]{
            "第一次來",
            "要出發了",
            "看起來很不錯",
            "揮揮手",
            "哈哈",
            "被騙來這裡",
            "排隊排很長",
            "人很多"};

    public static final String[] STEP2_SENTENCE_ARY = new String[]{
            "哈哈，被擋到了",
            "眼睛閉起來了啦",
            "我比旁邊的帥",
            "我比旁邊的美",
            "揮揮手",
            "西瓜甜不甜，甜~~",
            "Yeah~~",
            "多年的老朋友"};

    public static final String[] STEP3_SENTENCE_ARY = new String[]{
            "天氣很棒",
            "這裡非常漂亮",
            "風景不錯",
            "哈哈，太陽好大",
            "人山人海",
            "心曠神怡",
            "值得多來幾次",
            "到此一遊"};

    public static final String[] STEP4_SENTENCE_ARY = new String[]{
            "活力十足",
            "我跳",
            "瀟灑走一回",
            "Yeah~~",
            "哈哈，有夠醜的",
            "笑得開懷最好看",
            "永遠年輕",
            "這樣有很帥嗎"};

    public static final String[] STEP5_SENTENCE_ARY = new String[]{
            "這是我們的大合照",
            "今天非常開心",
            "這張超好看的",
            "有緣一起出遊",
            "真的很難得",
            "期待下次出遊",
            "下次要再一起來",
            "大成功"};




    // main turtal game config
    // 檢查的時間(多久檢查一次 秒*1000 = 毫秒)
    public final static int CHECK_TIME = 10 * 1000;
    // 多久會想洗澡  timer
    public final static int CLEAN_TIME = 8;
    // 多久會肚子餓(時) timer
    public final static int HUNGRY_TIME = 4;

    // App開始狀態檢查食、清潔秒數
    public final static int APP_TURNON_EAT_CHECK_DURATION = 5 * 1000;
    public final static int APP_TURNON_CLEAN_CHECK_DURATION = 5 * 1000;
    // location interval
    public final static int LOCATION_INTERVAL = 1000*60*10;
    public final static int LOCATION_FASTINTERVAL = 1000*5*60;
    public final static double LOCATION_DEFAULT_LAT = 24.711808;
    public final static double LOCATION_DEFAULT_LNG = 120.915872;
    public final static String LOCATION_DEFAULT_AREA = "苗栗縣";
    public final static String LOCATION_DEFAULT_LOCATE = "竹南鎮";

    // Tutorial movie path
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mm2.pcslab.com/mm/7m1000.mp4";
    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/Tutorial_mobile.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/Tutorial_mobile2.mp4";


//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mm2.pcslab.com/mm/7m1000.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/MVI_6924.mp4";
//
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mm2.pcslab.com/mm/7m1000.mp4";
   // public static final String TUTORIAL_VIDEO_PATH = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";

//     public static final String TUTORIAL_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/PhotoshopCS6TutorialFactory.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/dontbefriend.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "http://192.168.202.8/ampache/play/index.php?ssid=28e1021e55c620c05a795e01a278d61b&type=song&oid=52&uid=2&name=/Unknown%20%28Orphaned%29%20-%20Photoshopcs6tutorialfactory-1.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "http://192.168.202.8/ampache/play/index.php?ssid=28e1021e55c620c05a795e01a278d61b&type=song&oid=52&uid=2&name=/Unknown%20%28Orphaned%29%20-%20dontbefriend.mp4";
//    public static final String TUTORIAL_VIDEO_PATH = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

    // goodfoodfinder movie path
//    public static final String GoodFoodFinder_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/MVI_6924.mp4";
    public static final String GoodFoodFinder_VIDEO_PATH = "rtsp://mediaserver.coretronic.com/findgoodfood1.mp4";

    // wet webservice params
    public static final String SUCCESS_CODE = "A01";
    public static final String EROOR_CODE_NOMETHOD = "E10";
    public static final String ERROR_CODE = "E";
    public static final String DIVISION = "division";
    public static final String STORE_TYPE = "store_type";

    //    public static final String DEFAULT_PLACE = "竹南鎮";
    public static final String DEFAULT_PLACE = "";

    public static final String[] CITY = new String[]{"全部", "台北市", "新北市", "基隆市", "桃園縣", "新竹市", "新竹縣", "苗栗縣", "台中市", "彰化縣", "南投縣", "雲林縣", "嘉義市", "嘉義縣", "台南市", "高雄市", "屏東縣", "宜蘭縣", "花蓮縣", "台東縣", "連江縣", "澎湖縣", "金門縣", "南海島"};

    public static final String[] USER_AGE = new String[]{
            "老年(65歲以上)",
            "中年(40歲-64歲)",
            "青年(13歲-39歲)",
            "兒童(1歲-12歲)"
    };
    public static final String[] USER_CAREER = new String[]{
            "教師",
            "勞工",
            "農夫",
            "商人"
    };

    // Google map
    public static final int MAP_DEFAULT_ZOOM = 13;
    public static final int MAP_DEFAULT_SHOW_DISTANCE = 10000;

    public static final LatLng MAP_DEFAULT_LOCATION = new LatLng(24.712051, 120.916440);
    // Request int
    public static final int RQS_GooglePlayServices = 100;


    // show list data the once time
    public static final int SHOW_CONTENT_NUMBER = 10;
    // Health Knowledge params
    public static final String CANCLE_FAVOR_ARTICLE_ID = "0";
    public static final String FAVOR_ARTICLE_ID = "1";

    public static final String LINE_PACKAGE_NAME = "jp.naver.line.android";
    public static final String LINE_CLASS_NAME = "jp.naver.line.android.activity.selectchat.SelectChatActivity";


    public static final String RETURN_YES = "yes";
    public static final String RETURN_NO = "no";


    // SMS
    public static final String SMS_INTENT_FILTER = "com.coretronic.sms.intent";
    public static final String SMS_PROF = "sms_msg";

    //我要找好食
    public static final String[] GoodFoodFinderQuestion = new String[]{
            "您平常三餐大多是自己煮或是吃外面的食物 ?",
            "您與朋友聊天時會分享哪裡有美食嗎 ?",
            "您平常會喜歡自己下廚後，找朋友來家裡分享嗎 ?",
            "您在意食材是否健康(如：產地、有機、化學加工等 )嗎 ?",
            "剛剛的影片，「我要找好食」對您來說有吸引力嗎 ?"
    };
    public static final String[] GoodFoodFinderAnswer01 = new String[]{
            "自己煮比較多",
            "會，美食常是話題",
            "經常找朋友來吃",
            "是，非常在意",
            "有，覺得有趣實用"
    };
    public static final String[] GoodFoodFinderAnswer02 = new String[]{
            "吃外面比較多",
            "很少聊美食",
            "很少找朋友來吃",
            "偶爾在意",
            "沒有吸引力及興趣"
    };



}
