package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubjectCreateActivity extends AppCompatActivity {

    private long clientId;

    private long groupId;

    private String login;

    private TextInputEditText createSubjectTotalSumInputText;

    private TextInputEditText createSubjectNameInputText;

    private TextInputEditText createSubjectCommentNameInputText;

    private Context context;

    private long subjectId;

    private Switch equalSumSwitch;

    private RecyclerView recyclerView;

    ArrayList<Client> clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_create);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        this.groupId = getIntent().getLongExtra("group_id", 0);
        ((TextView) findViewById(R.id.createSubjectUserNameText)).setText(this.login);
        this.createSubjectTotalSumInputText = findViewById(R.id.createSubjectTotalSumInputText);
        this.context = this;

        this.createSubjectNameInputText = findViewById(R.id.createSubjectNameInputText);
        this.createSubjectCommentNameInputText = findViewById(R.id.createSubjectCommentNameInputText);

        getClients();

        this.equalSumSwitch = findViewById(R.id.switch1);
        equalSumSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                createSubjectTotalSumInputText.setEnabled(false);
            }
        });

        findViewById(R.id.createNewSubjectButton).setOnClickListener(view -> {
            if (createSubjectNameInputText.getEditableText().length() < 4) {
                Toast toast = Toast.makeText(context,
                        "Слишком короткое название сбора",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            try {
                if (equalSumSwitch.isChecked()) {
                    createSubjectEqualSum();
                } else {
                    List<Long> equal = getEqualPayments();
                    Intent intentToSubject = new Intent(context, SubjectCreateDifferentSumActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            .putExtra("client_name", login)
                            .putExtra("client_id", clientId)
                            .putExtra("group_id", groupId)
                            .putExtra("subject_name", createSubjectNameInputText.getEditableText().toString())
                            .putExtra("subject_comment", createSubjectCommentNameInputText.getEditableText().toString())
                            .putParcelableArrayListExtra("clients", (ArrayList<Client>) clients.stream().filter(x -> equal.contains(x.getClientId())).collect(Collectors.toList()));
                    startActivity(intentToSubject);
                    finish();
                }
            } catch(IllegalArgumentException e) {
                Toast toast = Toast.makeText(context,
                        "Не выбрано ни одного участника сбора",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void createSubjectEqualSum() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("client_id", clientId);
            jsonObjectWithBody.put("name", createSubjectNameInputText.getEditableText().toString());
            jsonObjectWithBody.put("comment", createSubjectCommentNameInputText.getEditableText().toString());
            jsonObjectWithBody.put("pool_id", groupId);
            jsonObjectWithBody.put("equal_sum", true);
            jsonObjectWithBody.put("sum", getSum());
            jsonObjectWithBody.put("payments", new JSONArray(getEqualPayments()));
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
                        .putExtra("client_name", login)
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

    private List<Long> getEqualPayments() {
        List<Long> clients = new ArrayList<>();
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            ClientRecyclerAdapter.ClientViewHolder holder = (ClientRecyclerAdapter.ClientViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            assert holder != null;
            if (holder.isChecked()) {
                clients.add(holder.getClientId());
            }
        }
        if (clients.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return clients;
    }

    private double getSum() {
        String x = createSubjectTotalSumInputText.getEditableText().toString();
        try {
            return Double.parseDouble(x);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void getClients() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("group_id", groupId);
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "get_clients",
                null, response -> {
            try {
                fillClientRecycler(response.getJSONArray("clients"));
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

    private void fillClientRecycler(JSONArray jsonArray) throws JSONException {
        clients = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            clients.add(new Client(jsonObject.getLong("client_id"),
                    jsonObject.getString("login")));
        }
        ClientRecyclerAdapter recyclerAdapter = new ClientRecyclerAdapter(this, clients);
        recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}