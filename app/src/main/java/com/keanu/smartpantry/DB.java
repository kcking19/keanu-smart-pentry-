package com.keanu.smartpantry;
import android.content.*;import android.database.*;import android.database.sqlite.*;import java.util.*;
public class DB extends SQLiteOpenHelper {
 public DB(Context c){super(c,"keanu_pantry.db",null,1);} 
 public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE pantry(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,quantity REAL NOT NULL,unit TEXT NOT NULL,expiry TEXT)");d.execSQL("CREATE TABLE recipes(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,steps TEXT NOT NULL)");d.execSQL("CREATE TABLE requirements(recipe_id INTEGER NOT NULL,ingredient TEXT NOT NULL,quantity REAL NOT NULL,unit TEXT NOT NULL)");seed(d);}
 public void onUpgrade(SQLiteDatabase d,int o,int n){}
 // Ingredient specifications use name:quantity:unit, separated by semicolons.
 void seed(SQLiteDatabase d){String[][] recipes={
 {"Tomato Salad","Chop tomatoes; season and serve.","tomato:2:piece; salt:1:g"},
 {"Scrambled Eggs","Beat eggs; cook gently in butter.","egg:2:piece; butter:10:g"},
 {"Butter Toast","Toast bread and spread butter.","bread:2:slice; butter:10:g"},
 {"Cheese Sandwich","Layer cheese between bread slices.","bread:2:slice; cheese:40:g"},
 {"Tomato Sandwich","Slice tomato and assemble sandwich.","bread:2:slice; tomato:1:piece"},
 {"Egg Sandwich","Boil eggs and place between bread.","egg:2:piece; bread:2:slice"},
 {"Banana Oats","Cook oats with milk; add banana.","oats:50:g; milk:200:ml; banana:1:piece"},
 {"Simple Porridge","Simmer oats and water until soft.","oats:50:g; water:250:ml"},
 {"Rice Bowl","Boil rice in water with salt.","rice:100:g; water:250:ml; salt:1:g"},
 {"Garlic Rice","Cook rice and stir in garlic.","rice:100:g; water:250:ml; garlic:2:clove"},
 {"Boiled Potatoes","Boil potatoes; season with salt.","potato:2:piece; water:500:ml; salt:2:g"},
 {"Mashed Potatoes","Boil and mash potatoes with milk.","potato:2:piece; milk:100:ml; butter:15:g"},
 {"Carrot Salad","Grate carrots and dress with lemon.","carrot:2:piece; lemon:1:piece"},
 {"Cucumber Salad","Slice cucumber and tomato.","cucumber:1:piece; tomato:1:piece"},
 {"Cheese Omelette","Beat eggs, add cheese and fry.","egg:2:piece; cheese:30:g; butter:10:g"},
 {"Pasta Butter","Boil pasta; toss with butter.","pasta:100:g; water:500:ml; butter:15:g"},
 {"Tomato Pasta","Boil pasta; mix with chopped tomatoes.","pasta:100:g; water:500:ml; tomato:2:piece"},
 {"Peanut Butter Toast","Toast bread and spread peanut butter.","bread:2:slice; peanut butter:25:g"},
 {"Yoghurt Banana","Slice banana into yoghurt.","yoghurt:150:g; banana:1:piece"},
 {"Milk Smoothie","Blend banana with milk.","banana:1:piece; milk:250:ml"}};
 for(String[] r:recipes){ContentValues v=new ContentValues();v.put("name",r[0]);v.put("steps",r[1]);long id=d.insert("recipes",null,v);for(String spec:r[2].split(";")){String[] s=spec.trim().split(":");ContentValues q=new ContentValues();q.put("recipe_id",id);q.put("ingredient",s[0]);q.put("quantity",Double.parseDouble(s[1]));q.put("unit",s[2]);d.insert("requirements",null,q);}}}
 static String amount(double n){return n==Math.rint(n)?Long.toString((long)n):Double.toString(n);}
 static String norm(String s){s=s.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+"," ");if(s.equals("tomatoes"))return "tomato";if(s.equals("potatoes"))return "potato";if(s.equals("eggs"))return "egg";if(s.equals("loaves"))return "loaf";if(s.equals("knives"))return "knife";if(s.equals("cheeses"))return "cheese";if(s.endsWith("s")&&!s.endsWith("ss")&&!s.equals("oats"))return s.substring(0,s.length()-1);return s;}
 // Converts supported mass and volume units to canonical g/ml; rejects incompatible units.
 static double convert(double q,String u){u=u.trim().toLowerCase(Locale.ROOT);switch(u){case "kg":return q*1000;case "l":return q*1000;case "g":case "ml":case "piece":case "slice":case "clove":return q;default:return -1;}}
 static String group(String u){u=u.trim().toLowerCase(Locale.ROOT);if(u.equals("kg")||u.equals("g"))return "mass";if(u.equals("l")||u.equals("ml"))return "volume";return u;}
 public long save(long id,String name,double quantity,String unit,String expiry){ContentValues v=new ContentValues();v.put("name",norm(name));v.put("quantity",quantity);v.put("unit",unit.trim().toLowerCase(Locale.ROOT));v.put("expiry",expiry);if(id<0)return getWritableDatabase().insert("pantry",null,v);getWritableDatabase().update("pantry",v,"id=?",new String[]{""+id});return id;}
 public void delete(long id){getWritableDatabase().delete("pantry","id=?",new String[]{""+id});}
 public Cursor pantry(){return getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry ORDER BY name",null);}
 public Cursor item(long id){return getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry WHERE id=?",new String[]{""+id});}
 public Cursor recipe(long id){return getReadableDatabase().rawQuery("SELECT name,steps FROM recipes WHERE id=?",new String[]{""+id});}
 public Cursor requirements(long id){return getReadableDatabase().rawQuery("SELECT ingredient,quantity,unit FROM requirements WHERE recipe_id=?",new String[]{""+id});}
 public ArrayList<Long> matches(){ArrayList<Long> out=new ArrayList<>();SQLiteDatabase d=getReadableDatabase();try(Cursor rs=d.rawQuery("SELECT id FROM recipes ORDER BY name",null)){while(rs.moveToNext()){long id=rs.getLong(0);boolean ok=true;try(Cursor req=requirements(id)){while(req.moveToNext()){String ingredient=norm(req.getString(0)),unit=req.getString(2);double needed=convert(req.getDouble(1),unit),available=0;try(Cursor pantry=pantry()){while(pantry.moveToNext()){String pu=pantry.getString(3);if(norm(pantry.getString(1)).equals(ingredient)&&group(pu).equals(group(unit)))available+=convert(pantry.getDouble(2),pu);}}if(needed<0||available+1e-9<needed){ok=false;break;}}}if(ok)out.add(id);}}return out;}
}
