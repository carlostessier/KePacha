package es.carlostessier.kepacha;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Carlos on 17/11/2014.
 */
public class KepachaApplication extends Application{

    @Override
    public void onCreate() {
        Parse.initialize(this, "pi7Y6GwytavFRn1uKXlZOmg2EZst80YWe0lT6Eot", "Z0cyJemlwD5eWet54jv3bBm3vKt2SraRxE2WNenF");


/*
        ParseObject testObject = new
                ParseObject("TestObject");
        testObject.put("foo","bar");
        testObject.saveInBackground();
        */

    }

}
