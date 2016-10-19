package com.rhythm003.app;

/**
 * Created by Rhythm003 on 7/25/2016.
 */
public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://rhythm003.noip.me:8080/type1_api/v1/login";

    // Server user register url
    public static String URL_REGISTER = "http://rhythm003.noip.me:8080/type1_api/v1/register";

    public static String URL_GLU = "http://rhythm003.noip.me:8080/type1_api/v1/glucose";

    public static String FITBIT_TOKEN = "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=227ZCG&redirect_uri=quickpredict%3A%2F%2Ffitbit&scope=activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight&expires_in=604800";

    public static String FITBIT_CAL = "https://api.fitbit.com/1/user/-/foods/log/date/";

    public static String FITBIT_HRATE = "https://api.fitbit.com/1/user/-/activities/heart/date/today/1d/1min.json";
}
