package es.carlostessier.kepacha;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends Activity {

    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
    }


    private void initializeViews() {
        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
    }


    private void errorFieldDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(R.string.signup_error_title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public void actionLoginButton(View v){

        String sUsername = usernameField.getText().toString().trim();
        String sPassword = passwordField.getText().toString().trim();


        String message ;

        if (sUsername.isEmpty())
        {
            message = String.format(getString(R.string.empty_field_message),getString(R.string.username_hint));
            errorFieldDialog(message);
        }
        else if (sPassword.isEmpty())
        {
            message = String.format(getString(R.string.empty_field_message),getString(R.string.password_hint));
            errorFieldDialog(message);

        }

        else{
            loginUser(sUsername, sPassword);
        }



    }


    private void loginUser(final String username, String password) {


        setProgressBarIndeterminateVisibility(true);


      final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,
                getString(R.string.loging_message),
                getString(R.string.waiting_message), true);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    dialog.dismiss();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    String message = String.format(getString(R.string.login_user_error_message), username);
                    errorFieldDialog(message);
                }
            }
        });

    }

    public void SignUpOnClick(View v){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
