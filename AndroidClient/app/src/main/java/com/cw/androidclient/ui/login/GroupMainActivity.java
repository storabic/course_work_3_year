package com.cw.androidclient.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cw.androidclient.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMainActivity extends AppCompatActivity {

    private long clientId;

    private long groupId;

    private String login;

    private Context context;

    private String token;

    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.groupId = getIntent().getLongExtra("group_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        this.context = this;
        initializeViews();
    }

    private void initializeViews() {
        setContentView(R.layout.activity_group_main);

        ((TextView) findViewById(R.id.groupUserNameText)).setText(login);

        findViewById(R.id.groupMainEditGroupButton).setOnClickListener(view -> {
            Intent intentToGroup = new Intent(context, EditGroupActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("client_id", clientId)
                    .putExtra("client_name", login)
                    .putExtra("group_id", groupId)
                    .putExtra("group_name", ((TextView) findViewById(R.id.textView)).getText())
                    .putExtra("group_comment", ((TextView) findViewById(R.id.textView2)).getText());
            startActivity(intentToGroup);
        });

        findViewById(R.id.button4).setOnClickListener(view -> {
            Intent intentToCreateSubject = new Intent(context, SubjectCreateActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("client_id", clientId)
                    .putExtra("client_name", login)
                    .putExtra("group_id", groupId);
            startActivity(intentToCreateSubject);
        });

        sendGetGroupRequest();
    }

    private void sendGetGroupRequest() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("group_id", groupId);
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "get_group",
                null, response -> {
            try {
                TextView groupTokenTextView = findViewById(R.id.textView3);
                groupTokenTextView.setText(String.format("Группа %s от %s", response.getString("token"), response.getJSONObject("creator").getString("login")));
                if (!response.getBoolean("is_active")) {
                    groupTokenTextView.setTextColor(0xFF808080);
                }
                ((TextView) findViewById(R.id.textView)).setText(response.getString("name"));
                ((TextView) findViewById(R.id.textView2)).setText(response.getString("comment"));
                this.groupId = response.getLong("id");
                fillSubjects(response.getJSONArray("subjects"));
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

    private void fillSubjects(JSONArray jsonArray) throws JSONException {
        List<Subject> subjects = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            subjects.add(new Subject(jsonObject.getLong("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("comment"),
                    jsonObject.getString("author_login"),
                    jsonObject.getDouble("sum"),
                    jsonObject.getBoolean("is_active")));
        }
        SubjectRecyclerAdapter recyclerAdapter = new SubjectRecyclerAdapter(this, subjects);
        RecyclerView recyclerView = findViewById(R.id.subjectRecyclerView);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int pos) {
                long subjectId = subjects.get(pos).getSubjectId();
                Intent intentToGroup = new Intent(context, SubjectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("group_id", groupId)
                        .putExtra("client_id", clientId)
                        .putExtra("client_name", login)
                        .putExtra("subject_id", subjectId);
                startActivity(intentToGroup);
            }

            @Override
            public void onLongItemClick(View view, int pos) {
                long subjectId = subjects.get(pos).getSubjectId();
                Intent intentToGroup = new Intent(context, SubjectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("group_id", groupId)
                        .putExtra("client_id", clientId)
                        .putExtra("client_name", login)
                        .putExtra("subject_id", subjectId);
                startActivity(intentToGroup);
            }
        }));
    }

    @Override
    public void onResume() {
        initializeViews();
        super.onResume();
    }
}