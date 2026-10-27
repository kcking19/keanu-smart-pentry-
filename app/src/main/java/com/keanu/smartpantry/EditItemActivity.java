package com.keanu.smartpantry;

import android.app.*;
import android.os.Bundle;
import android.database.Cursor;
import android.text.InputType;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class EditItemActivity extends Activity {
    private DB db;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);db=new DB(this);
        long id=getIntent().getLongExtra("id",-1);
        ScrollView scroll=new ScrollView(this);
        LinearLayout page=UI.page(this,id<0?"Add ingredient":"Edit ingredient");
        // Keep controls accessible on compact phone screens.
        ((android.view.ViewGroup)page.getParent()).removeView(page);
        scroll.addView(page);setContentView(scroll);
        EditText name=UI.input(this,page,"Ingredient name (e.g. tomatoes)");
        EditText qty=UI.input(this,page,"Quantity (e.g. 500)");
        qty.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Spinner unit=new Spinner(this);
        String[] units={"piece","g","kg","ml","l","slice","clove"};
        ArrayAdapter<String> unitAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,units);
        unit.setAdapter(unitAdapter);page.addView(unit);
        EditText expiry=UI.input(this,page,"Expiry date YYYY-MM-DD (optional)");
        UI.text(this,page,"Select the ingredient unit above. Quantity must be greater than zero.");
        if(id>=0){
            try(Cursor c=db.item(id)){
                if(c.moveToFirst()){
                    name.setText(c.getString(1));qty.setText(DB.amount(c.getDouble(2)));
                    for(int i=0;i<units.length;i++)if(units[i].equals(c.getString(3)))unit.setSelection(i);
                    expiry.setText(c.getString(4));
                }else{UI.message(this,"Ingredient not found");finish();return;}
            }
        }
        UI.button(this,page,"Save ingredient",()->{
            String n=name.getText().toString().trim();
            String qText=qty.getText().toString().trim();
            String e=expiry.getText().toString().trim();
            if(n.isEmpty()){name.setError("Ingredient name is required");name.requestFocus();return;}
            double q;
            try{q=Double.parseDouble(qText);}catch(NumberFormatException ex){qty.setError("Enter a number");qty.requestFocus();return;}
            if(!Double.isFinite(q)||q<=0){qty.setError("Enter a positive quantity");qty.requestFocus();return;}
            if(!e.isEmpty()){
                SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT);f.setLenient(false);
                ParsePosition pos=new ParsePosition(0);
                if(f.parse(e,pos)==null||pos.getIndex()!=e.length()||!e.matches("\\d{4}-\\d{2}-\\d{2}")){
                    expiry.setError("Enter a valid YYYY-MM-DD date");expiry.requestFocus();return;
                }
            }
            long saved=db.save(id,n,q,unit.getSelectedItem().toString(),e);
            if(saved<0){UI.message(this,"Unable to save ingredient");return;}
            UI.message(this,"Ingredient saved");finish();
        });
        if(id>=0)UI.button(this,page,"Delete ingredient",()->new AlertDialog.Builder(this)
            .setTitle("Delete this ingredient?")
            .setPositiveButton("Delete",(dialog,which)->{db.delete(id);finish();})
            .setNegativeButton("Cancel",null).show());
    }
    @Override protected void onDestroy(){db.close();super.onDestroy();}
}
