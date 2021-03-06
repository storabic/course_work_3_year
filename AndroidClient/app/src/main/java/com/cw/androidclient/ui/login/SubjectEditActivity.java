package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

public class SubjectEditActivity extends AppCompatActivity {

    private long groupId;

    private long clientId;

    private String login;

    private long subjectId;

    private Context context;

    private TextInputEditText nameText;

    private TextInputEditText commentText;

    private Switch switchh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_edit);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        this.groupId = getIntent().getLongExtra("group_id", 0);
        this.subjectId = getIntent().getLongExtra("subject_id", 0);
        context = this;
        TextView textView = findViewById(R.id.editSubjectNameText);
        textView.setText(this.login);
        this.nameText = findViewById(R.id.editSubjectNameInputText);
        this.commentText = findViewById(R.id.editSubjectCommentNameInputText);
        this.switchh = findViewById(R.id.switch2);
        nameText.setText(getIntent().getStringExtra("subject_name"));
        ((TextInputEditText) findViewById(R.id.editSubjectCommentNameInputText)).setText(getIntent().getStringExtra("subject_comment"));
        findViewById(R.id.createNewGroupButton).setOnClickListener(view -> {
            if (nameText.getEditableText().length() >= 4) {
                sendChangedGroup();
                finish();
            } else {
                Toast.makeText(context, "?????????????? ???????????????? ???????????????? ??????????\n?????????? ???? ?????????? 4 ????????????????", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendChangedGroup() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("subject_id", subjectId);
            jsonObjectWithBody.put("name", nameText.getEditableText().toString());
            jsonObjectWithBody.put("comment", commentText.getEditableText().toString());
            jsonObjectWithBody.put("is_active", switchh.isChecked());
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "change_subject",
                null, response -> {
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