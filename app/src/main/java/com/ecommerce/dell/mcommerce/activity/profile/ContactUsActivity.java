package com.ecommerce.dell.mcommerce.activity.profile;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.ecommerce.dell.mcommerce.R;

import java.util.Locale;


public class ContactUsActivity extends ActionBarActivity {

    private EditText mailbody, senderName, sendePhone, emailSubject;

    private SharedPreferences contactExists;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_contactus);

        SharedPreferences setting = getSharedPreferences("setting", 0);
        language = setting.getString("language", "");
        if (!language.equals("ar")) {
            language = "en";
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_contactus);

        emailSubject = (EditText) findViewById(R.id.emailSubject);
        senderName = (EditText) findViewById(R.id.senderName);
        sendePhone = (EditText) findViewById(R.id.senderPhone);
        mailbody = (EditText) findViewById(R.id.mailbody);

        Button sendmail_submit = (Button) findViewById(R.id.sendmail_submit);
        sendmail_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senderName.getText().toString().equals("")){

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_name), null,
                            new String[]{getString(R.string.ok)}, null, ContactUsActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (sendePhone.getText().toString().equals("")){

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_phone), null,
                            new String[]{getString(R.string.ok)}, null, ContactUsActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (emailSubject.getText().toString().equals("")){

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_subject), null,
                            new String[]{getString(R.string.ok)}, null, ContactUsActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (mailbody.getText().toString().equals("")){

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_comment), null,
                            new String[]{getString(R.string.ok)}, null, ContactUsActivity.this,
                            AlertView.Style.Alert, null).show();

                } else {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "wecare@arabianoud.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, mailbody.getText().toString() + "\n" + senderName.getText().toString() + "\n" + sendePhone.getText().toString());

                    try {
                        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                        senderName.setText("");
                        sendePhone.setText("");
                        emailSubject.setText("");
                        mailbody.setText("");
                        Log.i("Finished sending email", "");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ContactUsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        contactExists = getSharedPreferences("flag",0);

        boolean flag = contactExists.getBoolean("flag", false);
        if (!flag){
            addContact("ArabianOud", "00966559774497");
        }

        Log.d("Flag",String.valueOf(flag));

        TextView phone = (TextView) findViewById(R.id.phonenum);
        TextView whatsappnum = (TextView) findViewById(R.id.whatsappnum);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8001242030"));
                startActivity(callIntent);
            }
        });

        whatsappnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri1 = Uri.parse("smsto:" + "+966559774497");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri1);
                i.setPackage("com.whatsapp");

                if (isPackageExisted("com.whatsapp")) {
                    startActivity(Intent.createChooser(i, ""));
                } else
                    Toast.makeText(getBaseContext(), "Whatsapp is not installed", Toast.LENGTH_LONG).show();

            }


        });
    }

    public boolean isPackageExisted(String targetPackage){
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void addContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);

        SharedPreferences.Editor editor = contactExists.edit();
        editor.putBoolean("flag",true);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
