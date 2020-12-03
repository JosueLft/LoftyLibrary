package br.com.reign.loftylibrary.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public static String NOME_DB = "DB_CHAPTERSREAD";
    public static String TABLE_CHAPTERS = "chaptersRead";
    private Context context;

    public DbHelper(Context context) {
        super(context, NOME_DB, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CHAPTERS
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + " work_cover TEXT, "
                    + " work_name TEXT, "
                    + " chapter_title TEXT, "
                    + " date LONG, "
                    + " type TEXT);";

        try {
            db.execSQL(sql);
            Toast.makeText(
                    context,
                    "Sucesso!",
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    "Erro!",
                    Toast.LENGTH_SHORT
            ).show();
            Log.d("Erro!", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_CHAPTERS + " ;";

        try {
            db.execSQL(sql);
            onCreate(db);
            Toast.makeText(
                    context,
                    "Sucesso ao atualizar!",
                    Toast.LENGTH_SHORT
            ).show();
        } catch (Exception e) {
            Toast.makeText(
                    context,
                    "Falha ao atualizar!",
                    Toast.LENGTH_SHORT
            ).show();
            Log.d("Erro!", e.getMessage());
        }
    }
}
