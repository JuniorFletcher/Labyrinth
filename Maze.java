/*	Junior Fletcher 
 *	Labryinth
 * 		Maze.java
 * 			This java file draws game levels and changes accordingly
 */
package edu.JuniorMason.labryinth;// Package import

// Java and android imports
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

// Begin class Maze
public class Maze
{//Varibles
	public Maze activity;
	public int mode = 0;
	private String mLevel;

	// maze tile size and dimension
	private final static int TILE_SIZE = 16;
	private final static int MAZE_COLS = 20;
	private final static int MAZE_ROWS = 26;

	// tile types
	public final static int PATH_TILE = 0;
	public final static int VOID_TILE = 1;
	public final static int EXIT_TILE = 2;
	public final static int BUMPER_TILE = 3;
	public final static int key_TILE = 4;

	// tile colors
	private final static int VOID_COLOR = Color.BLACK;
	private final static int BUMPER_COLOR = Color.RED;

	// maze level data
	private static int[] mMazeData;
	public int keyLocation; //holds the location in MazeData of the key
	public Paint paintTemp;

	// number of level
	public final static int MAX_LEVELS = 10;

	// current tile attributes
	private Rect mRect = new Rect();
	private int mRow;
	private int mCol;
	public int mX = 0;
	public int mY = 0;

	// tile bitmaps
	public Bitmap mImgPath;
	private Bitmap mImgExit;
	private Bitmap mImgBumper;
	private Bitmap mImgPathCustom1;
	private Bitmap mImgExitCustom1;
	private Bitmap mImgBumperCustom1;
	private Bitmap mImgPathCustom2;
	private Bitmap mImgExitCustom2;
	private Bitmap mImgBumperCustom2;
	private Bitmap key;
	private Bitmap keyFire;
	private Bitmap keyBlue;
	
	//Drawabale resources
	public int customPath1 = R.drawable.pathhellfire;
	public int customExit1 = R.drawable.exit;
	public int customBumper1 = R.drawable.bumperhellfire;
	public int customPath2 = R.drawable.pathoceanblue;
	public int customExit2 = R.drawable.exitoceanblue;
	public int customBumper2 = R.drawable.bumperoceanblue;
	public int keypic = R.drawable.key;

	//If user selects custom theme
	public int customThemeSelected;

	//View and canvas variables
	public customView mView;
	public Canvas canvasTemp;
	
	//Sounds variables
	private Map<Integer, Integer> soundMap;
	private SoundPool soundPool;
	private static final int BUZZ_SOUND_ID = 0;
	private static final int EXIT_SOUND1_ID = 1;
	private static final int SLIP_SOUND2_ID= 2;

