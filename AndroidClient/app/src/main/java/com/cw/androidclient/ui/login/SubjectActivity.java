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

public class SubjectActivity extends AppCompatActivity {

    private long clientId;

    private long groupId;

    private String login;

    private TextInputEditText createSubjectTotalSumInputText;

    private TextInputEditText createSubjectNameInputText;

    private TextInputEditText createSubjectCommentNameInputText;

    private Context context;

    private long subjectId;

    private RecyclerView recyclerView;

    private long creatorClientId;

    private String creatorLogin;

    private boolean isActive;

    List<Client> payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        context = this;
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        this.groupId = getIntent().getLongExtra("group_id", 0);
        this.subjectId = getIntent().getLongExtra("subject_id", 0);
        ((TextView) findViewById(R.id.sSubjectUserNameText)).setText(this.login);
        findViewById(R.id.groupMainEditGroupButton22).setOnClickListener(view -> {
            Intent intentToPayment = new Intent(context, SubjectEditActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("subject_id", subjectId)
                    .putExtra("client_id", isActive ? clientId : 9999L)
                    .putExtra("client_name", login)
                    .putExtra("group_id", groupId)
                    .putExtra("subject_name", ((TextView) findViewById(R.id.textView7)).getText())
                    .putExtra("subject_comment", ((TextView) findViewById(R.id.textView8)).getText());
            startActivity(intentToPayment);
        });
        getSubjectRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSubjectRequest();
    }

    private void getSubjectRequest() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("subject_id", subjectId);
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "get_subject",
                null, response -> {
            try {
                ((TextView) findViewById(R.id.textView7)).setText(response.getString("name"));
                ((TextView) findViewById(R.id.textView8)).setText(response.getString("comment"));
                this.isActive = response.getBoolean("is_active");
                this.creatorLogin = response.getString("creator_login");
                this.creatorClientId = response.getLong("creator_client_id");
                if (isActive) {
                    ((TextView) findViewById(R.id.textView5)).setText(String.format("Сбор от %s", creatorLogin));
                } else {
                    ((TextView) findViewById(R.id.textView5)).setText(String.format("Неактивный сбор от %s", creatorLogin));
                    findViewById(R.id.groupMainEditGroupButton22).setEnabled(false);
                }
                fillPayments(response.getJSONArray("payments"));
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

    private void fillPayments(JSONArray jsonArray) throws JSONException {
        payments = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Client c = new Client(jsonObject.getLong("client_id"), jsonObject.getString("login"), jsonObject.getDouble("sum"));
            c.setClosedDate(jsonObject.getString("closed_date"));
            c.setComment(jsonObject.getString("comment"));
            c.setPaymentId(jsonObject.getLong("payment_id"));
            payments.add(c);
        }
        PaymentRecyclerAdapter recyclerAdapter = new PaymentRecyclerAdapter(this, payments);
        recyclerView = findViewById(R.id.paymentsRecyclerView);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int pos) {
                long paymentId = payments.get(pos).getPaymentId();
                Intent intentToPayment = new Intent(context, PaymentActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("subject_id", subjectId)
                        .putExtra("client_id", isActive ? clientId : 9999L)
                        .putExtra("payer_client_id", payments.get(pos).getClientId())
                        .putExtra("client_name", login)
                        .putExtra("group_id", groupId)
                        .putExtra("payment_id", paymentId)
                        .putExtra("creator_client_id", creatorClientId)
                        .putExtra("is_active", isActive);
                startActivity(intentToPayment);
            }

            @Override
            public void onLongItemClick(View view, int pos) {
                long paymentId = payments.get(pos).getPaymentId();
                Intent intentToPayment = new Intent(context, PaymentActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        .putExtra("subject_id", subjectId)
                        .putExtra("client_id", isActive ? clientId : 9999L)
                        .putExtra("payer_client_id", payments.get(pos).getClientId())
                        .putExtra("client_name", login)
                        .putExtra("group_id", groupId)
                        .putExtra("payment_id", paymentId)
                        .putExtra("creator_client_id", creatorClientId)
                        .putExtra("is_active", isActive);
                startActivity(intentToPayment);
            }
        }));
    }
}