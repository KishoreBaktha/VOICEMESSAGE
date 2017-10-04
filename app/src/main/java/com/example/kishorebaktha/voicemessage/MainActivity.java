package com.example.kishorebaktha.voicemessage;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button contact;
    Button msg,send;
    int PICK_CONTACT=1;
    TextView tcontact,tmsg;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo,message;
    private final int Req_code_speech_output=143;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contact=(Button)findViewById(R.id.button);
        tcontact=(TextView)findViewById(R.id.t2);
        tmsg=(TextView)findViewById(R.id.t4);
        send=(Button)findViewById(R.id.button3);
        msg=(Button)findViewById(R.id.button2);
    }
    public void CONTACT(View v)
    {
        Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,PICK_CONTACT);
    }
    public void SPEAK(View v)
    {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"HI SPEAK NOW....");
        try
        {
            startActivityForResult(intent,Req_code_speech_output);
        }
        catch (ActivityNotFoundException tim)
        {
            Toast.makeText(getApplicationContext(),"MIKE NOT RESPONDING",Toast.LENGTH_SHORT).show();
        }
    }
    public void SEND(View view)
    {
//        phoneNo = txtphoneNo.getText().toString();
//        message = txtMessage.getText().toString();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver contentResolver=getContentResolver();
        if(requestCode==PICK_CONTACT)
        {
            if(resultCode== ActionBarActivity.RESULT_OK)
            {
                Uri uri=data.getData();
                Cursor c=contentResolver.query(uri,null,null,null,null);
                if(c.moveToFirst())
                {
                    String id=c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    ,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ? ",new String[]{id},null);
                    if(phone.moveToFirst())
                    {
                        String name=phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        tcontact.setText(name);
                        phoneNo=name.trim();
                    }
                }
            }
        }
        if(requestCode==Req_code_speech_output) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tmsg.setText(voiceInText.get(0));
                message=voiceInText.get(0);
            }
        }
    }
}
