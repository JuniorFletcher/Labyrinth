/*	Junior Fletcher 
 *	Labryinth
 * 		Marble.java
 * 			Defines marble
 */
package edu.JuniorMason.labryinth;// package import

// java and android imports
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

// Begin class Marble
public class Marble
{// View controlling the marble.
	private View mView;

	// marble attributes
	// x,y are private because we need boundary checking on any new values to
	// make sure they are valid.
	private int mX = 0;
	private int mY = 0;
	private int mRadius = 8;
	private int mColor = Color.WHITE;
	private int mLives = 5;

	// Begin method Marble
	public Marble(View view)
	{// initializes marble
		this.mView = view;
		init();
	}// End method Marble

	// Begin method init
	public void init()
	{// Defines ball size
		mX = mRadius * 6;
		mY = mRadius * 6;
	}// End method init

	// Begin method draw
	public void draw(Canvas canvas, Paint paint)
	{//sets color
		paint.setColor(mColor);
		canvas.drawCircle(mX, mY, mRadius, paint);
	}// end method draw

	// Begin method updateX
	public void updateX(float newX)
	{// Updates x value
		mX += newX; // boundary checking, don't want the marble rolling
					// off-screen.
		if (mX + mRadius >= mView.getWidth())
			mX = mView.getWidth() - mRadius;
		else if (mX - mRadius < 0)
			mX = mRadius;
	}// End method updateX

	// Begin method updateY
	public void updateY(float newY)
	{// Updates Y value
		mY -= newY;
		// boundary checking, don't want the marble rolling off-screen.
		if (mY + mRadius >= mView.getHeight())
			mY = mView.getHeight() - mRadius;
		else if (mY - mRadius < 0)
			mY = mRadius;
	}// End method updateY

	// Returns lives and dimension values
	public void death()	{ mLives--;}
	public void setLives(int val){ mLives = val;}
	public int getLives(){	return mLives; }
	public int getX(){	return mX;	}
	public int getY(){	return mY;	}
}// End class Marble
