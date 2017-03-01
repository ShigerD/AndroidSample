package com.example.contextmenue;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {
	final int MENU1=1;
	final int MENU2=2;
	final int MENU3=3;
	final int MENU4=4;
	final int MENU5=5;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.registerForContextMenu(findViewById(R.id.editText1));
        this.registerForContextMenu(findViewById(R.id.editText2));
        this.registerForContextMenu(findViewById(R.id.editText3));
        
    }

    public void onCreateContextMenu(ContextMenu menu,View v,
    		ContextMenu.ContextMenuInfo menuInfo){
    	menu.setHeaderIcon(R.drawable.h0);
    	if(v==findViewById(R.id.editText1)){
    		menu.add(0, MENU1,0,R.string.mi1);
    		menu.add(0, MENU2,0,R.string.mi2);
    		menu.add(0, MENU3,0,R.string.mi3);
    	}
    	else if(v==findViewById(R.id.editText2)){
    		menu.add(0, MENU1,0,R.string.mi1);
    		menu.add(0, MENU4,0,R.string.mi4);
    		menu.add(0, MENU5,0,R.string.mi5);
    	}
    }
    public boolean onContextItemSelected(MenuItem mi){
		switch(mi.getItemId()){
		case MENU1:
		case MENU2:
		case MENU3:
			EditText et1=(EditText)this.findViewById(R.id.editText1);
			et1.append("\n"+mi.getTitle()+"被按下");
			break;
		case MENU4:
		case MENU5:
			EditText et2=(EditText)this.findViewById(R.id.editText2);
			et2.append("\n"+mi.getTitle()+"被按下");
			break;
		}
		return true;
    	
    }
}
