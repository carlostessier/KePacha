package es.carlostessier.kepacha;

import es.carlostessier.kepacha.ui.activities.LoginActivity;
import es.carlostessier.kepacha.R;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

/**
 * Created by ernesto on 10/02/15.
 */
public class MyTestCase extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Button login;
    private EditText usernameField;
    private EditText passwordField;
    private LoginActivity actividad;

    private static final String LOGIN = "eramiro";
    private static final String PASSWORD = "1 2 3 4";

    public MyTestCase() {
        super(LoginActivity.class);

    }

    protected void setUp() throws Exception {
        super.setUp();
//        setActivityInitialTouchMode(false);
        actividad = getActivity();
        usernameField = (EditText) actividad.findViewById(R.id.usernameField);
        passwordField = (EditText) actividad.findViewById(R.id.passwordField);
        login = (Button) actividad.findViewById(R.id.button);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }




    public void testUserLogin() {
        // nos aseguramos de no estar logueados
        if(ParseUser.getCurrentUser()!=null)
            ParseUser.logOut();

//        on first edit text
        TouchUtils.tapView(this, usernameField);
        getInstrumentation().sendStringSync(LOGIN);
        // sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);

        // on second edit text
        TouchUtils.tapView(this, passwordField);
        this.sendKeys(PASSWORD);

        // now on Add button
        TouchUtils.clickView(this, login);

        assertNotNull("The user is logged", (ParseUser.getCurrentUser()));
    }
}