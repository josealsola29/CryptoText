package com.example.alsola.proyectoseguridad;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.snatik.storage.Storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import se.simbio.encryption.Encryption;

public class MainActivity extends AppCompatActivity {
    private EditText editTextTextoPlano;
    private EditText editTextTextoCifrado;
    private Button buttonEncriptar;
    private Button buttonDesencriptar;
    private Button buttonDir;
    private Button buttonEmail;
    private FrameLayout linearlayoutMain;
    private FloatingActionButton fab;
    private Storage storage;
    private ActionBar actionBar;

    private String DIR_EXTERNAL_SDCARD;

    private String nomDirectorio;
    private String nomTextoPlano;

    private String textoSecreto;
    private String textoCifrado;
    private String textDesncriptado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindUI();

        solicitarPermisos();
//        SDCARD_ROOT= Environment.getExternalStorageDirectory();

        boolean isWritable = storage.isExternalWritable();
        //Verificar si tiene los permisos de escritura
        if(isWritable){
            Snackbar snackbar = Snackbar
                    .make(linearlayoutMain, "Preparado para crear archivo.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }else{
            Snackbar snackbar = Snackbar
                    .make(linearlayoutMain, "Permiso denegado.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }


        buttonEncriptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leerArchivoTxt();

            }
        });

        buttonDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPathApp();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 alertDialogCrearNomDirectorio();

            }
        });

        buttonDesencriptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDesencriptado();
            }
        });

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

    }
    private void encriptacionAES(String secretText) {
        // es cómo obtener la instancia de cifrado. Debes usar tu propia llave tu propia sal y tu propia matriz de bytes
        Encryption encryption = Encryption.getDefault("LlaveMaestra2", "SomeSalt", new byte[16]);

        // el método encryptOrNull encriptará su texto y si ocurre algún error devolverá null
        // si quieres manejar los errores, puedes llamar al método de cifrado directamente
        textoCifrado = encryption.encryptOrNull(secretText);

        // solo imprimiendo para ver el texto y la cadena encriptada
        editTextTextoCifrado.setText(textoCifrado);

        //
        textDesncriptado = encryption.decryptOrNull(textoCifrado);
    }

    private void leerArchivoTxt(){
        try {
        //Get the text file
        File file = new File(DIR_EXTERNAL_SDCARD);
        //Read text from file
        StringBuilder text = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();
        /** ------------------- E N C R I P T A N A D O -------------------**/
        encriptacionAES(text.toString());

        }catch (Exception e) {
            Snackbar snackbar = Snackbar
                    .make(linearlayoutMain,"Debe seleccionar primero la ruta del mensaje a encriptar. " + e.getMessage(), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void sendEmail(){
        try{
            generarDirectorioEncrypt(getApplicationContext());
            File filelocation = new File(Environment.getExternalStorageDirectory(), nomDirectorio+"/TextoCifrado.txt");
            Uri path = Uri.fromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent .setType("vnd.android.cursor.dir/email");
            String to[] = {"buddycheung2009@hotmail.com "};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Mensaje Encriptado.");
            startActivity(Intent.createChooser(emailIntent , "Enviando Mensaje.."));
        }catch (Exception e ){
            Snackbar snackbar = Snackbar
                    .make(linearlayoutMain,"Debe seleccionar primero la ruta del mensaje a encriptar. " + e.getMessage(), Snackbar.LENGTH_SHORT);
                snackbar.show();
        }
    }

    public void alertDialogCrearNomDirectorio(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Introduzca los datos correspondientes");
        builder.setMessage("Inserte el nombre del directorio");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_archivo,null);

        builder.setView(viewInflated);

         final EditText inputDirectorio =viewInflated.findViewById(R.id.editTextNomDirectorio);
         final EditText inputTextoPlano = viewInflated.findViewById(R.id.editTextNomArchivo);
        // Set up the buttons
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nomDirectorio = inputDirectorio.getText().toString().trim();
                nomTextoPlano = inputTextoPlano.getText().toString().trim();
                generarDirectorio(getApplicationContext());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void alertDialogDesencriptado(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Desencriptado...");
        if(!textDesncriptado.equals(null))
            builder.setMessage(textDesncriptado);
        else {
            Snackbar snackbar = Snackbar
                    .make(linearlayoutMain, "Debe seleccionar primero la ruta del mensaje a encriptar", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        builder.show();
    }

    public void generarDirectorio(Context context) {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), nomDirectorio);
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, nomTextoPlano+".txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(editTextTextoPlano.getText().toString());
                writer.flush();
                writer.close();
            Toast.makeText(context, "Directorio Creado :D", Toast.LENGTH_SHORT).show();
        }
     catch (Exception e) {
            Log.e("ERRRO:",e.getMessage());
        }
    }

    public void generarDirectorioEncrypt(Context context) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), nomDirectorio);
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "TextoCifrado"+".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(textoCifrado);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Directorio Creado :D", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e("ERRRO:",e.getMessage());
        }
    }

    private void getPathApp(){
        // Initialize Builder
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build();
        // Muestra la vista
        chooser.show();

        // get path that the user has chosen
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                DIR_EXTERNAL_SDCARD = path;
            }
        });

    }

    private void bindUI(){
        linearlayoutMain= findViewById(R.id.linearlayoutMain);
        editTextTextoPlano= findViewById(R.id.editTextTextoPlano);
        editTextTextoCifrado=findViewById(R.id.editTextTextoCifrado);
        buttonEncriptar=findViewById(R.id.buttonEncriptar);
        buttonDesencriptar=findViewById(R.id.buttonDesencriptar);
        buttonDir=findViewById(R.id.buttonDir);
        buttonEmail=findViewById(R.id.buttonEmail);
        fab = findViewById(R.id.fab);
    }

    private void solicitarPermisos(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemInfo:
                Intent intent = new Intent(this,InfoActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }
}
