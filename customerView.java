/*	Junior Fletcher
 * 	Labryinth
 * 		customView.java
 * 			This java files implements the game logic
 */
package edu.JuniorMason.labryinth;// Package import

// Java and android imports
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

// Begin class customView
@SuppressWarnings("deprecation")
public class customView extends View
{// Implements game logic
	// Game objects
	private Marble mMarble;
	public Maze mMaze;
	public Activity mActivity;
	public boolean gameover = false;

	// canvas and paints we paint to.
	public Canvas mCanvas;
	private Paint mPaint;
	private Typeface mFont = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
	private int mTextPadding = 10;
	private int mHudTextY = 440;

	// game states
	private final static int NULL_STATE = -1;
	private final static int GAME_INIT = 0;
	private final static int GAME_RUNNING = 1;
	private final static int GAME_OVER = 2;
	private final static int GAME_COMPLETE = 3;
	private final static int GAME_LANDSCAPE = 4;

	// current state of the game
	public static int mCurState = NULL_STATE;

	// game Strings
	private final static int TXT_LIVES = 0;
	private final static int TXT_LEVEL = 1;
	private final static int TXT_TIME = 2;
	private final static int TXT_TAP_SCREEN = 3;
	private final static int TXT_GAME_COMPLETE = 4;
	private final static int TXT_GAME_OVER = 5;
	private final static int TXT_TOTAL_TIME = 6;
	private final static int TXT_GAME_OVER_MSG_A = 7;
	private final static int TXT_GAME_OVER_MSG_B = 8;
	private final static int TXT_RESTART = 9;
	private final static int TXT_LANDSCAPE_MODE = 10;
	private static String mStrings[];
	public boolean customLevelSelected = false;

	public int keyLocation; //holds the location in MazeData of the key
	public boolean keyCollected = false;
	// this prevents the user from dying instantly when they start a level if
	// the device is tilted.
	private boolean mWarning = false;

	// screen dimensions
	private int mCanvasWidth = 0;
	private int mCanvasHeight = 0;
	private int mCanvasHalfWidth = 0;
	private int mCanvasHalfHeight = 0;
	public Paint tempPaint;

	// current level
	public int mlevel = 20;
	private long mTotalTime = 0;
	private long mStartTime = 0;

	// sensor manager used to control the accelerometer sensor.
	private SensorManager mSensorManager;
	// accelerometer sensor values.
	private float mAccelX = 0;
	private float mAccelY = 0;
	private float mAccelZ = 0; // this is never used but just in-case future
	// versions make use of it.

	// accelerometer buffer, currently set to 0 so even the slightest movement
	// will roll the marble.
	private float mSensorBuffer = 0;
	public int CurrentPositionX;
	public int CurrentPositionY;
	public int PreviousPositionX;
	public int PreviousPositionY;

	// Sound variables
	private Map<Integer, Integer> soundMap;
	private SoundPool soundPool;
	private static final int BUZZ_SOUND_ID = 0;
	private static final int EXIT_SOUND1_ID = 1;
	private static final int SLIP_SOUND2_ID = 2;

	// Begin method mSensorAccelerometer
	private final SensorListener mSensorAccelerometer = new SensorListener()
	{
		// Begin method onSensorChanged
		@Override
		public void onSensorChanged(int sensor, float[] values)
		{// method called whenever new sensor values are reported.
			// grab the values required to respond to user movement.
			mAccelX = values[0];
			mAccelY = values[1];
			mAccelZ = values[2];
		}// End method onSensorChanged

		// Begin onAccuracyChanged
		@Override
		public void onAccuracyChanged(int sensor, int accuracy)
		{
			// currently not used
		}// End method onAccuracyChanged
	};// End method mSensorAccelerometer

	// Begin method customView
	public customView(Context context, Activity activity)
	{// Constructor
		super(context);

		mActivity = activity;

		// init paint and make is look "nice" with anti-aliasing.
		mPaint = new Paint();
		mPaint.setTextSize(14);
		mPaint.setTypeface(mFont);
		mPaint.setAntiAlias(true);

		// setup accelerometer sensor manager.
		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		// register our accelerometer so we can receive values.
		// SENSOR_DELAY_GAME is the recommended rate for games
		mSensorManager.registerListener(mSensorAccelerometer, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);

		// setup our maze and marble.
		mMaze = new Maze(mActivity);
		mMarble = new Marble(this);

		// load array from /res/values/Strings.xml
		mStrings = getResources().getStringArray(R.array.gameStrings);

		// set the starting state of the game.
		switchGameState(GAME_INIT);
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<Integer, Integer>(); // create new HashMap
		soundMap.put(BUZZ_SOUND_ID, soundPool.load(mActivity, R.raw.buzzer, 1));
		soundMap.put(EXIT_SOUND1_ID, soundPool.load(mActivity, R.raw.exit, 1));
		soundMap.put(SLIP_SOUND2_ID, soundPool.load(mActivity, R.raw.slip, 1));
	}// End method customView

