package com.cw.androidclient.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cw.androidclient.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Presents dialog for choosing lobby
 */
public class KeyPicker extends DialogFragment {
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker0;
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker1;
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker2;
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker3;
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker4;
    /**
     * Wheel for choosing one symbol of game key
     */
    NumberPicker charPicker5;

    /**
     * Client id
     */
    private final long clientId;

    private String login;

    /**
     * MainActivity context
     */
    private final Context context;

    /**
     * Constructor, sets name and context
     *
     * @param clientId client id
     * @param context  context
     */
    KeyPicker(long clientId, Context context, String login) {
        this.clientId = clientId;
        this.context = context;
        this.login = login;
    }

    /**
     * Initalizes everything in dalog when created
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        View root = getActivity().getLayoutInflater().
                inflate(R.layout.fragment_key_picker, null);
        charPicker0 = root.findViewById(R.id.char0);
        charPicker1 = root.findViewById(R.id.char1);
        charPicker2 = root.findViewById(R.id.char2);
        charPicker3 = root.findViewById(R.id.char3);
        charPicker4 = root.findViewById(R.id.char4);
        charPicker5 = root.findViewById(R.id.char5);
        charPicker0.setMinValue(0);
        charPicker0.setMaxValue(alphabet.length - 1);
        charPicker0.setDisplayedValues(alphabet);
        charPicker1.setMinValue(0);
        charPicker1.setMaxValue(alphabet.length - 1);
        charPicker1.setDisplayedValues(alphabet);
        charPicker2.setMinValue(0);
        charPicker2.setMaxValue(alphabet.length - 1);
        charPicker2.setDisplayedValues(alphabet);
        charPicker3.setMinValue(0);
        charPicker3.setMaxValue(alphabet.length - 1);
        charPicker3.setDisplayedValues(alphabet);
        charPicker4.setMinValue(0);
        charPicker4.setMaxValue(alphabet.length - 1);
        charPicker4.setDisplayedValues(alphabet);
        charPicker5.setMinValue(0);
        charPicker5.setMaxValue(alphabet.length - 1);
        charPicker5.setDisplayedValues(alphabet);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogBW);
        builder.setTitle("?????????????? ?????????? ????????????, ?? ?????????????? ???????????? ????????????????????????????");
        builder.setView(root);
        builder.setPositiveButton(R.string.join, (dialog, which) -> {
            String result = alphabet[charPicker0.getValue()] + alphabet[charPicker1.getValue()]
                    + alphabet[charPicker2.getValue()] + alphabet[charPicker3.getValue()] +
                    alphabet[charPicker4.getValue()] + alphabet[charPicker5.getValue()];
            JSONObject jsonObjectWithBody = new JSONObject();
            try {
                jsonObjectWithBody.put("client_id", clientId);
                jsonObjectWithBody.put("pool_token", result);
            } catch (JSONException e) {
                Log.d("Can't make request:", e.toString());
            }

            final String requestBody = jsonObjectWithBody.toString();

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                    getString(R.string.url) + "join_pool",
                    null, response -> {
                try {
                    long key = response.getLong("pool_id");
                    Intent intent_redirectToGame =
                            new Intent(context, GroupMainActivity.class).
                                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT).
                                    putExtra("client_name", login).
                                    putExtra("client_id", clientId)
                                    .putExtra("group_id", key);
                    context.startActivity(intent_redirectToGame);
                } catch (JSONException e) {
                    Log.d("Error", e.toString());
                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.name),
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }, error -> {
                String ans;
                if (error.networkResponse.statusCode == 400) {
                    ans = "?????? ???????????? ?? ?????????? ??????????????";
                } else {
                    ans = context.getString(R.string.problem);
                }
                try {
                    Toast toast = Toast.makeText(getContext(),
                            ans,
                            Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception ignored) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        Log.d("Error", "UTF-8 encoding error");
                        return null;
                    }
                }
            };
            queue.add(jsonObjectRequest);

        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });
        return builder.create();
    }
}