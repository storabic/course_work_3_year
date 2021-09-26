package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cw.androidclient.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {

    private long clientId;

    private String login;

    private long groupId;

    private Context context;

    private TextInputEditText nameText;

    private TextInputEditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        context = this;
        TextView textView = findViewById(R.id.createGroupNameText);
        this.nameText = findViewById(R.id.groupNameInputText);
        this.commentText = findViewById(R.id.commentNameInputText);
        textView.setText(this.login);
        findViewById(R.id.createNewGroupButton).setOnClickListener(view -> {
            if (nameText.getEditableText().length() >= 4) {
                sendCreateGroupRequest();
            } else {
                Toast.makeText(context, "Слишком короткое название группы\nНужно не менее 4 символов", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendCreateGroupRequest() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("client_id", clientId);
            jsonObjectWithBody.put("name", nameText.getEditableText().toString());
            jsonObjectWithBody.put("comment", commentText.getEditableText().toString());
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "create_pool",
                null, response -> {
            try {
                groupId = response.getLong("group_id");
                Intent intentToGroup = new Intent(context, GroupMainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("client_id", clientId)
                        .putExtra("client_name", login)
                        .putExtra("group_id", groupId);
                startActivity(intentToGroup);
                finish();
            } catch (JSONException ignored) {
            }
        }, error -> {
            try {
                Toast toast = Toast.makeText(context,
                        getString(R.string.problem),
                        Toast.LENGTH_SHORT);

                toast.show();
            } catch (Exception ignored) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}