/* 
 * Copyright (C) 2009 Roman Masek
 * 
 * This file is part of OpenSudoku.
 * 
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.moire.opensudoku.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

/**
 * Wrapper around opensudoku's database.
 * <p/>
 * You have to pass application context when creating instance:
 * <code>SudokuDatabase db = new SudokuDatabase(getApplicationContext());</code>
 * <p/>
 * You have to explicitly close connection when you're done with database (see {@link #close()}).
 * <p/>
 * @author romario
 */
public class SudokuDatabase {
	public static final String DATABASE_NAME = "opensudoku";
	public static final String SUDOKU_TABLE_NAME = "sudoku";
	public static final String FOLDER_TABLE_NAME = "folder";

	private DatabaseHelper mOpenHelper;

	public SudokuDatabase(Context context) {
		mOpenHelper = new DatabaseHelper(context);
	}

	/**
	 * Returns sudoku game object.
	 *
	 * @param sudokuID Primary key of folder.
	 * @return
	 */
	public SudokuGame getSudoku(long sudokuID) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(SUDOKU_TABLE_NAME);
		qb.appendWhere(SudokuColumns._ID + "=" + sudokuID);

		// Get the database and run the query

		SQLiteDatabase db = null;
		Cursor c = null;
		SudokuGame s = null;
		try {
			db = mOpenHelper.getReadableDatabase();
			c = qb.query(db, null, null, null, null, null, null);

			if (c.moveToFirst()) {
				long id = c.getLong(c.getColumnIndex(SudokuColumns._ID));
				long created = c.getLong(c.getColumnIndex(SudokuColumns.CREATED));
				String data = c.getString(c.getColumnIndex(SudokuColumns.DATA));
				long lastPlayed = c.getLong(c.getColumnIndex(SudokuColumns.LAST_PLAYED));
				int state = c.getInt(c.getColumnIndex(SudokuColumns.STATE));
				long time = c.getLong(c.getColumnIndex(SudokuColumns.TIME));
				String note = c.getString(c.getColumnIndex(SudokuColumns.PUZZLE_NOTE));

				s = new SudokuGame();
				s.setId(id);
				s.setCreated(created);
				s.setCells(CellCollection.deserialize(data));
				s.setLastPlayed(lastPlayed);
				s.setState(state);
				s.setTime(time);
				s.setNote(note);
			}
		} finally {
			if (c != null) c.close();
		}

		return s;

	}

	/**
	 * Updates sudoku game in the database.
	 *
	 * @param sudoku
	 */
	public void updateSudoku(SudokuGame sudoku) {
		ContentValues values = new ContentValues();
		values.put(SudokuColumns.DATA, sudoku.getCells().serialize());
		values.put(SudokuColumns.LAST_PLAYED, sudoku.getLastPlayed());
		values.put(SudokuColumns.STATE, sudoku.getState());
		values.put(SudokuColumns.TIME, sudoku.getTime());
		values.put(SudokuColumns.PUZZLE_NOTE, sudoku.getNote());

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.update(SUDOKU_TABLE_NAME, values, SudokuColumns._ID + "=" + sudoku.getId(), null);
	}

	public void close() {
		mOpenHelper.close();
	}
}
