package com.nani.data;

import static com.nani.data.DBSudoku.*;
import static com.nani.data.DBNonogram.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "sus_doku";
    public static final int DATABASE_VERSION = 1;
    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SUDOKU_LIST_NAME + " ( " +
                SUDOKU_ID + " integer primary key," +
                SUDOKU_NAME + " text," +
                SUDOKU_STATE + " integer," +  // sql lite hasn't boolean?
                SUDOKU_DATA + " text" + ");"
        );
        db.execSQL("create table " + NONOGRAM_LIST_NAME + " ( " +
                NONOGRAM_ID + " integer primary key," +
                NONOGRAM_NAME + " text," +
                NONOGRAM_STATE + " integer," +  // sql lite hasn't boolean?
                NONOGRAM_DATA + " text" + ");"
        );
        addSudokuBoard(db, 1, "Almost done!", "009612437723854169164379528986147352375268914241593786432981675617425893598736241");
        addSudokuBoard(db, 2, "Easy start2", "402000007000080420050302006090030050503060708070010060900406030015070000200000809");
        addSudokuBoard(db, 3, "Easy3", "060091080109680405050040106600000200023904710004000003907020030305079602040150070");
        addSudokuBoard(db, 4,  "Easy4", "060090380009080405050300106001008200020904010004200900907006030305070600046050070");
        addSudokuBoard(db, 5,  "Easy5", "402000380109607400008300106090030004023964710800010060907006500005809602046000809");
        addSudokuBoard(db, 6,  "Easy6", "400091000009007425058340190691000000003964700000000963087026530315800600000150009");
        addSudokuBoard(db, 7,  "Easy7", "380001004002600070000487003000040239201000406495060000600854000070006800800700092");
        addSudokuBoard(db,  8, "Medium1", "916004072800620050500008930060000200000207000005000090097800003080076009450100687");
        addSudokuBoard(db,  9, "Medium2", "000900082063001409908000000000670300046050290007023000000000701704300620630007000");
        addSudokuBoard(db,  10, "Medium3", "035670000400829500080003060020005807800206005301700020040900070002487006000052490");
        addSudokuBoard(db,  11, "Medium4", "030070902470009000009003060024000837007000100351000620040900200000400056708050090");
        addSudokuBoard(db,  12, "Medium5", "084200000930840000057000000600401700400070002005602009000000980000028047000003210");
        addSudokuBoard(db,  13, "Medium6", "007861000008003000560090010100070085000345000630010007050020098000600500000537100");
        addSudokuBoard(db,  14, "Hard1", "600300100071620000805001000500870901009000600407069008000200807000086410008003002");
        addSudokuBoard(db,  15, "Hard2", "906013008058000090030000010060800920003409100049006030090000080010000670400960301");
        addSudokuBoard(db,  16, "Hard3", "300060250000500103005210486000380500030000040002045000413052700807004000056070004");
        addSudokuBoard(db,  17, "Hard4", "060001907100007230080000406018002004070040090900100780607000040051600009809300020");
        addSudokuBoard(db,  18, "Hard5", "600300208400185000000000450000070835030508020958010000069000000000631002304009006");
        addSudokuBoard(db, 19, "Hard6", "400030090200001600760800001500318000032000510000592008900003045001700006040020003");
        addSudokuBoard(db,  20, "Hard7", "004090170900070002007204000043000050798406213060000890000709400600040001085030700");

        addNonogramBoard(db, 22, "Example1", "5|1|5|1|5,3:1|1:1:1|1:1:1|1:1:1|1:3", 5, 5);
        addNonogramBoard(db, 21, "Example2", "1|3|5|7|9|11|13|13|15|15|15|6:1:6|4:1:4|3|5,4|7|8|9|10|10:1|10:2|15|10:2|10:1|10|9|8|7|4", 15, 15);
        addNonogramBoard(db, 23, "Amogus", "4:5|1:2:1:3|4:5|2:3|4:5|1:1:1:1:1|2:1:1:1|2:1:1:1|1:1:1:3|4:5,3:6|1:3:1:1|5:1:1|3:6|@|3:6|1:3:1|5:1:2|5:2|3:6", 10, 10);
    }

    private void addSudokuBoard(SQLiteDatabase db, int id, String name, String data) {
        StringBuilder dataV2 = new StringBuilder();
        for (int i = 0; i < data.length(); ++i) {
            dataV2.append("|");
            dataV2.append(data.charAt(i) == '0' ? "+" : "-");
            dataV2.append(data.charAt(i));
        }
        String query = "insert into " + SUDOKU_LIST_NAME + " VALUES (" +
                        id + "," +
                        "'" + name + "', " + //spaces
                        0 + ", " +
                        "'" + dataV2  + "');";
        db.execSQL(query);
    }
    private void addNonogramBoard(SQLiteDatabase db, int id, String name, String description, int rows, int cols) {
        StringBuilder bld = new StringBuilder();
        bld.append("&");
        for (int i = 0; i < rows * cols; ++i)
            bld.append("0|");
        String data = bld.toString();
        String query = "insert into " + NONOGRAM_LIST_NAME + " VALUES (" +
                id + "," +
                "'" + name + "', " + //spaces
                0 + ", " +
                "'" + description + data  + "');";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
