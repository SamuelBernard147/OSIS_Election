package com.samuelbernard.osis_election;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.input_username)
    TextInputEditText inputUsername;
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;
    @BindView(R.id.btn_login)
    MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void onButtonSubmit() {
//        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
//                .setTitleText("Login berhasil")
//                .show();

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }
}