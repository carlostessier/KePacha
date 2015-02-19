package es.carlostessier.kepacha.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.carlostessier.kepacha.R;
import es.carlostessier.kepacha.ui.activities.ViewImageActivity;
import es.carlostessier.kepacha.utils.ParseConstants;

/**
 *
 * Created by carlosfernandez on 30/12/14.
 */
public class InboxFragment extends ListFragment {

    ProgressBar spinner;
    List<ParseObject> mMessages;

    private ArrayList<String> messages;
    private ArrayAdapter adapter;

    final static String TAG = InboxFragment.class.getName();


    @Override
    public void onResume() {
        super.onResume();

        messages= new ArrayList<>();

        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,messages);
        setListAdapter(adapter);

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
                        adapter.add(message.getString(ParseConstants.KEY_SENDER_NAME));
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


        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);

        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);

        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            Intent i = new Intent(getActivity(), ViewImageActivity.class);
            i.setData(fileUri);
            startActivity(i);
        }
        else{
            Intent i = new Intent(Intent.ACTION_VIEW, fileUri);
            i.setDataAndType(fileUri,"video/*" );
            startActivity(i);
        }
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
