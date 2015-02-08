package es.carlostessier.kepacha.ui.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import es.carlostessier.kepacha.utils.ParseConstants;
import es.carlostessier.kepacha.R;


public class EditFriendsActivity extends ListActivity {

    final static String TAG = EditFriendsActivity.class.getName();

    List<ParseUser> mUsers;
    ArrayList<String> usernames;
    ArrayList<String> objectIds;

    ArrayAdapter<String> adapter;

    ProgressBar spinner;

    ParseUser mCurrentUser;
    ParseRelation<ParseUser> mFriendsRelation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        spinner = (ProgressBar)
                findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

    }

    private void setListView() {
        objectIds = new ArrayList<>();
        usernames= new ArrayList<>();

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_checked,usernames);
        setListAdapter(adapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();

        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(ParseConstants.MAX_USERS);


        spinner.setVisibility(View.VISIBLE);



        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null){
                    //sucess
                    mUsers = users;
                    for(ParseUser user:users){
                        objectIds.add(user.getObjectId());
                        adapter.add(user.getUsername());
                    }
                    addFriendCheckmarks();

                }
                else{
                    Log.e(TAG, "ParseException caught: ", e);
                    errorEditFriendsdDialog(getString(R.string.error_message));
                }
            }
        });

    }

    private void addFriendCheckmarks() {

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){

                    //sucess
                   for(ParseUser user:parseUsers){
                       Log.d(TAG, "id " + user.getObjectId());
                       if(objectIds.contains(user.getObjectId()))
                           getListView().setItemChecked(objectIds.indexOf(user.getObjectId()),true);
                   }

                    spinner.setVisibility(View.INVISIBLE);



                }
                else{
                    Log.e(TAG, "ParseException caught: ", e);
                    errorEditFriendsdDialog(getString(R.string.error_message));
                }
            }
        });
    }

    private void errorEditFriendsdDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(R.string.signup_error_title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = builder.create();

        dialog.show();

    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(getListView().isItemChecked(position)) {
            //add friend
            mFriendsRelation.add(mUsers.get(position));

        }
        else{
            //remove friend
            mFriendsRelation.remove(mUsers.get(position));

        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "ParseException caught: ", e);
                    errorEditFriendsdDialog(getString(R.string.error_message));
                }
            }
        });
    }
}