	// Begin method onSizeChanged
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{// screen size change (scalable
		super.onSizeChanged(w, h, oldw, oldh);

		// get new screen dimensions.
		mCanvasWidth = w;
		mCanvasHeight = h;
		mCanvasHalfWidth = w / 2;
		mCanvasHalfHeight = h / 2;
	}// End method onSizeChanged

	// Begin method gameTick
	public void gameTick()
	{
		// state machine
		switch (mCurState)
		{
		case GAME_INIT:
			// prepare a new game for the user.
			initNewGame();
			switchGameState(GAME_RUNNING);

		case GAME_RUNNING:
			// update our marble.
			if (!mWarning)
				updateMarble();
			break;
		}// end switch
			// redraw the screen once our tick function is complete.
		invalidate();
	}// End method gameTick

	// Begin method initNewGame
	public void initNewGame()
	{// Starts new game and resets lives
		mMarble.setLives(5);
		mTotalTime = 0;
		if (customLevelSelected)
		{
			// Don't do anything
		} else
			mlevel = 0;
		initLevel();
	}// End method initNewGame

	// Begin method initLevel
	public void initLevel()
	{// displays next level
		if (mlevel < Maze.MAX_LEVELS)
		{// setup the next level.
			mWarning = true;
			if (customLevelSelected)
			{
				customLevelSelected = false;
			} else
				mlevel++;
			mMaze.load(mActivity, mlevel);
			mMarble.init();
		} else
		{
			// user has finished the game, update state machine.
			switchGameState(GAME_COMPLETE);
		}// end else
	}// End method initLevel

	// Begin method updateMarble
	public void updateMarble()
	{// Updates marble position
		PreviousPositionY = mMarble.getY();
		PreviousPositionX = mMarble.getX();

		// Updates acceleration
		if (mMaze.getCellType(mMarble.getX(), mMarble.getY()) == Maze.BUMPER_TILE)
		{
			soundPool.play(soundMap.get(SLIP_SOUND2_ID), 1, 1, 1, 0, 1f);
			if (mAccelX > mSensorBuffer || mAccelX < -mSensorBuffer)
				mMarble.updateX(2 * mAccelX);
			if (mAccelY > mSensorBuffer || mAccelY < -mSensorBuffer)
				mMarble.updateY(2 * mAccelY);
		} else if (mMaze.getCellType(mMarble.getX(), mMarble.getY()) == Maze.PATH_TILE && mMaze.getCellType(PreviousPositionX, PreviousPositionY) == Maze.BUMPER_TILE)
		{
			soundPool.play(soundMap.get(SLIP_SOUND2_ID), 1, 1, 1, 0, 1f);
			if (mAccelX > mSensorBuffer || mAccelX < -mSensorBuffer)
				mMarble.updateX(3 / 2 * mAccelX);
			if (mAccelY > mSensorBuffer || mAccelY < -mSensorBuffer)
				mMarble.updateY(3 / 2 * mAccelY);
		} else
		{
			if (mAccelX > mSensorBuffer || mAccelX < -mSensorBuffer)
				mMarble.updateX(mAccelX);
			if (mAccelY > mSensorBuffer || mAccelY < -mSensorBuffer)
				mMarble.updateY(mAccelY);
		}// end elses

		// check which cell the marble is currently occupying.
		if (mMaze.getCellType(mMarble.getX(), mMarble.getY()) == Maze.VOID_TILE)
		{// user entered the "void".
			if (mMarble.getLives() > 0)
			{// user still has some lives remaining, restart the level.
				mMarble.death();
				mMarble.init();
				mWarning = true;
			} else
			{
				switchGameState(GAME_OVER);
			}// end else
				// Hits exit tiles
		} else if (mMaze.getCellType(mMarble.getX(), mMarble.getY()) == Maze.EXIT_TILE)
		{// Checks if key is collected
			if (keyCollected == false && mMaze.mode == 2)
			{
				soundPool.play(soundMap.get(BUZZ_SOUND_ID), 1, 1, 1, 0, 1f);
			} else
			{
				soundPool.play(soundMap.get(EXIT_SOUND1_ID), 1, 1, 1, 0, 1f);
				keyCollected = false;// resets coin count
				initLevel();
			}
		} else if (mMaze.getCellType(mMarble.getX(), mMarble.getY()) == Maze.key_TILE)
		{
			mMaze.drawkeyCollected();
			keyCollected = true;
		}// end if
	}// End method updateMarble

