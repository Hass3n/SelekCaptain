package minya.salek.salekcaptain;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import minya.salek.salekcaptain.Model.CaptainModel;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameRegister, idRegister, phoneRegister, emailRegister, passwordRegister, confirmPasswordRegister;
    private String name, id, phone, email, password, confirmPassword, carType,imageUrl="https://firebasestorage.googleapis.com/v0/b/salek-327e3.appspot.com/o/Captains%20Image%2Fmask_group.png?alt=media&token=b467c4d6-5b1e-4580-a014-30f1ae0ed866";

    private Button signUp;

    private FirebaseAuth mAuth;
    private SweetAlertDialog loadingDialog;
    private FirebaseUser currentUser;
    private FirebaseDatabase database ;
    private DatabaseReference userRef;

    private Spinner cars_spinner;
    private String[]  cars_list = {"موتوسيكل", "نقل"};
    private ArrayAdapter carsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        nameRegister = findViewById(R.id.sign_up_name);
        //idRegister = findViewById(R.id.sign_up_id);
        phoneRegister = findViewById(R.id.sign_up_phone);
        emailRegister = findViewById(R.id.sign_up_email);
        passwordRegister = findViewById(R.id.sign_up_password);
        confirmPasswordRegister = findViewById(R.id.sign_up_confirm_password);
        signUp = findViewById(R.id.sign_up_btn);

        cars_spinner = findViewById(R.id.cars_spinner);
        carsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_selected, cars_list);
        carsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        cars_spinner.setAdapter(carsAdapter);

        mAuth = FirebaseAuth.getInstance();


        database = FirebaseDatabase.getInstance();
        userRef = database.getReference();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateUserData();
            }
        });


    }
    private void ValidateUserData() {
        name = Objects.requireNonNull(nameRegister.getText()).toString();
        //id = idRegister.getText().toString();
        id = "21234567890123";
        phone = phoneRegister.getText().toString();
        email = emailRegister.getText().toString();
        password = passwordRegister.getText().toString();
        confirmPassword = confirmPasswordRegister.getText().toString();
        if (cars_spinner.getSelectedItem().toString().equals("موتوسيكل")) {
            carType = "موتوسيكل";
        } else {
            carType = "نقل";
        }

        if (TextUtils.isEmpty(name)) {
            nameRegister.setError("من فضلك ادخل اسمك");
        } else if (TextUtils.isEmpty(phone)) {
            phoneRegister.setError("ادخل رقم هاتفك");
        } else if (!Prevalent.isValidMobile(phone)) {
            phoneRegister.setError("يجب أن يتكون رقم الهاتف من 11 ارقام ويبدأ 01");
        } else if (TextUtils.isEmpty(id)) {
            idRegister.setError("ادخل رقم رقمك القومي");
        } else if (!Prevalent.isValidID(id)) {
            idRegister.setError("يجب أن يتكون رقمك القومي من 14 ارقام ويبدأ 2");
        } else if (TextUtils.isEmpty(email)) {
            emailRegister.setError("ادخل البريد الالكتروني");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegister.setError("يجب ادخال بريد الكتروني صحيح مثلا : \n example@gmail.com");
        } else if (TextUtils.isEmpty(password)) {
            passwordRegister.setError("ادخل كلمة المرور");
        } else if (!Prevalent.isValidPassword(password)) {
            passwordRegister.setError("يجب ان تحتوي كلمة المرور على حرف كبير وحرف صغير وواحد من هذه الرموز @#$%^&+=! ولا يقل طولها عن 6 أحرف");
        }else if (!password.equals(confirmPassword)) {
            confirmPasswordRegister.setError("الكلمتان غير متطابقتان");
        }
        else {
            if(!Prevalent.isConnection(RegistrationActivity.this)){
                Prevalent.showCustomDialog(RegistrationActivity.this);
            }else {

                loadingDialog = new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                loadingDialog.setTitleText("سالك جديد");
                loadingDialog.setContentText("يتم الان تسجيل بياناتك");
                loadingDialog.setCancelable(false);
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            saveUserInfoToDatabase();

                            //Toast.makeText(RegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(RegistrationActivity.this, "User registered successfully"+currentUser.getUid(), Toast.LENGTH_SHORT).show();

                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void createUser() {
        if (TextUtils.isEmpty(name)) {
            nameRegister.setError("name cannot be empty");
            nameRegister.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            phoneRegister.setError("phone cannot be empty");
            phoneRegister.requestFocus();
        } else if (TextUtils.isEmpty(id)) {
            idRegister.setError("id cannot be empty");
            idRegister.requestFocus();
        }
        else if (TextUtils.isEmpty(phone)) {
            phoneRegister.setError("phone cannot be empty");
            phoneRegister.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            emailRegister.setError("email cannot be empty");
            emailRegister.requestFocus();
        }
        else if (TextUtils.isEmpty(password)) {
            passwordRegister.setError("Password cannot be empty");
            passwordRegister.requestFocus();
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordRegister.setError("confirm Password cannot be empty");
            confirmPasswordRegister.requestFocus();
        }
        else if (password.equals(confirmPassword)) {
            confirmPasswordRegister.setError("Password and confirm Password not equals");
            confirmPasswordRegister.requestFocus();
        } else {
            SweetAlertDialog pDialog = new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Loading ...");
            pDialog.setCancelable(false);
            pDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        pDialog.dismiss();
                        saveUserInfoToDatabase();
                        //Toast.makeText(RegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));

                    } else {
                        pDialog.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void saveUserInfoToDatabase() {


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Captains").child(currentUser.getUid()).exists())) {

                    CaptainModel model = new CaptainModel(name, id, phone, email,password,imageUrl,carType,"3",currentUser.getUid());

                    userRef.child("Captains").child(currentUser.getUid()).setValue(model)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Prevalent.currentOnlineUser = model;
                                        startActivity(new Intent(RegistrationActivity.this, PrivacyActivity.class));
                                        loadingDialog.dismiss();
                                    } else {

                                        String message = Objects.requireNonNull(task.getException()).toString();
                                        Toast.makeText(RegistrationActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    loadingDialog.dismiss();

                    new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("عذرا يوجد خطأ")
                            .setContentText(" مسجل لدينا بالفعل" + email + "هذا البريد : ")
                            .setConfirmText("ادخل الان")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismiss();
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            }).setCancelText("سجل ببريد اخر")
                            .setCancelClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismiss();
                                startActivity(new Intent(RegistrationActivity.this, RegistrationActivity.class));
                            })
                            .show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegistrationActivity.this, "cannnnnnnnnnnnn" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
