package com.keanu.smartpantry;

import android.app.*;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.widget.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantryActivity extends Activity {
    private DB db;
    private ListView list;
    private TextView status;
    private final ArrayList<Long> ids=new ArrayList<>();
    @Override public void onCreate(Bundle b){
        super.onCreate(b);db=new DB(this);
        LinearLayout page=UI.page(this,"Welcome back, Keanu 🌿");
        UI.text(this,page,"Your pantry, less food waste. Tap an item to edit it; long-press to delete.");
        UI.button(this,page,"+ Add ingredient",()->UI.go(this,EditItemActivity.class));
        UI.button(this,page,"🍽 Suggested recipes",()->UI.go(this,SuggestionsActivity.class));
        UI.button(this,page,"⚙ Settings",()->UI.go(this,SettingsActivity.class));
        status=UI.text(this,page,"Loading pantry…");
        list=new ListView(this);page.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        list.setOnItemClickListener((parent,v,pos,row)->{
            Intent i=new Intent(this,EditItemActivity.class);i.putExtra("id",ids.get(pos));startActivity(i);
        });
        list.setOnItemLongClickListener((parent,v,pos,row)->{
            long id=ids.get(pos);
            new AlertDialog.Builder(this).setTitle("Delete ingredient?")
                .setMessage("This will remove the ingredient from your pantry.")
                .setPositiveButton("Delete",(d,w)->{db.delete(id);refresh();})
                .setNegativeButton("Cancel",null).show();return true;
        });
    }
    @Override protected void onResume(){super.onResume();refresh();}
    private void refresh(){
        ids.clear();ArrayList<String> rows=new ArrayList<>();
        try(Cursor c=db.pantry()){
            while(c.moveToNext()){
                ids.add(c.getLong(0));
                String expiry=c.getString(4);
                String flag="";
                if(expiry!=null&&!expiry.isEmpty()&&getSharedPreferences("keanu_settings",MODE_PRIVATE).getBoolean("expiry_alerts",true)){
                    try{
                        Date date=new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT).parse(expiry);
                        long days=(date.getTime()-System.currentTimeMillis())/86400000L;
                        if(days<0)flag="⚠ Expired • ";
                        else if(days<=3)flag="⚠ Expiring soon • ";
                    }catch(Exception ignored){}
                }
                rows.add(flag+(c.getString(1)+"  •  "+DB.amount(c.getDouble(2))+" "+c.getString(3)
                    +(expiry==null||expiry.isEmpty()?"":"  •  expires "+expiry));
            }
        }
        status.setText(rows.isEmpty()?"Your pantry is empty. Add your first ingredient!":
            "Your pantry ("+rows.size()+" items)");
        list.setAdapter(new PantryAdapter(this,rows));
    }
    @Override protected void onDestroy(){db.close();super.onDestroy();}
}
