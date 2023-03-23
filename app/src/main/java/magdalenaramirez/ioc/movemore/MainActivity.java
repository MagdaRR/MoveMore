package magdalenaramirez.ioc.movemore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView mTV_Main_Email;
    TextView mTV_Main_Passw;
    TextView mTV_Main_Registro;
    TextView mTV_Main_RecContra;

    SharedPreferences SM;
    RequestQueue requestQueue;
    //http://10.2.66.56/index.php/user/login?username=user_user1&password=11&email=asdgaehg@gaega
    private static final String URl_login="http://10.2.66.56/index.php/user/login";

    //Datos del txt de credenciales de login
    private String file = "credenciales_login";
    private String fileContents;
    private boolean isAdminLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Comunicación respuesta JSON con Volley
        //Volley uso de redes, solicitudes o cargar datos en el servidor
        requestQueue = Volley.newRequestQueue(this);

        //Instancia de elementos
        mTV_Main_Email = (TextView) findViewById(R.id.TextViewEmail);
        mTV_Main_Passw = (TextView) findViewById(R.id.TextViewPassw);
        MaterialButton loginbtn = (MaterialButton) findViewById(R.id.loginbtn);

        //Leer fichero credenciales al arrancar app
        readFile_UserLogin();

        //Botón_CLICK LOGIN - Verificar login (email/contraseña)
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verificar email y contraseña de la respuesta JSON
                if (mTV_Main_Email.getText().toString().isEmpty()|| mTV_Main_Passw.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(),"Invalid Username or Password!",Toast.LENGTH_LONG).show();
                }else {
                    loginRequest();
                }
            }
        });

        //TextView_CLICK - Registrar un nuevo Usuario
        mTV_Main_Registro = findViewById(R.id.TextViewRegistro);
        mTV_Main_Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Registro.class);
                startActivity(intent);
            }
        });

        //TextView_CLICK -Recuperar contraseña
        mTV_Main_RecContra = findViewById(R.id.TextViewRecContra);
        mTV_Main_RecContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    // Cierre del onCreate
    }

    private void writeFile_UserLogin(){
        fileContents = mTV_Main_Email.getText().toString();

        try{
            FileOutputStream fOut = openFileOutput(file, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(),file);
            //Toast.makeText(getBaseContext(),"File Saved at" + fileDir,Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile_UserLogin(){
        fileContents = mTV_Main_Email.getText().toString();

        try{
            FileInputStream fIn = openFileInput(file);
            int c;
            String temp = "";
            while ((c = fIn.read())!= -1)
            {
                temp = temp + Character.toString((char)c);
            }
            //Evaluar si el fichero contiene usuario creado o está vacío el fichero
            if(temp != ""){
                Toast.makeText(getBaseContext(),"usuario conectado: " + temp,Toast.LENGTH_LONG).show();

                //Con SharedPreference recuperamos valores guardados al cerrar app
                SharedPreferences prefs = getSharedPreferences("sharedPrefName", Context.MODE_PRIVATE);
                boolean bool= prefs.getBoolean("isAdminLogin",isAdminLogin);

                if (bool) {
                    Intent intent = new Intent(MainActivity.this,MenuPrincipal_admin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginRequest(){

        Map<String, String> parameters = new HashMap<>();
        // parameters.put("email", "password");
        parameters.put("email", mTV_Main_Email.getText().toString());
        parameters.put("password", mTV_Main_Passw.getText().toString());

        String mURL_login = URl_login+Util.getParamsString(parameters);
        Log.i("login",mURL_login.toString());

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URl_login+Util.getParamsString(parameters),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //JSON - Con los datos del login para respuesta
                            JSONObject jsonObject = new JSONObject(response.toString());
                            boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");

                            if(status == true) {
                                //JSON - Con los datos del usuario, para obtener isAdmin
                                JSONObject jsonObject_user = jsonObject.getJSONObject("user");
                                int admin = jsonObject_user.getInt("is_admin");

                                //Guardar datos del usuario conectado - credenciales
                                writeFile_UserLogin();
                                //validar si es usuario admin o no (0 = noAdmin / 1 = admin)
                                if (admin == 0) {
                                    isAdminLogin = false;
                                    Intent intent_admin = new Intent(MainActivity.this, MenuPrincipal.class);
                                    startActivity(intent_admin);
                                    finish();
                                } else {
                                    isAdminLogin = true;
                                    Intent intent = new Intent(MainActivity.this, MenuPrincipal_admin.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
                            //Guadar el valor de la boleana (isAdminLogin) para reutirlizar al cerrar app
                            sharedPreference_login();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        //Añadir solicitud
        requestQueue.add(request);
    // Cierre del stringRequest
    }

    private void sharedPreference_login(){
        SharedPreferences prefs= getSharedPreferences("sharedPrefName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= prefs.edit();
        editor.putBoolean("isAdminLogin", isAdminLogin);
        editor.apply();
    }

// Cierre principal
}