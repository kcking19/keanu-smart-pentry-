package com.keanu.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.*;

public final class UI {
    private UI() {}
    static int dp(Activity a, int n) { return (int)(n*a.getResources().getDisplayMetrics().density+0.5f); }
    static LinearLayout page(Activity a, String title) {
        LinearLayout page=new LinearLayout(a);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(a,18),dp(a,18),dp(a,18),dp(a,12));
        page.setBackgroundColor(Color.rgb(246,250,246));
        TextView heading=new TextView(a);
        heading.setText(title); heading.setTextSize(25); heading.setTypeface(null,Typeface.BOLD);
        heading.setTextColor(Color.rgb(26,91,57));
        heading.setPadding(0,0,0,dp(a,12));
        page.addView(heading);
        a.setContentView(page); return page;
    }
    static Button button(Activity a, LinearLayout l, String title, Runnable action) {
        Button b=new Button(a); b.setText(title); b.setAllCaps(false);
        b.setTextSize(16); b.setTextColor(Color.WHITE);
        GradientDrawable bg=new GradientDrawable(); bg.setColor(Color.rgb(33,112,70)); bg.setCornerRadius(dp(a,12));
        b.setBackground(bg);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(a,48));
        lp.bottomMargin=dp(a,9); l.addView(b,lp);
        b.setOnClickListener(v->action.run()); return b;
    }
    static EditText input(Activity a,LinearLayout l,String hint){
        EditText e=new EditText(a); e.setHint(hint);e.setSingleLine(true);
        e.setTextSize(16); l.addView(e,new LinearLayout.LayoutParams(-1,dp(a,55)));return e;
    }
    static TextView text(Activity a,LinearLayout l,String value){
        TextView t=new TextView(a);t.setText(value);t.setTextSize(16);t.setTextColor(Color.rgb(48,65,53));
        t.setPadding(0,dp(a,8),0,dp(a,8));l.addView(t);return t;
    }
    static void go(Activity a,Class<?> cls){a.startActivity(new Intent(a,cls));}
    static void message(Activity a,String s){Toast.makeText(a,s,Toast.LENGTH_LONG).show();}
}
