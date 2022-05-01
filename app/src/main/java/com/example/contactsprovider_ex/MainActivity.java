package com.example.contactsprovider_ex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.list_view);
        cursorAdapter=new CursorAdapter(this,null,true) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View itemView= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
                return itemView;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView textView=view.findViewById(R.id.textView);
                ImageView imageView=view.findViewById(R.id.imageView);

                String name=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                textView.setText(name);
                String thumbnailUri=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                if(thumbnailUri!=null){
                    imageView.setImageURI(Uri.parse(thumbnailUri));
                }else{
                    imageView.setImageResource(R.mipmap.ic_launcher_round);
                }
            }
        };
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               int ps=position;
                Cursor c= (Cursor) cursorAdapter.getItem(ps);
                Log.d("setOnItemClickListener",c.toString());
                Intent intent=new Intent(MainActivity.this,DetailActivity.class);

                String displayName=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                long _id=c.getLong(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                intent.putExtra("disPlayName",displayName);
                intent.putExtra("_id",_id);

                startActivity(intent);

            }
        });
        loadConTacts();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            loadConTacts();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadConTacts(){
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
            return;
        }
        LoaderManager.getInstance(this).initLoader(1, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                return new CursorLoader(MainActivity.this,ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                cursorAdapter.swapCursor(data);

            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                cursorAdapter.swapCursor(null);

            }
        });
    }
}