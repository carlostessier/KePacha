package es.carlostessier.kepacha.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.carlostessier.kepacha.MessageAdapter;
import es.carlostessier.kepacha.R;
import es.carlostessier.kepacha.utils.ParseConstants;

/**
 *
 * Created by carlosfernandez on 30/12/14.
 */
public class InboxFragment extends ListFragment {

    ProgressBar spinner;
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<String> messages;
    private ArrayAdapter adapter;

    final static String TAG = InboxFragment.class.getName();


    @Override
    public void onResume() {
        super.onResume();

        messages= new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT );

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null) {
                    mMessages = parseObjects;

                    for(ParseObject message: mMessages){
//                        adapter.add(message.getString(ParseConstants.KEY_SENDER_NAME));
                        MessageAdapter adapter2 = new MessageAdapter(getListView().getContext(),mMessages);
                        setListAdapter(adapter2);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        spinner = (ProgressBar)
                rootView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1,R.color.swipeRefresh2,R.color.swipeRefresh3,R.color.swipeRefresh4);

        return rootView;
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
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Toast.makeText(getActivity(),"We're refreshing!", Toast.LENGTH_SHORT).show();
            if (mSwipeRefreshLayout.isRefreshing())
            {
            mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };
}
