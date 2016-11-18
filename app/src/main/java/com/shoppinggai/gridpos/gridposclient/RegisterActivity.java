package com.shoppinggai.gridpos.gridposclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText textEmail = (EditText)findViewById(R.id.email);
        EditText textPass = (EditText)findViewById(R.id.password);

        Bundle extras = getIntent().getExtras();

        if (extras != null && textEmail != null && textPass != null) {
            String mEmail = extras.getString("mEmail");
            textEmail.setText(mEmail, TextView.BufferType.EDITABLE);
            textPass.requestFocus();
        }
    }
}
