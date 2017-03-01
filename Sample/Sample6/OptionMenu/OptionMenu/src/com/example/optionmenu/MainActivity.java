package com.example.optionmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MainActivity extends Activity {
	final int MENU_GENDER_MALE=0;
	final int MENU_GENDER_FEMALE=1;
	final int MENU_HOBBY1=2;
	final int MENU_HOBBY2=3;
	final int MENU_HOBBY3=4;
	final int MENU_OK=5;
	final int MENU_GENDER=6;
	final int MENU_HOBBY=7;
	final int GENDER_GROUP=0;
	final int HOBBY_GROUP=1;
	final int MAIN_GROUP=2;
	MenuItem[] miahobby=new MenuItem[3];
	MenuItem male=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	public void appendStateStr(){
    		String result="你选择的性别为：";
    		if(male.isCheckable()){
    				result=result+"男";	
    		}
    		else {
    		result=result+"女";	
    		}
    	
    	String hobbyStr="";
    	for(MenuItem mi:miahobby){
    		if(mi.isChecked()){
    			hobbyStr=hobbyStr+mi.getTitle()+",";
    			
    		}
    	}
		if(hobbyStr.length()>0){
			result=result+",你的爱好："
		+hobbyStr.substring(0,hobbyStr.length()-1)+"。\n";
		}
		else{
			result=result+".\n";
			
		}
		EditText ed=(EditText)MainActivity.this.findViewById(R.id.editText1);
		ed.append(result);
    }
}
