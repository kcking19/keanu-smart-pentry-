package com.keanu.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.*;

public class RecipeDetailActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        DB db=new DB(this);long id=getIntent().getLongExtra("id",-1);
        LinearLayout page=UI.page(this,"Recipe details");
        ScrollView scroll=new ScrollView(this);
        ((android.view.ViewGroup)page.getParent()).removeView(page);
        scroll.addView(page);setContentView(scroll);
        try(Cursor recipe=db.recipe(id)){
            if(!recipe.moveToFirst()){UI.text(this,page,"Recipe not found.");return;}
            UI.text(this,page,recipe.getString(0));
            UI.text(this,page,"INGREDIENTS");
            try(Cursor req=db.requirements(id)){
                while(req.moveToNext())UI.text(this,page,"• "+req.getString(0)+" — "+DB.amount(req.getDouble(1))+" "+req.getString(2));
            }
            UI.text(this,page,"PREPARATION");
            String[] steps=recipe.getString(1).split(";");
            for(int i=0;i<steps.length;i++)UI.text(this,page,(i+1)+". "+steps[i].trim());
        }finally{db.close();}
    }
}
