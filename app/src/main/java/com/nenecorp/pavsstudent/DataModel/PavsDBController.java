package com.nenecorp.pavsstudent.DataModel;

public class PavsDBController {
    private static PavsDB pavsDB;

    public interface EventHandler {
        void onLoaded(PavsDB database);
    }


    public PavsDBController(EventHandler handler) {
        new PavsDB(pavsDatabase -> {
            PavsDBController.pavsDB = pavsDatabase;
            handler.onLoaded(pavsDatabase);
        });
    }

    public static boolean isLoaded() {
        return pavsDB != null;
    }

    public static PavsDB getDatabase() {
        return pavsDB;
    }
}
