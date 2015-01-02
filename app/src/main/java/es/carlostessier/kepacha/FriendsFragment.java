package es.carlostessier.kepacha;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosfernandez on 30/12/14.
 */
public class FriendsFragment extends ListFragment {

    final static String TAG = FriendsFragment.class.getName();

    List<ParseUser> mUsers;
    ArrayList<String> usernames;

    ArrayAdapter<String> adapter;

    ProgressDialog dialog;

    ParseUser mCurrentUser;
    ParseRelation<ParseUser> mFriendsRelation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setListView();

        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);


        dialog = ProgressDialog.show(getActivity(),
                getString(R.string.edit_friends_message),
                getString(R.string.waiting_message), true);

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null){
                    //sucess
                    dialog.dismiss();
                    mUsers = users;
                    for(ParseUser user:users){
                        adapter.add(user.getUsername());
                    }

                }
                else{
                    Log.e(TAG, "ParseException caught: ", e);
                    errorEditFriendsdDialog(getString(R.string.error_message));
                }
            }
        });

    }

    private void setListView() {
        usernames= new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,usernames);
        setListAdapter(adapter);


    }

    private void errorEditFriendsdDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle(R.string.signup_error_title);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog dialog = builder.create();

        dialog.show();

    }
}
