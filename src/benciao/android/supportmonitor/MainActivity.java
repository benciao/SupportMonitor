package benciao.android.supportmonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ViewSwitcher;
import benciao.android.supportmonitor.http.RequestWrapper;
import benciao.android.supportmonitor.util.SupportMonitorUtil;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Diese Aktivität prüft, ob der Smartphone-Benutzer im mts.support.team.tracker
 * Backend mit seiner Telefonnummer registriert ist. Ist er das, so kann er die
 * App benutzen. Ist er es nicht, muss er sich zunächst registrieren lassen. <br>
 * <br>
 * by Markus Arndt <br>
 * <a href="arndt.markus@googlemail.com">arndt.markus@googlemail.com</a><br>
 * created 01.06.2012
 */
public class MainActivity extends SherlockActivity implements OnClickListener
{
	public static SherlockActivity	activity;
	private ViewSwitcher			viewSwitcher;
	private Bundle					bundle;
	private CheckRegistrationTask	registrationTask;
	private MenuItem				refreshActionItem;
	private Button					buttonRetryRegistration;

	/**
	 * Wird aufgerufen, wenn eine Instanz der Aktivität erzeugt wird.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		activity = this;
		bundle = savedInstanceState;
		setContentView(R.layout.main);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

		buttonRetryRegistration = (Button) findViewById(R.id.buttonRetryRegistration);
		buttonRetryRegistration.setOnClickListener(this);

		checkRegistration();
	}

	/**
	 * Setzt die Aktions-Icons in der ActionBar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		refreshActionItem = menu.add("Aktualisieren");
		refreshActionItem.setIcon(R.drawable.ic_menu_refresh).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	/**
	 * Wird aufgerufen, wenn ein Aktions-Icon der ActionBar gedrückt wird.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item == refreshActionItem)
		{
			this.onCreate(bundle);
		}

		return true;
	}

	/**
	 * Diese Methode erzeugt einen Hintergrundprozess, in welchem geprüft wird,
	 * ob der Smartphone-Benutzer im Backend mit seiner Telefonnummer
	 * registriert ist.
	 */
	public void checkRegistration()
	{
		if (registrationTask == null)
		{
			registrationTask = new CheckRegistrationTask();
			registrationTask.execute();
		}
		else
		{
			registrationTask.cancel(true);
			registrationTask = null;
			registrationTask = new CheckRegistrationTask();
			registrationTask.execute();
		}
	}

	@Override
	public void onClick(View view)
	{
		if (view == buttonRetryRegistration)
		{
			this.onCreate(bundle);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		registrationTask.cancel(true);
		registrationTask = null;
	}

	/**
	 * Stellt eine HTTP-Verbindung zu
	 * http://benciao-support-team-tracker.appspot.com her und prüft, ob die
	 * unter dem Telefonkontakt <code>SUPPORT_TELEPHONE_CONTACT</code>
	 * gespeicherte Nummer im Backend registriert ist. <br>
	 * <br>
	 * by Markus Arndt <br>
	 * <a href="arndt.markus@googlemail.com">arndt.markus@googlemail.com</a><br>
	 * created 01.06.2012
	 */
	class CheckRegistrationTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			return RequestWrapper.isMobilePhoneRegistered(SupportMonitorUtil
					.getRegisteredMobilePhoneNumber(getContentResolver()));
		}

		@Override
		protected void onPostExecute(Boolean isMobilePhoneRegistered)
		{
			if (isMobilePhoneRegistered)
			{
				Intent intent = new Intent(activity, MenuActivity.class);
				startActivity(intent);
			}
			else
			{
				viewSwitcher.showNext();
			}
		}
	}
}