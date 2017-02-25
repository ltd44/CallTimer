package com.tishcn.calltimer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        if (hasCallPhonePermission() && hasReadPhoneStatePermission() && hasVibratePermission()
                && hasReceiveBootCompletePermission()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.emptyFrame, new MainFragment());
            ft.commit();
        } else {
            final AppCompatActivity activity = this;
            Snackbar.make(findViewById(android.R.id.content),
                    "Please grant permissions so the timer can function.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(activity
                                    , new String[]{Manifest.permission.READ_PHONE_STATE
                                            , Manifest.permission.CALL_PHONE
                                            , Manifest.permission.VIBRATE
                                            , Manifest.permission.RECEIVE_BOOT_COMPLETED}
                                    , 4);
                        }
                    })
                    .show();
        }
        super.onResume();
    }

    private boolean hasCallPhonePermission() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadPhoneStatePermission() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasVibratePermission() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReceiveBootCompletePermission() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[]
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Thanks for permission!"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.CALL_PHONE)) {;
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "call phone permissions."
                                , Toast.LENGTH_LONG).show();
                    } else if(ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "access to read the phone state."
                                , Toast.LENGTH_LONG).show();
                    } else if(ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.VIBRATE)) {
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "access to vibrate."
                                , Toast.LENGTH_LONG).show();
                    } else if(ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "access to receive boot complete notices."
                                , Toast.LENGTH_LONG).show();
                    } else {
                        goToSettings();
                    }
                }
            }
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 5);
    }

}
