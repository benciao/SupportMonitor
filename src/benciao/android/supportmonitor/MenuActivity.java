package benciao.android.supportmonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Diese Aktivität bietet dem Nutzer ein Menü, über welches er Zugang zu den
 * Funktionen der App bekommt. <br>
 * <br>
 * by Markus Arndt <br>
 * <a href="arndt.markus@googlemail.com">arndt.markus@googlemail.com</a><br>
 * created 01.06.2012
 */
public class MenuActivity extends SherlockActivity implements OnClickListener
{
	public static SherlockActivity	activity;
	private MenuItem				closeActionItem;
	private ImageView				imageViewStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		activity = this;

		imageViewStatus = (ImageView) findViewById(R.id.imageViewStatus);
		imageViewStatus.setOnClickListener(this);
	}

	/**
	 * Setzt die Aktions-Icons in der ActionBar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		closeActionItem = menu.add("Beenden");
		closeActionItem.setIcon(R.drawable.ic_menu_back);
		closeActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	/**
	 * Wird aufgerufen, wenn ein Aktions-Icon der ActionBar gedrückt wird.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item == closeActionItem)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage(R.string.exit_app_question)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									MainActivity.activity.finish();
									MenuActivity.activity.finish();
								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

		return true;
	}

	@Override
	public void onClick(View view)
	{
		if (view == imageViewStatus)
		{
			imageViewStatus.performHapticFeedback(
					HapticFeedbackConstants.LONG_PRESS,
					HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			Intent intent = new Intent(activity, StatusActivity.class);
			startActivity(intent);
		}
	}
}
