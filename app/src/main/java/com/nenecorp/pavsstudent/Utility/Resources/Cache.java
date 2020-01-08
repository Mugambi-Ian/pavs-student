package com.nenecorp.pavsstudent.Utility.Resources;

import com.nenecorp.pavsstudent.DataModel.PavsDatabase;
import com.nenecorp.pavsstudent.Interface.StudentUi.Home;

public class Cache {
    private static Home home;

    public static Home getHome() {
        return home;
    }


    public static void setHome(Home home) {
        if (home == null){
            PavsDatabase.closeBroadcast();
        }
        Cache.home = home;
    }
}