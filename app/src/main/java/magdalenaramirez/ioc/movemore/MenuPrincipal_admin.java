package magdalenaramirez.ioc.movemore;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MenuPrincipal_admin extends AppCompatActivity {

    private String file = "credenciales_login";
    private String fileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_admin);

        //Toolbar - ActionBar - Logout
        ImageView mIM_Menu = findViewById(R.id.icon_menu);
        mIM_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });

        // Cierre onCreate
    }

    // Menu ActionBar
    private void showMenu(View view){

        PopupMenu popupMenu = new PopupMenu(MenuPrincipal_admin.this,view);
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

    private void logoutSes(){
        //Borramos las credenciales
        deleteFile_UserLogin();

        SharedPreferences SM = getSharedPreferences("userrecord",0);
        SharedPreferences.Editor edit = SM.edit();
        edit.putBoolean("userlogin",false);
        edit.commit();

        Intent intent = new Intent(MenuPrincipal_admin.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void deleteFile_UserLogin() {

        try {
            FileOutputStream fOut = openFileOutput(file, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), file);
            Toast.makeText(getBaseContext(), "File Deleted:" + fileDir, Toast.LENGTH_LONG).show();
            fileDir.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// Cierre principal
}