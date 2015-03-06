package es.carlostessier.kepacha.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.carlostessier.kepacha.R;
import es.carlostessier.kepacha.UserAdapter;
import es.carlostessier.kepacha.utils.ParseConstants;

/**
 *
 * Created by carlosfernandez on 30/12/14.
 * interfaces00 branch
 */
public class FriendsFragment extends Fragment {

    protected GridView mGridView;
    final static String TAG = FriendsFragment.class.getName();

    List<ParseUser> mUsers;
    ArrayList<String> usernames;

//    ArrayAdapter<String> adapter;
    UserAdapter adapter;

    ProgressBar spinner;

    ParseUser mCurrentUser;
    ParseRelation<ParseUser> mFriendsRelation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        spinner = (ProgressBar)
                rootView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();



        mCurrentUser = ParseUser.getCurrentUser();

        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);


        spinner.setVisibility(View.VISIBLE);


        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null){
                    //sucess
                    spinner.setVisibility(View.INVISIBLE);
                    mUsers = users;

                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for(ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

//                    for(ParseUser user:mUsers){
//                        adapter.add(user);
//                    }

                    if (mGridView.getAdapter() == null) {
                        adapter = new UserAdapter(getActivity(), mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mUsers);

                    }
                    mGridView.setAdapter(adapter);

                }
                else{
                    Log.e(TAG, "ParseException caught: ", e);
                    errorEditFriendsdDialog(getString(R.string.error_message));
                }
            }
        });

//        setmGridView();
    }

//    private void setmGridView() {
////        usernames= new ArrayList<>();
////
////        adapter = new ArrayAdapter<>(
////                getActivity(),
////                android.R.layout.simple_list_item_1,usernames);
//
////useradapter
//
//    }

//    }

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
