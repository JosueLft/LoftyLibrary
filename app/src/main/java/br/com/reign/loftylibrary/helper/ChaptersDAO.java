package br.com.reign.loftylibrary.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.reign.loftylibrary.model.Chapter;
import br.com.reign.loftylibrary.model.MangaChapter;

public class ChaptersDAO implements IChaptersDao {

    private SQLiteDatabase write;
    private SQLiteDatabase read;
    private Context context;
    private DbHelper db;
    public ChaptersDAO(Context context) {
        db = new DbHelper(context);
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();
        this.context = context;
    }

    @Override
    public boolean read(Chapter chapter) {
        ContentValues cv = new ContentValues();
        cv.put("work_cover", chapter.getCover());
        cv.put("work_name", chapter.getWorkTitle());
        cv.put("chapter_title", chapter.getChapterTitle());
        cv.put("type", chapter.getType());
        cv.put("date", System.currentTimeMillis());

        try {
            write.insert(DbHelper.TABLE_CHAPTERS, null, cv);
            Toast.makeText(
                    context,
                    "Sucesso ao cadastrar o capitulo!",
                    Toast.LENGTH_SHORT
            ).show();
        } catch(Exception e) {
            Toast.makeText(
                    context,
                    "Falha ao marcar o capitulo!",
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Chapter chapter) {
        ContentValues cv = new ContentValues();

        try {
            String[] args = {chapter.getWorkTitle()};
            write.delete(DbHelper.TABLE_CHAPTERS, "work_name = ?", args);
            Log.d("INFO", "Obra " + chapter.getWorkTitle() + " removida com sucesso!");
        } catch (Exception e) {
            Log.e("INFO", "Erro ao remover a obra!\n " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public List<Chapter> chaptersList() {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM " + db.TABLE_CHAPTERS + ";";
        Cursor c = read.rawQuery(sql, null);

        c.moveToFirst();

        while (c.moveToNext()) {
            Chapter chapter = new Chapter();

            String work_cover = c.getString(c.getColumnIndex("work_cover"));
            String work_name = c.getString(c.getColumnIndex("work_name"));
            String chapter_title = c.getString(c.getColumnIndex("chapter_title"));
            String type = c.getString(c.getColumnIndex("type"));
            Long current_date = c.getLong(c.getColumnIndex("date"));

            chapter.setCover(work_cover);
            chapter.setWorkTitle(work_name);
            chapter.setChapterTitle(chapter_title);
            chapter.setDate(current_date);
            chapter.setType(type);

            chapters.add(chapter);
        }
        return chapters;
    }
}