package org.izv.amml.consultaagenda_ad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
//import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final int CONTACTS_PERMISSION = 1;
    private final String TAG = "xyzyx";

    private Button bt_Search;
    private EditText etPhone;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");//verbose
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");//verbose
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissionsResult");//verbose
        switch(requestCode){
            case CONTACTS_PERMISSION:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permiso
                    search();
                } // else{ //sin permiso }
                break;
            default:
                break;
        }
        //requestCode
        //permissions
        //grantResults
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");//verbose
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");//verbose
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");//verbose
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title),
                getString(R.string.message),
                Manifest.permission.READ_CONTACTS,
                CONTACTS_PERMISSION);
        //requestPermission();
    }

    private void initialize() {
        bt_Search = findViewById(R.id.bt_Search);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        bt_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    private void search() {
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };
        String seleccion = null;
        String argumentos[] = null;
        String orden = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(contentUri, proyeccion, seleccion, argumentos, orden);
        int columnName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        String name, num;

        while (cursor.moveToNext()){
            name = cursor.getString(columnName);
            num = cursor.getString(columnNum);
            num = num.replaceAll("[^0-9]", "");
            if(!etPhone.getText().toString().isEmpty()){
                if (num.equals(etPhone.getText().toString())){
                    Log.v(TAG, name + ": " + num);
                    tvResult.setText(name);
                }
            } else{
                Log.v(TAG, "No se ha introducido ningún número");
                tvResult.setText("No se ha introducido ningún número");
            }

        }
    }

    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*La version de Android es posterior a la eligida para crear el proyecto.
             * Esto sucede puesto que estamos la version 5 y a partir de la 6 se produce un cambio
             * en la forma en la que se otorgan los permisos. A partir de la versión 6,
             * se puede solicitar permisos en tiempo de ejecución.
             */
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();//2ª Ejecución
            } else {
                requestPermission();// 1ª Ejecución
            }
        } else {
            //La version de Android es anterior a la 6
            //Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                });

        builder.create().show();
    }
}