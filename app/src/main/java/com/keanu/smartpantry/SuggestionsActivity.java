package com.keanu.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.content.Intent;
import android.widget.*;
import java.util.ArrayList;

public class SuggestionsActivity extends Activity {
    private DB db;
    private ListView list;
    private TextView status;
    private final ArrayList<Long> ids=new ArrayList<>();
    @Override public void onCreate(Bundle b){
        super.onCreate(b);db=new DB(this);
        LinearLayout page=UI.page(this,"Suggested recipes 🍽");
        UI.text(this,page,"Only recipes with ALL required ingredients and sufficient quantities are shown.");
        UI.button(this,page,"Refresh suggestions",this::refresh);
        UI.button(this,page,"+ Add ingredient",()->UI.go(this,EditItemActivity.class));
        status=UI.text(this,page,"");
        list=new ListView(this);page.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        list.setOnItemClickListener((a,v,pos,row)->{
            Intent i=new Intent(this,RecipeDetailActivity.class);i.putExtra("id",ids.get(pos));startActivity(i);
        });
    }
    @Override protected void onResume(){super.onResume();refresh();}
    private void refresh(){
        ids.clear();ArrayList<String> rows=new ArrayList<>();
        for(Long id:db.matches())try(Cursor c=db.recipe(id)){
            if(c.moveToFirst()){ids.add(id);rows.add(c.getString(0));}
        }
        status.setText(rows.isEmpty()?"No recipes match your pantry yet — add more ingredients.":
            rows.size()+" recipe(s) you can make right now:");
        list.setAdapter(new PantryAdapter(this,rows));
    }
    @Override protected void onDestroy(){db.close();super.onDestroy();}
}
