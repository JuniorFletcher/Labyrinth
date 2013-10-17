/*	Junior Fletcher 
 * 	Labryinth
 * 		MainActivity.java
 * 			This java files implements the basic GUI and menu components
 */
package edu.JuniorMason.labryinth; // Package import

// Android and Java imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;

// Begin constructor
public class MainActivity extends Activity
{// Initializes other classes variables and oncreate method
	private static final String TAG = MainActivity.class.getSimpleName();
	private customView mView;
	private Maze mMaze;

	// Menu Options
	private final int DIFFICULTY_MENU_ID = Menu.FIRST;
	private final int THEME_MENU_ID = Menu.FIRST + 1;
	private final int LEVELSELECT_MENU_ID = Menu.FIRST + 2;
	public String currentDifficulty = "Easy";
	public String currentTheme = "Default"; // Default theme

	public int levelSelect = 20; // limiter
	private GestureDetector gestureDetector; // Used for touch event

	// Begin onCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{//Super class call
		super.onCreate(savedInstanceState);

		// remove title bar.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// setup our view, give it focus and display.
		mView = new customView(getApplicationContext(), this);
		mView.setFocusable(true);
		setContentView(mView);

		//Checks for level selection 
		if (mView.mlevel == 20)
		{
			mView.mlevel = 1;
		} else if (mView.customLevelSelected == true)
		{
			mView.mlevel = levelSelect;
		}// end if
	}// End onCreate

	// Begin onCreateOptionsMenu
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{// call the base class method
		super.onCreateOptionsMenu(menu);

		// Adds menu options
		menu.add(Menu.NONE, DIFFICULTY_MENU_ID, Menu.NONE, getResources().getString(R.string.difficult_menu));
		menu.add(Menu.NONE, THEME_MENU_ID, Menu.NONE, R.string.theme_menu);
		menu.add(Menu.NONE, LEVELSELECT_MENU_ID, Menu.NONE, R.string.levelselect_menu);
		return true;
	}// End onCreateOptionsMenu

	// Begin method onOptionsItemSelected
	public boolean onOptionsItemSelected(MenuItem item)
	{// Implements menu options
		final Context context = this;

		switch (item.getItemId())
		{
		// Inflates difficulty options
		case DIFFICULTY_MENU_ID:
			final String[] possiblechoices1 = getResources().getStringArray(R.array.difficultyStrings);
			// adding the choices to our AlertDialog
			AlertDialog.Builder DifficultychoicesBuilder = new AlertDialog.Builder(this);
			DifficultychoicesBuilder.setTitle(R.string.difficult_menu);
			DifficultychoicesBuilder.setItems(R.array.difficultyStrings, new DialogInterface.OnClickListener()
			{
				@Override
				// Decides difficulty
				public void onClick(DialogInterface dialog, int item)
				{//Handles option selection click
					if (item == 0)
					{
						currentDifficulty = possiblechoices1[0];
						DisplayDifficulty(item);
					} else if (item == 1)
					{
						currentDifficulty = possiblechoices1[1];
						DisplayDifficulty(item);
					} else if (item == 2)
					{
						currentDifficulty = possiblechoices1[2];
						DisplayDifficulty(item);
					} else
						currentDifficulty = possiblechoices1[0];

					//Logging
					Log.d(TAG, "Difficulty chosen is " + currentDifficulty);
				}// end onClick
			});// end listener

			// Create and show dialog
			AlertDialog DifficultychoicesDialog = DifficultychoicesBuilder.create();
			DifficultychoicesDialog.show();

			return true;

		// Chooses custom theme
		case THEME_MENU_ID:
			final String[] possiblechoices2 = getResources().getStringArray(R.array.themeStrings);

			AlertDialog.Builder ThemechoicesBuilder = new AlertDialog.Builder(this);
			ThemechoicesBuilder.setTitle(R.string.theme_menu);
			// adding the choices to our AlertDialog

			ThemechoicesBuilder.setItems(R.array.themeStrings, new DialogInterface.OnClickListener()
			{
				@Override
				// Decides current theme
				public void onClick(DialogInterface dialog, int item)
				{
					if (item == 1)
					{
						currentTheme = possiblechoices2[1];
						DisplayCustomTheme(item);
					} else if (item == 2)
					{
						currentTheme = possiblechoices2[2];
						DisplayCustomTheme(item);
					} else
					{
						currentTheme = possiblechoices2[0];
						DisplayCustomTheme(0);
					}// end else
					Log.d(TAG, "Theme chosen is " + currentTheme);
				}// end onClick
			});// end listener

			// Create and show dialog
			AlertDialog ThemechoicesDialog = ThemechoicesBuilder.create();
			ThemechoicesDialog.show();

			return true;

		// Allows user to select custom level
		case LEVELSELECT_MENU_ID:
			AlertDialog.Builder LevelchoicesBuilder = new AlertDialog.Builder(this);
			LevelchoicesBuilder.setTitle(R.string.levelselect_menu);
			// adding the choices to our AlertDialog

			LevelchoicesBuilder.setItems(R.array.levelStrings, new DialogInterface.OnClickListener()
			{
				@Override
				// Decides current level
				public void onClick(DialogInterface dialog, int item)
				{
					if (!mView.gameover)
					{
						if (item == 0)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 1)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 2)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 3)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 4)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 5)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 6)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 7)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 8)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else if (item == 9)
						{
							levelSelect = (item + 1);
							DisplayCustomLevel();
						} else
							levelSelect = 1;

						Log.d(TAG, "Theme chosen is " + currentTheme);
					}// end onClick
				}
			});// end listener
				// Create and show dialog
			AlertDialog LevelchoicesDialog = LevelchoicesBuilder.create();
			LevelchoicesDialog.show();

			return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}// End method onCreateOptionsMenu

	// Begin method DisplayCustomLevel
	public void DisplayCustomLevel()
	{// resets perspective to custom level
		mView.mlevel = levelSelect;
		mView.customLevelSelected = true;
		mView.mCurState = 0;
	}// End method DisplayCustomLevel

	// Begin method DisplayCustomTheme
	public void DisplayCustomTheme(int i)
	{// resets perspective to custom theme
		mView.mlevel = levelSelect;
		mView.mMaze.customThemeSelected = i;
		mView.mCurState = 0;
	}// End method DisplayCustomTheme

	// Begin method DisplayDifficulty
	public void DisplayDifficulty(int i)
	{// resets perspective to custom difficulty
		mView.mMaze.mode = i;
		mView.mCurState = 0;
	}// End method DisplayDifficulty

	// Begin method onResume
	@Override
	protected void onResume()
	{
		super.onResume();
		mView.registerListener();
	}// End method onResume

	// Begin method onSaveInstanceState
	@Override
	public void onSaveInstanceState(Bundle icicle)
	{
		super.onSaveInstanceState(icicle);
		mView.unregisterListener();
	}// End method onSaveInstanceState
}// End Class
