package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AlertDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cw.androidclient.R;
import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private long clientId;

    private String login;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView textView = findViewById(R.id.menuUserNameText);
        this.clientId = getIntent().getLongExtra("client_id", 0);
        this.login = getIntent().getStringExtra("client_name");
        context = this;
        textView.setText(this.login);
        findViewById(R.id.joinGroupButton).setOnClickListener(v -> {
            KeyPicker keyPickerDialog = new KeyPicker(clientId, context, login);
            keyPickerDialog.show(getSupportFragmentManager(), "key_picker");
        });

        FlurryAgent.logEvent("NEW_ACTIVITY_WOW", new HashMap<>());

        FlurryAgent.logEvent("OLD_ACTIVITY_WOW", new HashMap<>());

        findViewById(R.id.createGroupButton).setOnClickListener(view -> {
            Intent intentToMenu = new Intent(context, CreateGroupActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("client_id", clientId)
                    .putExtra("client_name", login);
            startActivity(intentToMenu);
        });

        findViewById(R.id.faqButton).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Добро пожаловать в приложение совместной оплаты или сборов\n" +
                    "По нажатии на кнопку 'Создать группу' вы попадете в окно настройки новой группы\n" +
                    "По нажатии на кнопку 'Присоединиться к группе' введите сообщенный вам участником группы токен\n" +
                    "Внизу вы можете найти список всех групп, в которых вы принимаете участие\n" +
                    "\n" +
                    "На экране группы можно увидеть все существующие сборы в группе или создать свой сбор, а также поменять информацию о группе\n" +
                    "На экране сбора можно увидеть все платежи в сборе с указанием долей их плательщиков");
            builder.create().show();
        });

        getGroups();
    }

    private void getGroups() {
        JSONObject jsonObjectWithBody = new JSONObject();
        try {
            jsonObjectWithBody.put("client_id", clientId);
        } catch (JSONException e) {
            Log.d("Can't make request:", e.toString());
        }
        final String requestBody = jsonObjectWithBody.toString();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.url) + "get_allowed_pools",
                null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("pools");
                List<Group> groups = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    groups.add(new Group(jsonObject.getString("name"), jsonObject.getString("token"), jsonObject.getLong("group_id")));
                }
                GroupRecyclerAdapter recyclerAdapter = new GroupRecyclerAdapter(this, groups);
                RecyclerView recyclerView = findViewById(R.id.groupRecyclerView);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                        recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int pos) {
                        long groupId = groups.get(pos).getGroupId();
                        Intent intentToGroup = new Intent(context, GroupMainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("group_id", groupId)
                                .putExtra("client_id", clientId)
                                .putExtra("client_name", login);
                        startActivity(intentToGroup);
                    }

                    @Override
                    public void onLongItemClick(View view, int pos) {
                        long groupId = groups.get(pos).getGroupId();
                        Intent intentToGroup = new Intent(context, GroupMainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                .putExtra("group_id", groupId)
                                .putExtra("client_id", clientId)
                                .putExtra("client_name", login);
                        startActivity(intentToGroup);
                    }
                }));
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

    @Override
    public void onResume() {
        super.onResume();
        getGroups();
    }
}