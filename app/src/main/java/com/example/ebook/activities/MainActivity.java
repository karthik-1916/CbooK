package com.example.ebook.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ebook.R;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CARLZ";

    private Runnable permissionAndUISetupThread = ()->{
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            Log.i(TAG,"permissionAndUISetupThread: Requested storage permission");
        }
    };



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage(R.string.exit_app);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionAndUISetupThread.run();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CbooK");
        setSupportActionBar(toolbar);
        Log.i(TAG,"onCreate: Execute");
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        Log.i(TAG,"onCreateOptionsMenu: Executed");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.about:
                showAboutDialog();
                break;
            case R.id.search:
                showSearchDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.search_dialog,null);
        initSearchLayout(view);
        builder.setView(view);
        builder.create().show();
    }

    private void initSearchLayout(View view) {
        EditText editText = view.findViewById(R.id.input);
        Button button = view.findViewById(R.id.search_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    Snackbar.make(MainActivity.this.findViewById(R.id.relMain), R.string.please_provide_book_name, Snackbar.LENGTH_SHORT).show();
                }else {
                    String query = editText.getText().toString();
                    Intent intent = new Intent(MainActivity.this,DataFetcherActivity.class);
                    intent.putExtra("query",query);
                    startActivity(intent);
                }
            }
        });
    }

    public void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
        initAboutDialog(view);
        builder.setView(view);
        builder.setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    public void initAboutDialog(View view) {
        TextView appVersion = (TextView)view.findViewById(R.id.about_version);
        TextView aboutDescription = (TextView)view.findViewById(R.id.about_description);
        TextView aboutDevDescription = (TextView)view.findViewById(R.id.about_developer_description);

        appVersion.setText(R.string.app_version);
        aboutDescription.setText(R.string.about_app_description);
        aboutDescription.setMovementMethod(LinkMovementMethod.getInstance());
        aboutDevDescription.setText(R.string.about_author_description);
        aboutDevDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
