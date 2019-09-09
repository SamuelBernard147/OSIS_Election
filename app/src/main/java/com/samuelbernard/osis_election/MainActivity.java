package com.samuelbernard.osis_election;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

/* TODO :
 * User login(post username, pw) ambil data mesin(id, username, pw, status, id_kandidat) Kalo berhasil Is pref loggedIn jadi true
 * Simpan kedalam shared preference
 * Masuk dalam Main Activity get data kandidat utama dari pref di tengah sisanya di bawah
 * Klik visi misi muncul sesuai kandidat utama
 * Klik scan barcode
 * kalo belum milih post(id_user, status jadi voted), post riwayat(id_user, id_mesin, id_kandidat)
 * Kalo sudah milih muncul sweet alert
 */