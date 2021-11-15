package minya.salek.salekcaptain;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPasswordActivity extends AppCompatActivity {

    private Button checkBtn;
    private EditText emailEd;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkBtn = findViewById(R.id.check_btn);
        emailEd = findViewById(R.id.forget_email_data);

        auth = FirebaseAuth.getInstance();

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        String email = emailEd.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEd.setError("ادخل البريد الإلكتروني");
            emailEd.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEd.setError("يجب ادخال بريد الكتروني صحيح مثلا : \n example@gmail.com");
            emailEd.requestFocus();
        } else{
            SweetAlertDialog pDialog = new SweetAlertDialog(ForgetPasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
            pDialog.setTitleText("جاري التحقق");
            pDialog.setCancelable(false);
            pDialog.show();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        pDialog.dismiss();
                        final SweetAlertDialog successBar = new SweetAlertDialog(ForgetPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        successBar.setContentText("تم ارسال رابط تغير كلمة المرور للبريد الخاص بك");
                        successBar.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
                        successBar.setCancelable(false);
                        successBar.setCanceledOnTouchOutside(false);
                        successBar.setConfirmText("حسنا");
                        successBar.setConfirmClickListener(sDialog -> {
                            sDialog.dismiss();
                        });
                        successBar.show();
                    }else {
                        pDialog.dismiss();
                        final SweetAlertDialog successBar = new SweetAlertDialog(ForgetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE);
                        successBar.setTitleText("حدث خطأ ما");
                        successBar.setContentText("حاول موجددا");
                        successBar.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
                        successBar.setCancelable(false);
                        successBar.setCanceledOnTouchOutside(false);
                        successBar.setConfirmText("حسنا");
                        successBar.setConfirmClickListener(sDialog -> {
                            sDialog.dismiss();
                        });
                        successBar.show();
                    }
                }
            });
        }
    }
}