	// Begin Constructor
	Maze(Activity activity)
	{
		// Bitmap instances
		mImgPath = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.path);
		mImgExit = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.exit);
		mImgBumper = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.bumper);
		mImgPathCustom1 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customPath1);
		mImgExitCustom1 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customExit1);
		mImgBumperCustom1 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customBumper1);
		mImgPathCustom2 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customPath2);
		mImgExitCustom2 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customExit2);
		mImgBumperCustom2 = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), customBumper2);
		key = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), keypic);
		keyFire = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.keyfire);
		keyBlue = BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), R.drawable.keyblue);

		// Sound instances
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<Integer, Integer>(); // create new HashMap
		soundMap.put(BUZZ_SOUND_ID, soundPool.load(activity, R.raw.buzzer, 1));
		soundMap.put(EXIT_SOUND1_ID, soundPool.load(activity, R.raw.exit, 1));
		soundMap.put(SLIP_SOUND2_ID, soundPool.load(activity, R.raw.slip, 1));
	}// End constructor

	// Begin load method
	void load(Activity activity, int newLevel)
	{// Draws game board and displays current level and theme
		// maze data is stored in the assets folder as level1.txt, level2.txt
		// etc....
		// if check for diff, and concat dir path
		if (mode == 0)
		{
			mLevel = "N00b/level" + newLevel + ".txt";
		} else if (mode == 1)
		{
			mLevel = "Seasoned/level" + newLevel + ".txt";
		} else if (mode == 2)
		{
			mLevel = "Veteran/level" + newLevel + ".txt";
		}
		InputStream is = null;

		// Try/Catch block
		try
		{
			// construct our maze data array.
			mMazeData = new int[MAZE_ROWS * MAZE_COLS];
			// attempt to load maze data.
			is = activity.getAssets().open(mLevel);

			// we need to loop through the input stream and load each tile for
			// the current maze.
			for (int i = 0; i < mMazeData.length; i++)
			{
				// data is stored in unicode so we need to convert it.
				mMazeData[i] = Character.getNumericValue(is.read());

				// skip the "," and white space in our human readable file.
				is.read();
				is.read();
			}// end for
		} catch (Exception e)
		{
			Log.i("Maze", "load exception: " + e);
		} finally
		{
			closeStream(is);
		}// end finally
	}// End method load

	// Begin method drawkeyCollected
	public void drawkeyCollected(){
		mMazeData[keyLocation] = PATH_TILE;// turns key into pasth tile
	}// end method drawkeyCollected
	
	// Begin method draw
	public void draw(Canvas canvas, Paint paint)
	{
		// canvas and paint temps
		canvasTemp = canvas;
		paintTemp = paint;
		// loop through our maze and draw each tile individually.
		for (int i = 0; i < mMazeData.length; i++)
		{
			// calculate the row and column of the current tile.
			mRow = i / MAZE_COLS;
			mCol = i % MAZE_COLS;

			// convert the row and column into actual x,y co-ordinates so we can
			// draw it on screen.
			mX = mCol * TILE_SIZE;
			mY = mRow * TILE_SIZE;

			// draw the actual tile based on type.
			if (customThemeSelected == 1)
			{
				if (mMazeData[i] == PATH_TILE)
					canvas.drawBitmap(mImgPathCustom1, mX, mY, paint);
				else if (mMazeData[i] == EXIT_TILE)
					canvas.drawBitmap(mImgExitCustom1, mX, mY, paint);
				else if (mMazeData[i] == VOID_TILE)
				{
					mRect.left = mX;
					mRect.top = mY;
					mRect.right = mX + TILE_SIZE;
					mRect.bottom = mY + TILE_SIZE;

					paint.setColor(0xFF7D1414);
					canvas.drawRect(mRect, paint);
				} else if (mMazeData[i] == BUMPER_TILE)
				{
					canvas.drawBitmap(mImgBumperCustom1, mX, mY, paint);
				} else if (mMazeData[i] == key_TILE)
				{
					keyLocation = i; //store the location of the key
					canvas.drawBitmap(keyFire, mX, mY, paint);
				}// end if
			} else if (customThemeSelected == 2)
			{
				if (mMazeData[i] == PATH_TILE)
					canvas.drawBitmap(mImgPathCustom2, mX, mY, paint);
				else if (mMazeData[i] == EXIT_TILE)
					canvas.drawBitmap(mImgExitCustom2, mX, mY, paint);
				else if (mMazeData[i] == VOID_TILE)
				{
					mRect.left = mX;
					mRect.top = mY;
					mRect.right = mX + TILE_SIZE;
					mRect.bottom = mY + TILE_SIZE;

					paint.setColor(0xFF1E213F);
					canvas.drawRect(mRect, paint);
				} else if (mMazeData[i] == BUMPER_TILE)
				{
					paint.setColor(BUMPER_COLOR);
					canvas.drawBitmap(mImgBumperCustom2, mX, mY, paint);
				} else if (mMazeData[i] == key_TILE)
				{
					keyLocation = i; //store the location of the key
					canvas.drawBitmap(keyBlue, mX, mY, paint);
				}//end if
			} else
			{
				if (mMazeData[i] == PATH_TILE)
					canvas.drawBitmap(mImgPath, mX, mY, paint);
				else if (mMazeData[i] == EXIT_TILE)
					canvas.drawBitmap(mImgExit, mX, mY, paint);
				else if (mMazeData[i] == VOID_TILE)
				{
					mRect.left = mX;
					mRect.top = mY;
					mRect.right = mX + TILE_SIZE;
					mRect.bottom = mY + TILE_SIZE;

					paint.setColor(VOID_COLOR);
					canvas.drawRect(mRect, paint);
				} else if (mMazeData[i] == BUMPER_TILE)
				{
					paint.setColor(BUMPER_COLOR);
					canvas.drawBitmap(mImgBumper, mX, mY, paint);
				} else if (mMazeData[i] == key_TILE)
				{
					keyLocation = i; //store the location of the key
					canvas.drawBitmap(key, mX, mY, paint);
				}// end if 
			}// end
		}// end for
	}// End method draw

	// Begin method getCellType
	public int getCellType(int x, int y)
	{// converts text file to gameboard
		// convert the x,y co-ordinate into row and col values.
		int mCellCol = x / TILE_SIZE;
		int mCellRow = y / TILE_SIZE;

		// location is the row,col coordinate converted so we know where in the
		// maze array to look.
		int mLocation = 0;

		// if we are beyond the 1st row need to multiple by the number of
		// columns.
		if (mCellRow > 0)
			mLocation = mCellRow * MAZE_COLS;

		// add the column location.
		mLocation += mCellCol;

		return mMazeData[mLocation];
	}// end method getCellType

	// Begin method closeStream
	private static void closeStream(Closeable stream)
	{// Closes input stream
		if (stream != null)
		{
			try
			{
				stream.close();
			} catch (IOException e)
			{
				// Ignore
			}// end catch
		}// end if
	}// end method closeStream
}// end class maze
