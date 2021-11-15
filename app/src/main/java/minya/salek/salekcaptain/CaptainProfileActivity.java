package minya.salek.salekcaptain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import minya.salek.salekcaptain.Model.CaptainModel;
import minya.salek.salekcaptain.Model.LocationModel;

public class CaptainProfileActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private ImageView captainImage, editImage, statusImage;
    private TextView captainName, captainPhone, captainId, captainEmail, captainEvaluation, statusTxt;
    private static final int GALLERY_REQUEST_CODE = 10;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private Uri selectedUri;

    private Button oldTrip;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    //private FirebaseUser captain = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference rootcaptain = FirebaseDatabase.getInstance().getReference("captainlocation");

    private FusedLocationProviderClient fusedLocationProviderClient;

    private StorageReference reference =
            FirebaseStorage.getInstance().getReference("Captains Image");
    private String userId;
    private LocationRequest locationRequest;
    private SweetAlertDialog sweetAlert;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captin_profile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*------------------------------------*/


        geoFire = new GeoFire(rootcaptain);

        /*----------------------------------*/

        oldTrip = findViewById(R.id.Captain_old_trip_btn);
        captainImage = findViewById(R.id.captain_image);
        editImage = findViewById(R.id.captain_edit_pen);
        statusImage = findViewById(R.id.captain_status_image);
        captainName = findViewById(R.id.captain_name_txt);
        captainPhone = findViewById(R.id.captain_phone_txt);
        //captainId = findViewById(R.id.captain_id_data);
        captainEmail = findViewById(R.id.captain_email_data);
        captainEvaluation = findViewById(R.id.captain_evaluation_data);
        statusTxt = findViewById(R.id.captain_status_txt);

        FirebaseUser captain = FirebaseAuth.getInstance().getCurrentUser();
        assert captain != null;
        userId = captain.getUid();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        captainName.setText(Prevalent.currentOnlineUser.getName());
        captainPhone.setText(Prevalent.currentOnlineUser.getPhone());
        //captainId.setText(Prevalent.currentOnlineUser.getId());
        captainEmail.setText(Prevalent.currentOnlineUser.getEmail());
        captainEvaluation.setText(Prevalent.currentOnlineUser.getEvaluation());
        Picasso.get().load(Prevalent.currentOnlineUser.getImageUrl()).placeholder(R.drawable.mask_group).into(captainImage);
        //} else { }

        oldTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptainProfileActivity.this, CaptainOldTripActivity.class));
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });


        statusTxt.setText("أنت غير متاح الآن");
        statusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getLocation();
                // take lat,len and store
               /* geoFire.setLocation(userId, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Log.e("key", key);
                        } else {
                            System.out.println("Location saved on server successfully!");
                            System.out.println(latitude + " " + longitude);
                            System.out.println("Location saved on server successfully!");

                        }
                    }
                });*/

                if (statusTxt.getText() == "أنت غير متاح الآن") {
                    getCurrentLocation(geoFire);
                    // take lat,len and store
                    /*HashMap userMap = new HashMap();
                    userMap.put("userId", "0987654321");
                    userMap.put("captainId", "1234567890");
                    userMap.put("endTrip", "yes");
                    userMap.put("tripDate", "22-2-2020");
                    userMap.put("rate", "false");
                    root.child("Accepted").child(userId).push().setValue(userMap);*/

                    statusImage.setImageDrawable(getResources().getDrawable(R.drawable.on));
                    statusTxt.setText("أنت سالك الآن");
                    statusTxt.setTextColor(getResources().getColorStateList(R.color.colorPrimary));
                    //sweetAlert.dismiss();
                } else {
                    //sweetAlert.show();
                    statusImage.setImageDrawable(getResources().getDrawable(R.drawable.off));
                    FirebaseDatabase.getInstance()
                            .getReference("captainlocation")
                            .child(userId)
                            .removeValue();
                    statusTxt.setText("أنت غير متاح الآن");
                    statusTxt.setTextColor(getResources().getColorStateList(R.color.grayDark));
                    //sweetAlert.dismiss();
                }
            }
        });

        sweetAlert = new SweetAlertDialog(CaptainProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
        sweetAlert.setTitleText("جاري التحميل");
        sweetAlert.setCancelable(false);

    }

    void showCustomDialog() {
        final Dialog dialog = new Dialog(CaptainProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        LayoutInflater inflater = this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.captain_notification_dialog, null);
        final TextView phone, location, destination, price;
        final Button acceptBtn, cancelBtn;
        final ImageView userIcon, callIcon;

        phone = mView.findViewById(R.id.notification_phone);
        location = mView.findViewById(R.id.current_location_txt);
        destination = mView.findViewById(R.id.destination_location_txt);
        price = mView.findViewById(R.id.notification_price_text);
        acceptBtn = mView.findViewById(R.id.notification_accept_btn);
        cancelBtn = mView.findViewById(R.id.notification_cancel_btn);
        callIcon = mView.findViewById(R.id.call_icon);
        userIcon = mView.findViewById(R.id.user_icon);

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAtRuntime();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(mView);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void callAtRuntime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            String phoneNumber = captainPhone.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }


    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                selectedUri = data.getData();
                if (ActivityCompat.checkSelfPermission(this
                        , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //readFileFromUri();
                    captainImage.setImageURI(selectedUri);
                    uploadToFirebase(selectedUri);
                    //Toast.makeText(this, "First", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(this
                            , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , READ_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            } else {
                Toast.makeText(this, "لم يتم إختار صورة شخصية جديدة", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                getCurrentLocation(geoFire);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //readFileFromUri();
                captainImage.setImageURI(selectedUri);
                uploadToFirebase(selectedUri);

                //Toast.makeText(this, "Second", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "لا يمكننا عرض الصورة المختارة", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callAtRuntime();
            } else {
                Toast.makeText(this, "Permission Denied.Try Again !", Toast.LENGTH_SHORT).
                        show();
            }
        }


        if (requestCode == 6) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation(geoFire);
                } else {
                    turnOnGPS();
                }
            }
        }

    }

    private void uploadToFirebase(Uri uri) {
        final SweetAlertDialog loadingBar = new SweetAlertDialog(CaptainProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        loadingBar.setTitleText("سالك");
        loadingBar.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
        loadingBar.setContentText("جاري تغيير الصورة الشخصية");
        loadingBar.setCancelable(false);
        loadingBar.setCanceledOnTouchOutside(false);

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        CaptainModel model = new CaptainModel(Prevalent.currentOnlineUser.getName(), Prevalent.currentOnlineUser.getId(), Prevalent.currentOnlineUser.getPhone(), Prevalent.currentOnlineUser.getEmail(), Prevalent.currentOnlineUser.getPassword(), uri.toString(), Prevalent.currentOnlineUser.getCarType(), Prevalent.currentOnlineUser.getEvaluation(), Prevalent.currentOnlineUser.getKey());

                        root.child("Captains").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                root.child("Captains").child(userId).setValue(model)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Prevalent.currentOnlineUser = model;
                                                } else {
                                                    String message = Objects.requireNonNull(task.getException()).toString();
                                                    Toast.makeText(CaptainProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CaptainProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        loadingBar.dismiss();

                        final SweetAlertDialog successBar = new SweetAlertDialog(CaptainProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        successBar.setTitleText("سالك");
                        successBar.setContentText("تم تغيير الصورة الشخصية بنجاح");
                        successBar.getProgressHelper().setBarColor(Color.parseColor("#FCCB09"));
                        successBar.setCancelable(false);
                        successBar.setCanceledOnTouchOutside(false);
                        successBar.setConfirmText("حسنا");
                        successBar.setConfirmClickListener(sDialog -> {
                            sDialog.dismiss();
                        });
                        successBar.show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                loadingBar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                final SweetAlertDialog successBar = new SweetAlertDialog(CaptainProfileActivity.this, SweetAlertDialog.ERROR_TYPE);
                successBar.setTitleText("سالك");
                successBar.setContentText("حدث خطأ أثناء رفع الصورة الشخصية من فضلك حاول مجددا");
                successBar.setCancelable(false);
                successBar.setCanceledOnTouchOutside(false);
                successBar.setConfirmText("حسنا");
                successBar.setConfirmClickListener(sDialog -> {
                    sDialog.dismiss();
                });
                successBar.show();
            }
        });
    }

    private String getFileExtension(Uri mUri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }


    private void getLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 132);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(CaptainProfileActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            String latitude, longitude, city, locality, address;

                            latitude = String.valueOf(addresses.get(0).getLatitude());
                            longitude = String.valueOf(addresses.get(0).getLongitude());
                            city = String.valueOf(addresses.get(0).getLongitude());
                            locality = String.valueOf(addresses.get(0).getLongitude());
                            address = String.valueOf(addresses.get(0).getLongitude());
                            //locationModel = new LocationModel(String.valueOf(addresses.get(0).getLatitude()),String.valueOf(addresses.get(0).getLongitude()),addresses.get(0).getPhone(),addresses.get(0).getLocality(),addresses.get(0).getAddressLine(0));
                            if (latitude.isEmpty() && longitude.isEmpty() && city.isEmpty() && locality.isEmpty() && address.isEmpty()) {
                                //locationModel = new LocationModel(latitude, longitude, city, locality, address);
                                //storeLocation(locationModel);
                            } else {
                                Toast.makeText(CaptainProfileActivity.this, "Empty location", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void storeLocatio(LocationModel model) {

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                root.child("Captain Location").child(userId).setValue(model)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CaptainProfileActivity.this, "Location Data Stored", Toast.LENGTH_SHORT).show();
                                    sweetAlert.dismiss();
                                } else {
                                    String message = Objects.requireNonNull(task.getException()).toString();
                                    Toast.makeText(CaptainProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CaptainProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentLocation(GeoFire geoFire) {
        sweetAlert.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(CaptainProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(CaptainProfileActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(CaptainProfileActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();
                                        //locationModel = new LocationModel(userId,String.valueOf(latitude), String.valueOf(longitude),Prevalent.currentOnlineUser.getName(),Prevalent.currentOnlineUser.getPhone(),Prevalent.currentOnlineUser.getEvaluation(),Prevalent.currentOnlineUser.getImageUrl());
                                        //storeLocation(locationModel);
                                        //storeLocation(geoFire,latitude,latitude,root);
                                        geoFire.setLocation(userId, new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                            @Override
                                            public void onComplete(String key, DatabaseError error) {
                                                if (error != null) {
                                                    Log.e("key", key);
                                                } else {
                                                    System.out.println("Location saved on server successfully!");
                                                    System.out.println(latitude + " " + longitude);
                                                    System.out.println("Location saved on server successfully!");
                                                }
                                            }
                                        });
                                        Toast.makeText(CaptainProfileActivity.this, "Latitude: " + latitude + "\n" + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CaptainProfileActivity.this, CaptainOldTripActivity.class));

                                        //Toast.makeText(CaptainProfileActivity.this, "Latitude: " + latitude + "\n" + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                                        //AddressText.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                    sweetAlert.dismiss();
                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 6);
            }
        }
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(CaptainProfileActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(CaptainProfileActivity.this, 5);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

}
