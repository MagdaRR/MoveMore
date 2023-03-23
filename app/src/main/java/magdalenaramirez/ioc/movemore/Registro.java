package magdalenaramirez.ioc.movemore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    TextInputEditText mTI_Reg_Nombre;
    TextInputEditText mTI_Reg_Apellidos;
    TextInputEditText mTI_Reg_Telefono;
    TextInputEditText mTI_Reg_Direccion;
    TextInputEditText mTI_Reg_Usuario;
    TextInputEditText mTI_Reg_Email;
    TextInputEditText mTI_Reg_Contrasena;
    Button mbtn_Reg_Aceptar;
    Button mbtn_Reg_Cancelar;

    RequestQueue requestQueue;
    //http://10.2.66.56/index.php/user/register?firstname=test2&lastname=test2&phone=123&addr=test2&username=test2&email=test2@test.net&password=test2
    private static final String URl_registro="http://10.2.66.56/index.php/user/register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Toolbar - ActionBar - Logout
        ImageView mIM_Menu = findViewById(R.id.icon_menu);
        mIM_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        //Elementos
        mTI_Reg_Nombre = (TextInputEditText) findViewById(R.id.TI_Reg_Nombre);
        mTI_Reg_Apellidos = (TextInputEditText) findViewById(R.id.TI_Reg_Apellidos);
        mTI_Reg_Telefono = (TextInputEditText) findViewById(R.id.TI_Reg_Telefono);
        mTI_Reg_Direccion = (TextInputEditText) findViewById(R.id.TI_Reg_Direccion);
        mTI_Reg_Usuario = (TextInputEditText) findViewById(R.id.TI_Reg_Usuario);
        mTI_Reg_Email = (TextInputEditText) findViewById(R.id.TI_Reg_Email);
        mTI_Reg_Contrasena = (TextInputEditText) findViewById(R.id.TI_Reg_Contrasena);
        mbtn_Reg_Aceptar = (Button) findViewById(R.id.btn_Reg_Aceptar);
        mbtn_Reg_Cancelar = (Button) findViewById(R.id.btn_Reg_Cancelar);

        //Crear registro nuevo
        mbtn_Reg_Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validar los datos válidos para el registro, si son OK formalizar.
                validarDatos();
            }

        });

        //Cancelar - Volver a inicio sesión
        mbtn_Reg_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutSes();
            }

        });

    // Cierre onCreate
    }

    // Menu ActionBar
    private void showMenu(View view){

        PopupMenu popupMenu = new PopupMenu(Registro.this,view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.popup_logout)
                    logoutSes();
                return true;
            }
        });

        popupMenu.show();
        // Cierre showMenu
    }

    // Logout Sesion - Volver al inicio
    private void logoutSes(){
        SharedPreferences SM = getSharedPreferences("userrecord",0);
        SharedPreferences.Editor edit = SM.edit();
        edit.putBoolean("userlogin",false);
        edit.commit();

        Intent intent = new Intent(Registro.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void validarDatos() {
        String nombre = mTI_Reg_Nombre.getText().toString();
        String apellidos = mTI_Reg_Apellidos.getText().toString();
        String telefono = mTI_Reg_Telefono.getText().toString();
        String direcc = mTI_Reg_Direccion.getText().toString();
        String usuario = mTI_Reg_Usuario.getText().toString();
        String email = mTI_Reg_Email.getText().toString();
        String contrase_a = mTI_Reg_Contrasena.getText().toString();

        boolean a = esNombreValido(nombre);
        boolean b = esApellidoValido(apellidos);
        boolean c = esTelefonoValido(telefono);
        boolean d = esDireccValido(direcc);
        boolean e = esUsuarioValido(usuario);
        boolean f = esEmailValido(email);
        boolean g = esContraValido(contrase_a);

        if (a && b && c && d && e && f && g) {
            //Envío de datos al JSON para insertar datos
            registroRequest();
            // OK, se pasa a la siguiente acción
            Toast.makeText(this, "¡Datos validados correctamente!", Toast.LENGTH_LONG).show();

        }
    }

    private boolean esNombreValido(String nombre){
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            mTI_Reg_Nombre.setError("Nombre inválido");
            return false;
        } else {
            mTI_Reg_Nombre.setError(null);
        }
        return true;
    }

    private boolean esApellidoValido(String apellidos){
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        if (!patron.matcher(apellidos).matches() || apellidos.length() > 30) {
            mTI_Reg_Apellidos.setError("Apellidos inválido");
            return false;
        } else {
            mTI_Reg_Apellidos.setError(null);
        }
        return true;
    }

    private boolean esTelefonoValido(String telefono){
        if (!Patterns.PHONE.matcher(telefono).matches()) {
            mTI_Reg_Telefono.setError("Teléfono inválido");
            return false;
        } else {
            mTI_Reg_Telefono.setError(null);
        }
        return true;
    }

    private boolean esDireccValido(String direcc){
        Pattern patron = Pattern.compile("^[a-zA-Z0-9\\s]+$");
        if (!patron.matcher(direcc).matches() || direcc.length() > 30) {
            mTI_Reg_Direccion.setError("Dirección inválida");
            return false;
        } else {
            mTI_Reg_Direccion.setError(null);
        }
        return true;
    }

    private boolean esUsuarioValido(String usuario){
        Pattern patron = Pattern.compile("^[a-zA-Z0-9\\s]+$");
        if (!patron.matcher(usuario).matches() || usuario.length() > 30) {
            mTI_Reg_Usuario.setError("Nombre usuario inválido");
            return false;
        } else {
            mTI_Reg_Usuario.setError(null);
        }
        return true;
    }

    private boolean esEmailValido(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mTI_Reg_Email.setError("Correo electrónico inválido");
            return false;
        } else {
            mTI_Reg_Email.setError(null);
        }
        return true;
    }

    private boolean esContraValido(String contrase_a) {
        Pattern patron = Pattern.compile("^[a-zA-Z0-9]+$");
        if (!patron.matcher(contrase_a).matches() || contrase_a.length() > 30) {
            mTI_Reg_Contrasena.setError("Contraseña inválida");
            return false;
        } else {
            mTI_Reg_Contrasena.setError(null);
        }
        return true;
    }

    private void registroRequest(){

        Map<String, String> parameters = new HashMap<>();

        //firstname=test2,lastname=test2,phone=123,addr=test2,username=test2,email=test2@test.net,password=test2
        // parameters.put(first_name","last_name",phone_number","address","username","email","password");
        parameters.put("firstname", mTI_Reg_Nombre.getText().toString());
        parameters.put("lastname", mTI_Reg_Apellidos.getText().toString());
        parameters.put("phone", mTI_Reg_Telefono.getText().toString());
        parameters.put("addr", mTI_Reg_Direccion.getText().toString());
        parameters.put("username", mTI_Reg_Usuario.getText().toString());
        parameters.put("email", mTI_Reg_Email.getText().toString());
        parameters.put("password", mTI_Reg_Contrasena.getText().toString());

        String mURL_registro = URl_registro+Util.getParamsString(parameters);
        Log.i("Registro",Util.getParamsString(parameters));
        Log.i("Registro",mURL_registro.toString());

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URl_registro+Util.getParamsString(parameters),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response:", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");
                            Log.i("registroRequest",response.toString());

                            if(status){
                                //Toast.makeText(Registro.this,"Registro respuesta true", Toast.LENGTH_LONG).show();
                                Toast.makeText(Registro.this,message, Toast.LENGTH_LONG).show();
                                limpiarDatosTextView();
                            }else{
                                //Toast.makeText(Registro.this,"Registro respuesta false", Toast.LENGTH_LONG).show();
                                Toast.makeText(Registro.this,message, Toast.LENGTH_LONG).show();
                            }

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
        requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(request);
    // Cierre del stringRequest
    }

    private void limpiarDatosTextView(){
        mTI_Reg_Nombre.getText().clear();
        mTI_Reg_Apellidos.getText().clear();
        mTI_Reg_Telefono.getText().clear();
        mTI_Reg_Direccion.getText().clear();
        mTI_Reg_Usuario.getText().clear();
        mTI_Reg_Email.getText().clear();
        mTI_Reg_Contrasena.getText().clear();
    }

// Cierre principal
}