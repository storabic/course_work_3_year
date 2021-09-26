package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.cw.androidclient.ui.login.UserPayRecyclerAdapter.round;

public class PaymentActivity extends AppCompatActivity {

    private long paymentId;

    private long payerClientId;

    private long clientId;

    private String payerlogin;

    private Context context;

    private boolean isNotPaid;

    private long creatorClientId;

    private boolean isSubjectActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        context = this;
        ((TextView) findViewById(R.id.paymentUserNameText)).setText(getIntent().getStringExtra("client_name"));
        this.paymentId = getIntent().getLongExtra("payment_id", 0);
        this.creatorClientId = getIntent().getLongExtra("creator_client_id", 0);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.isSubjectActive = getIntent().getBooleanExtra("is_active", true);
        getPayment();
        findViewById(R.id.button).setOnClickListener(view -> {
            sendPayment();
        });
    }

    public double getSum() {
        double x = Double.parseDouble(((TextInputEditText)findViewById(R.id.paymentSumInputText)).getEditableText().toString());
        if (x <= 0) {
            throw new NumberFormatException();
        }
        return round(x, 2);
    }

    private void sendPayment() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("payment_id", paymentId);
            jsonObjectWithBody.put("comment", ((TextInputEditText)findViewById(R.id.paymentCommentInputText)).getEditableText().toString());
            jsonObjectWithBody.put("is_active", !((Switch)findViewById(R.id.switch3)).isChecked());
            jsonObjectWithBody.put("sum", getSum());
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "change_payment",
                null, response -> {
                getPayment();
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

    private void getPayment() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("payment_id", paymentId);
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "get_payment",
                null, response -> {
            try {
                String comment = response.getString("comment");
                ((TextView) findViewById(R.id.paymentCommentInputText)).setText(comment == null ? "" : comment);
                ((TextView) findViewById(R.id.paymentSumInputText)).setText(response.getString("sum"));
                this.payerlogin = response.getString("payer_login");
                this.payerClientId = response.getLong("payer_client_id");
                ((TextView) findViewById(R.id.textView11)).setText(String.format("Плательщик - %s", payerlogin));
                this.isNotPaid = response.getBoolean("is_active");
                ((Switch) findViewById(R.id.switch3)).setChecked(!isNotPaid);
                if (clientId == creatorClientId) {
                    showElements(1);
                } else if (payerClientId == clientId) {
                    showElements(0);
                }
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

    private void showElements(int x) {
        if (!isSubjectActive) {
            findViewById(R.id.button).setEnabled(false);
            findViewById(R.id.button).setVisibility(View.INVISIBLE);
            findViewById(R.id.paymentCommentInputText).setFocusableInTouchMode(false);
            findViewById(R.id.paymentCommentInputText).setEnabled(false);
            findViewById(R.id.paymentSumInputText).setFocusableInTouchMode(false);
            findViewById(R.id.paymentSumInputText).setEnabled(false);
            findViewById(R.id.switch3).setEnabled(false);
            return;
        }
        if (!isNotPaid) {
            findViewById(R.id.button).setEnabled(false);
            findViewById(R.id.button).setVisibility(View.INVISIBLE);
            findViewById(R.id.paymentCommentInputText).setFocusableInTouchMode(false);
            findViewById(R.id.paymentCommentInputText).setEnabled(false);
            findViewById(R.id.paymentSumInputText).setFocusableInTouchMode(false);
            findViewById(R.id.paymentSumInputText).setEnabled(false);
            findViewById(R.id.switch3).setEnabled(false);
            return;
        }
        if (x == 1) {
            findViewById(R.id.paymentSumInputText).setFocusableInTouchMode(true);
        }
        findViewById(R.id.switch3).setEnabled(true);
        findViewById(R.id.paymentCommentInputText).setFocusableInTouchMode(true);
        findViewById(R.id.switch3).setFocusableInTouchMode(true);
        findViewById(R.id.button).setEnabled(true);
        findViewById(R.id.button).setVisibility(View.VISIBLE);
    }
}