package es.carlostessier.kepacha;

import es.carlostessier.kepacha.ui.activities.LoginActivity;
import es.carlostessier.kepacha.R;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
//		super("com.example.calc", MainActivity.class);
        super(LoginActivity.class);

    }

    protected void setUp() throws Exception {
        super.setUp();
//        setActivityInitialTouchMode(false);
//        ActivityInstrumentationTestCase2.setActivityTouchMode(false);
        actividad = getActivity();
        usernameField = (EditText) actividad.findViewById(R.id.usernameField);
        passwordField = (EditText) actividad.findViewById(R.id.passwordField);
        login = (Button) actividad.findViewById(R.id.button);
        // MainActivity actividad = getActivity();
        // suma = (Button) actividad.findViewById(R.id.button1);
        // suma.requestFocus();
        // suma.performClick();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }




    public void testAddValues() {
        // nos aseguramos de no estar logueados
        if(ParseUser.getCurrentUser()!=null)
            ParseUser.logOut();

        //usernameField.clearComposingText();

        TouchUtils.tapView(this, usernameField);
        getInstrumentation().sendStringSync(LOGIN);
//        this.sendKeys(LOGIN);
        // sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
        // now on value2 entry
        TouchUtils.tapView(this, passwordField);
        this.sendKeys(PASSWORD);
        // now on Add button
        TouchUtils.clickView(this, login);
        // sendKeys("ENTER");
        // get result
        // Log.d("JUNIT", mathResult1);
        assertNotNull("The user is logged", (ParseUser.getCurrentUser()));
    }
/*
    public void testMultiplyValues() {
        TouchUtils.tapView(this, etext1);
        sendKeys(NUMBER_1);
        // now on value2 entry
        TouchUtils.tapView(this, etext2);
        sendKeys(NUMBER_2);
        // now on Multiply button

        // sendKeys("ENTER");
        // get result
        String mathResult2 = etiqueta.getText().toString();
        assertTrue("Multiply result should be 888",
                mathResult2.equals(MUL_RESULT));
    }*/

}