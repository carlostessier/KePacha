package es.carlostessier.kepacha.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import es.carlostessier.kepacha.R;
import es.carlostessier.kepacha.ui.fragments.SectionsPagerAdapter;
import es.carlostessier.kepacha.utils.FileUtilities;
import es.carlostessier.kepacha.utils.ParseConstants;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    final static String TAG = MainActivity.class.getName();

    static final int TAKE_PHOTO_REQUEST = 0;
    static final int TAKE_VIDEO_REQUEST = 1;
    static final int PICK_PHOTO_REQUEST = 2;
    static final int PICK_VIDEO_REQUEST = 3;
    private static final String IMAGE_FILTER = "image/*";
    private static final String VIDEO_FILTER = "video/*";
    private final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;


    Uri mMediaUri;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "No está logueado");
            navigateToLogin();
        } else {
            Log.d(TAG, "usuario " + currentUser.getUsername() + " logueado");
        }


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //noinspection SimplifiableIfStatement
            case R.id.action_logout:
                Log.d(TAG, "usuario " + ParseUser.getCurrentUser().getUsername() + " desconectado");
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                dialogCameraChoices();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogCameraChoices() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private DialogInterface.OnClickListener mDialogListener() {

        return
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                takeVideo();
                                break;
                            case 2:
                                choosePhoto();
                                break;
                            case 3:
                                chooseVideo();
                                break;
                        }
                    }
                };

    }

    private DialogInterface.OnClickListener mWarningDialogListener() {

        return
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseVideoIntent.setType(VIDEO_FILTER);
                        startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    }
                };

    }


    private void choosePhoto() {
        Log.d(TAG, "Choose picture");
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType(IMAGE_FILTER);
        startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
    }

    private void chooseVideo() {
        Log.d(TAG, "Choose video");
        warningSizeDialog(getString(R.string.size_warning_message));

    }

    private void takeVideo() {
        Log.d(TAG, "Take video");
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            errorDialog(MainActivity.this,
                    R.string.error_file_too_big,
                    R.string.signup_error_title,
                    android.R.drawable.ic_dialog_alert);
        } else {
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
        }
    }


    private void takePhoto() {
        Log.d(TAG, "Take picture");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            errorDialog(MainActivity.this,
                    R.string.error_external_storage,
                    R.string.signup_error_title,
                    android.R.drawable.ic_dialog_alert);
        } else {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
        }
    }


    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_VIDEO_REQUEST || requestCode == PICK_PHOTO_REQUEST) {
                if (data != null) {
                    mMediaUri = data.getData();
                } else {
                    errorDialog(MainActivity.this,
                            R.string.error_file_too_big,
                            R.string.signup_error_title,
                            android.R.drawable.ic_dialog_alert);
                }

                if (requestCode == PICK_VIDEO_REQUEST) {
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();

                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Caught FileNotFoundException", e);
                    } catch (IOException e) {
                        Log.e(TAG, "Caught IOException", e);
                    } finally {
                        if (inputStream != null)
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Caught IOException", e);
                            }
                    }

                    if (fileSize > FILE_SIZE_LIMIT) {
                        errorDialog(MainActivity.this,
                                R.string.error_file_too_big,
                                R.string.signup_error_title,
                                android.R.drawable.ic_dialog_alert);
                    }

                }
            } else {
                Log.e(TAG, "add image to the gallery");

                Intent mediaScantIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScantIntent.setData(mMediaUri);
                sendBroadcast(mediaScantIntent);
            }
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);

            String fileType;

            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)
                fileType = ParseConstants.TYPE_IMAGE;
            else fileType = ParseConstants.TYPE_VIDEO;

            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);

            recipientsIntent.setData(mMediaUri);


            startActivity(recipientsIntent);
        } else if (resultCode != RESULT_CANCELED) {
            errorDialog(MainActivity.this, R.string.error_message, R.string.signup_error_title, android.R.drawable.ic_dialog_alert);
        }
    }

    private void errorDialog(Context context, int messageId, int titleId, int iconId) {
        String message = getString(messageId);
        Log.e(TAG, message);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(titleId);
        builder.setIcon(iconId);
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void warningSizeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, mWarningDialogListener());
        builder.setTitle(R.string.warning_title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = builder.create();

        dialog.show();

    }

}