	// Begin method onTouchEvent
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// we only want to handle down events
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (mCurState == GAME_OVER || mCurState == GAME_COMPLETE)
			{
				// re-start the game.
				mCurState = GAME_INIT;
			} else if (mCurState == GAME_RUNNING)
			{
				// in-game, remove the pop-up text so user can play.
				mWarning = false;
				mStartTime = System.currentTimeMillis();
			}// end if
		}// end if
		return true;
	}// end method onTouchEvent

	// Begin method onKeyDown
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{// quit application if user presses the back key.
		if (keyCode == KeyEvent.KEYCODE_BACK)
			cleanUp();
		return true;
	}// End method onKeyDown

	// Begin method onDraw
	@Override
	public void onDraw(Canvas canvas)
	{// update our canvas reference.
		mCanvas = canvas;

		// clear the screen.
		mPaint.setColor(Color.WHITE);
		mCanvas.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mPaint);

		// simple state machine, draw screen depending on the current state.
		switch (mCurState)
		{
		case GAME_RUNNING:
			// draw our maze first since everything else appears "on top" of it.
			mMaze.draw(mCanvas, mPaint);

			// draw our marble and hud.
			mMarble.draw(mCanvas, mPaint);

			// draw hud
			drawHUD();
			break;
			// gameover
		case GAME_OVER:
			gameover = true;
			drawGameOver();
			break;
			// game complete
		case GAME_COMPLETE:
			gameover = true;
			drawGameComplete();
			break;
		}// end swiitch
		gameTick();
	}// end method onDraw

	// Begin method drawHUD
	public void drawHUD()
	{// Draws score and level information
		mPaint.setColor(Color.BLACK);
		mPaint.setTextAlign(Paint.Align.LEFT);
		mCanvas.drawText(mStrings[TXT_TIME] + ": " + (mTotalTime / 1000), mTextPadding, mHudTextY, mPaint);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mCanvas.drawText(mStrings[TXT_LEVEL] + ": " + mlevel, mCanvasHalfWidth, mHudTextY, mPaint);
		mPaint.setTextAlign(Paint.Align.RIGHT);
		mCanvas.drawText(mStrings[TXT_LIVES] + ": " + mMarble.getLives(), mCanvasWidth - mTextPadding, mHudTextY, mPaint);

		// do we need to display the warning message to save the user from
		// possibly dying instantly.
		if (mWarning)
		{
			mPaint.setColor(Color.BLUE);
			mCanvas.drawRect(0, mCanvasHalfHeight - 15, mCanvasWidth, mCanvasHalfHeight + 5, mPaint);
			mPaint.setColor(Color.WHITE);
			mPaint.setTextAlign(Paint.Align.CENTER);
			mCanvas.drawText(mStrings[TXT_TAP_SCREEN], mCanvasHalfWidth, mCanvasHalfHeight, mPaint);
		}// end if
	}// end method drawHUD

	// Begin method drawGameOver
	public void drawGameOver()
	{// draws loser info
		mPaint.setColor(Color.BLACK);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mCanvas.drawText(mStrings[TXT_GAME_OVER], mCanvasHalfWidth, mCanvasHalfHeight, mPaint);
		mCanvas.drawText(mStrings[TXT_TOTAL_TIME] + ": " + (mTotalTime / 1000) + "s", mCanvasHalfWidth, mCanvasHalfHeight + mPaint.getFontSpacing(), mPaint);
		mCanvas.drawText(mStrings[TXT_GAME_OVER_MSG_A] + " " + (mlevel - 1) + " " + mStrings[TXT_GAME_OVER_MSG_B], mCanvasHalfWidth, mCanvasHalfHeight + (mPaint.getFontSpacing() * 2), mPaint);
		mCanvas.drawText(mStrings[TXT_RESTART], mCanvasHalfWidth, mCanvasHeight - (mPaint.getFontSpacing() * 3), mPaint);
	}// End method drawGameOver

	// Begin method drawGameComplete
	public void drawGameComplete()
	{// draws completion info
		mPaint.setColor(Color.BLACK);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mCanvas.drawText(mStrings[GAME_COMPLETE], mCanvasHalfWidth, mCanvasHalfHeight, mPaint);
		mCanvas.drawText(mStrings[TXT_TOTAL_TIME] + ": " + (mTotalTime / 1000) + "s", mCanvasHalfWidth, mCanvasHalfHeight + mPaint.getFontSpacing(), mPaint);
		mCanvas.drawText(mStrings[TXT_RESTART], mCanvasHalfWidth, mCanvasHeight - (mPaint.getFontSpacing() * 3), mPaint);
	}// end method drawGameComplete

	// Begin method switchGameState
	public void switchGameState(int newState)
	{// Switches game state
		mCurState = newState;
	}// end method switchGameState

	// Begin method registerListener
	public void registerListener()
	{// registers accelerometer
		mSensorManager.registerListener(mSensorAccelerometer, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
	}// end method registerListener

	// Begin method unregisterListener
	public void unregisterListener()
	{// unregisters listener
		mSensorManager.unregisterListener(mSensorAccelerometer);
	}// End method unregisterListener

	// Begin method cleanUp
	public void cleanUp()
	{// flushs resources
		mMarble = null;
		mMaze = null;
		mStrings = null;
		unregisterListener();
		mActivity.finish();
	}// end method cleanUp
}// End class customView
