package com.rhythm003.app;

/**
 * Created by Rhythm003 on 7/25/2016.
 * URLs of APIs
 */
public class AppConfig {
    // Remote Server user login url
    public static String URL_LOGIN = "http://rhythm003.noip.me:8080/type1_api/v1/login";

    // Remote Server user register url
    public static String URL_REGISTER = "http://rhythm003.noip.me:8080/type1_api/v1/register";

    // Remote Server get glucose level
    public static String URL_GLU = "http://rhythm003.noip.me:8080/type1_api/v1/glucose";

    // Fitbit get calorie data
    public static String FITBIT_CAL = "https://api.fitbit.com/1/user/-/foods/log/date/";

    // Fitbit get heart rate data
    public static String FITBIT_HRATE = "https://api.fitbit.com/1/user/-/activities/heart/date/today/1d/1min.json";

    // Fitbit get heart rate test
    public static String FITBIT_HRATE_TEST = "https://api.fitbit.com/1/user/-/activities/heart/date/2016-10-12/1d/1min.json";

    // Fitbit get token
    public static String FITBIT_TOKEN = "https://api.fitbit.com/oauth2/token";

    // Fitbit get auth code. Need to change it once you use another Fitbit api key.
    public static String FITBIT_CODE = "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=227ZCG&redirect_uri=quickpredict%3A%2F%2Ffitbit&scope=activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight&expires_in=604800";
}
