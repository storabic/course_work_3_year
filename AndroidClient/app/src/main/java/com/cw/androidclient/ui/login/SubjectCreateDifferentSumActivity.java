package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cw.androidclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectCreateDifferentSumActivity extends AppCompatActivity {

    private long clientId;

    private long groupId;

    private String subjectName;

    private String commentName;

    UserPayRecyclerAdapter recyclerAdapter;

    RecyclerView recyclerView;

    private Context context;

    private long subjectId;

    private String clientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_create_different_sum);
        this.clientName = getIntent().getStringExtra("client_name");
        ((TextView)findViewById(R.id.createSubjectDifferentUserNameText)).setText(this.clientName);
        ArrayList<Client> clients = getIntent().getParcelableArrayListExtra("clients");
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.groupId = getIntent().getLongExtra("group_id", 0);
        this.subjectName = getIntent().getStringExtra("subject_name");
        this.commentName = getIntent().getStringExtra("subject_comment");
        recyclerAdapter = new UserPayRecyclerAdapter(this, clients);
        recyclerView = findViewById(R.id.userPayRecycler);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        context = this;
        findViewById(R.id.createNewSubjectDifferentButton).setOnClickListener(view -> {
            try {
                sendCreateSubjectRequest();
            } catch (NumberFormatException nfe) {
                Toast toast = Toast.makeText(context,
                        "Значения сумм ввведены неправильно, введите пожалуйста числа",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void sendCreateSubjectRequest() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("client_id", clientId);
            jsonObjectWithBody.put("name", subjectName);
            jsonObjectWithBody.put("comment", commentName);
            jsonObjectWithBody.put("pool_id", groupId);
            jsonObjectWithBody.put("equal_sum", false);
            jsonObjectWithBody.put("sum", 0);
            jsonObjectWithBody.put("payments", getPayments());
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "create_subject",
                null, response -> {
            try {
                subjectId = response.getLong("subject_id");
                Intent intentToSubject = new Intent(context, SubjectActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("subject_id", subjectId)
                        .putExtra("client_name", clientName)
                        .putExtra("client_id", clientId)
                        .putExtra("group_id", groupId);
                startActivity(intentToSubject);
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

    private JSONArray getPayments() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            UserPayRecyclerAdapter.ClientViewHolder holder = (UserPayRecyclerAdapter.ClientViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            assert holder != null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("client_id", holder.getClientId());
                jsonObject.put("sum", holder.getSum());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
}