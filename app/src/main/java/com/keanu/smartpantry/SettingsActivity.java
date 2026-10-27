package com.keanu.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.*;

public class SettingsActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout page=UI.page(this,"Keanu | Settings ⚙");
        SharedPreferences prefs=getSharedPreferences("keanu_settings",MODE_PRIVATE);
        Switch alert=new Switch(this);alert.setText("Highlight ingredients expiring in 3 days");
        alert.setChecked(prefs.getBoolean("expiry_alerts",true));page.addView(alert);
        alert.setOnCheckedChangeListener((v,on)->prefs.edit().putBoolean("expiry_alerts",on).apply());
        UI.text(this,page,"Your preference is stored on this device. When enabled, pantry items expiring within three days are highlighted.");
        UI.text(this,page,"ABOUT SMART PANTRY MANAGER");
        UI.text(this,page,"Made for Keanu. Track leftover ingredients and discover meals you can cook without shopping. Suggestions require every ingredient in the recipe, in the right amount.");
        UI.text(this,page,"Data is stored locally using SQLite. No GPS, maps, account or payment is required.");
    }
}
