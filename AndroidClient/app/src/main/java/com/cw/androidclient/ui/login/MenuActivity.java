package com.cw.androidclient.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cw.androidclient.R;

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
            KeyPicker keyPickerDialog = new KeyPicker(clientId, context);
            keyPickerDialog.show(getSupportFragmentManager(), "key_picker");
        });
    }
}