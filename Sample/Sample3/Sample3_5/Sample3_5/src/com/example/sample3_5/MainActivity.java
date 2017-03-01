package com.example.sample3_5;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button okButton =(Button)findViewById(R.id.button1);
        final Button cancelButton =(Button)findViewById(R.id.button2);
        final EditText uid=(EditText)findViewById(R.id.EditText1);
        final EditText pwd=(EditText)findViewById(R.id.editText2);
        final EditText log=(EditText)findViewById(R.id.editText3);
        okButton.setOnClickListener(
        		new View.OnClickListener() {				
					public void onClick(View v) {
						String uidStr=uid.getText().toString();
						String pwdStr=pwd.getText().toString();
						log.append("”√ªß√˚£∫"+uidStr+"√‹¬Î"+pwdStr+"\n");						
					}
				});
        cancelButton.setOnClickListener(

        		new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
					 uid.setText("");
					 pwd.setText("");						
					}
				}
        		);
    }


}
