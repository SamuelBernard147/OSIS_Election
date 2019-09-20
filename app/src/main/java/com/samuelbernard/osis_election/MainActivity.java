package com.samuelbernard.osis_election;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.samuelbernard.osis_election.preference.LoginPref;
import com.samuelbernard.osis_election.rest.ApiClient;
import com.samuelbernard.osis_election.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.imgbtn_scan)
    ImageButton imgbtnScan;
    @BindView(R.id.qr_scan)
    DecoratedBarcodeView qrCodeScanner;
    @BindView(R.id.btn_logout)
    ImageButton btnLogout;
    @BindView(R.id.tv_main_candidate_picture)
    ImageView tvMainCandidatePicture;
    @BindView(R.id.tv_main_candidate_name)
    MaterialTextView tvMainCandidateName;
    @BindView(R.id.tv_main_candidate_vote)
    MaterialTextView tvMainCandidateVote;
    @BindView(R.id.btn_visi)
    MaterialButton btnVisi;
    @BindView(R.id.btn_misi)
    MaterialButton btnMisi;

    LoginPref loginPref;
    SweetAlertDialog dialog;
    boolean isVoted = true;
    SweetAlertDialog dialogFail;
    SweetAlertDialog dialogSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loginPref = new LoginPref(this);
        tvMainCandidateName.setText(loginPref.getNamaKetua());
        tvMainCandidateVote.setText(loginPref.getVote() + "%");
        Picasso.get()
                .load("http://election.starbhaktefa.com/img/" + loginPref.getFoto())
                .into(tvMainCandidatePicture);
        requestPermission();
        initScanner();
    }

    void initScanner() {
        qrCodeScanner.setVisibility(View.VISIBLE);
        imgbtnScan.setVisibility(GONE);
        qrCodeScanner.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                String[] data = result.getText().split("-");
//                Toast.makeText(MainActivity.this, data[0], Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, data[1], Toast.LENGTH_SHORT).show();
                qrCodeScanner.setVisibility(GONE);
                imgbtnScan.setVisibility(View.VISIBLE);
                checkVoter(data[0], data[1]);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeScanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.pause();
    }

    void checkVoter(String no_pemilih, String namaPemilih) {
        isVoted = true;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> cekVoter = apiService.cekPemilih(no_pemilih);
        cekVoter.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    int status = object.getInt("Status");
                    if (status == 200) {
                        if (object.getJSONObject("data").getInt("status") == 1) {
                            isVoted = false;
                            voteCandidate(no_pemilih);
                        } else {
                            isVoted = true;
                            showFailDialog("Failed", "You already voted");
                        }
                    } else {
                        showFailDialog("Failed", "Please scan again!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showFailDialog("Failed", "Something went wrong!");
            }
        });
    }

    void voteCandidate(String no_pemilih) {
        if (!isVoted) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ResponseBody> voterVote = apiService.pemilihMemilih(no_pemilih);
            voterVote.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        int status = object.getInt("status");
                        if (status == 200) {

                        } else {
                            showFailDialog("Failed", "Please scan again!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showFailDialog("Failed", "Something went wrong!");
                }
            });

            Call<ResponseBody> addHistory = apiService.addVote(no_pemilih, Integer.toString(loginPref.getIdMesin()), Integer.toString(loginPref.getIdKandidat()));
            addHistory.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        int status = object.getInt("status");
                        if (status == 200) {
                            String voteCount = String.valueOf(object.getDouble("data"));
                            loginPref.setVote(voteCount.substring(0, 5));
                            tvMainCandidateVote.setText(loginPref.getVote());
                        } else {
                            showFailDialog("Failed", "Please scan again!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showSuccessDialog("Success", "Thank you for choosing");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showFailDialog("Failed", "Something went wrong!");
                }
            });
            isVoted = true;
        }
    }

    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            qrCodeScanner.resume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        }
    }

    @OnClick({R.id.btn_logout, R.id.btn_visi, R.id.btn_misi, R.id.imgbtn_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                dialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                dialog.setTitleText(getResources().getString(R.string.loading));
                dialog.setContentText(getResources().getString(R.string.loading_message));
                dialog.setCancelable(false);
                dialog.show();

//                Memasukan nilai default kedalam preference
                loginPref.setIdMesin(100);
                loginPref.setIdKandidat(100);

//                Jeda sebelum logout
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissWithAnimation();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }, 1500);
                break;
            case R.id.btn_visi:
                showMessageDialog(getResources().getString(R.string.visi), loginPref.getVisi());
                break;
            case R.id.btn_misi:
                showMessageDialog(getResources().getString(R.string.misi), loginPref.getMisi());
                break;
            case R.id.imgbtn_scan:
                requestPermission();
                initScanner();
                break;
        }
    }

    //    Menampilkan pesan dialog
    void showMessageDialog(String title, String content) {
        dialog = new SweetAlertDialog(this);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.show();
    }

    void showFailDialog(String title, String content) {
        dialogFail = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        dialogFail.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        dialogFail.setTitleText(title);
        dialogFail.setContentText(content);
        dialogFail.show();
    }

    void showSuccessDialog(String title, String content) {
        dialogSuccess = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        dialogSuccess.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorSuccess));
        dialogSuccess.setTitleText(title);
        dialogSuccess.setContentText(content);
        dialogSuccess.show();
    }
}