package benciao.android.supportmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import benciao.android.supportmonitor.domain.DomainDataWrapper;
import benciao.android.supportmonitor.domain.Group;
import benciao.android.supportmonitor.domain.User;
import benciao.android.supportmonitor.domain.User.SupportLevel;
import benciao.android.supportmonitor.domain.User.SupportStatus;
import benciao.android.supportmonitor.http.RequestWrapper;
import benciao.android.supportmonitor.util.SupportMonitorUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * <br>
 * <br>
 * by Markus Arndt <br>
 * <a href="arndt.markus@googlemail.com">arndt.markus@googlemail.com</a><br>
 * created 01.06.2012
 */
public class StatusActivity extends SherlockListActivity implements
		ActionBar.OnNavigationListener
{
	public static SherlockListActivity	activity;
	private ViewSwitcher				viewSwitcher;
	private EfficientAdapter			userListAdapter;
	private MenuItem					refreshActionItem;
	private MenuItem					statusChangeActionItem;
	private MenuItem					supportLevelSwitchActionItem;
	private static List<User>			listData;
	private Bundle						bundle;
	private ArrayAdapter<CharSequence>	navigationList;
	private DomainDataWrapper			requestedStatusObject;
	private StatusRequestTask			statusRequestTask;
	private StatusChangeRequestTask		statusChangeRequestTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		activity = this;
		bundle = savedInstanceState;

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(navigationList, this);

		statusRequestTask = new StatusRequestTask();
		statusRequestTask.execute();
		
		Context context = getSupportActionBar().getThemedContext();
		navigationList = new ArrayAdapter<CharSequence>(context,
				R.layout.sherlock_spinner_item);
		navigationList
				.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		navigationList.add("Status aller Benutzer");
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

		statusChangeActionItem = menu.add("Status ändern");
		statusChangeActionItem.setIcon(R.drawable.ic_menu_status_change)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		if (listData != null && !listData.isEmpty())
		{
			User currentUser = SupportMonitorUtil.getCurrentUser(listData,
					getContentResolver());

			if (currentUser.getLevel() == SupportLevel.FIRST_LEVEL
					|| currentUser.getLevel() == SupportLevel.SECOND_LEVEL)
			{
				supportLevelSwitchActionItem = menu
						.add("Bereitschaft tauschen");
				supportLevelSwitchActionItem.setIcon(
						R.drawable.ic_menu_switch_support_level)
						.setShowAsAction(
								MenuItem.SHOW_AS_ACTION_ALWAYS
										| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			}
		}

		return true;
	}

	/**
	 * Wird aufgerufen, wenn ein Aktions-Icon der ActionBar gedrückt wird.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			Intent intent = new Intent(this, MenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		if (item == refreshActionItem)
		{
			this.onCreate(bundle);
		}
		if (item == statusChangeActionItem)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(R.string.statusChangeHeader);
			builder.setSingleChoiceItems(
					SupportStatus.getSupportStatusDescriptions(), -1,
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int item)
						{
							String userId = SupportMonitorUtil
									.getRegisteredMobilePhoneNumber(getContentResolver());

							statusChangeRequestTask = new StatusChangeRequestTask();
							statusChangeRequestTask.execute(
									userId,
									SupportStatus
											.getSupportStatusDescriptions()[item]
											.toString());

							dialog.cancel();
						}
					});

			AlertDialog statusChangeDialog = builder.create();
			statusChangeDialog.show();
		}

		return true;
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
	class StatusRequestTask extends AsyncTask<Void, Void, DomainDataWrapper>
	{
		@Override
		protected DomainDataWrapper doInBackground(Void... params)
		{
			DomainDataWrapper statusObject = null;
			try
			{
				statusObject = RequestWrapper.getStatus();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return statusObject;
		}

		@Override
		protected void onPostExecute(DomainDataWrapper statusObject)
		{
			requestedStatusObject = statusObject;
			viewSwitcher.showNext();

			for (Group group : statusObject.getGroupList())
			{
				navigationList.add("Status " + group.getName());
			}

			listData = statusObject.getUserList();

			userListAdapter = new EfficientAdapter(activity);
			setListAdapter(userListAdapter);

			activity.invalidateOptionsMenu();
		}
	}

	class StatusChangeRequestTask extends AsyncTask<String, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... params)
		{
			Boolean statusChangeResult = false;
			try
			{

				statusChangeResult = RequestWrapper.changeStatus(params[0],
						params[1]);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return statusChangeResult;
		}

		@Override
		protected void onPostExecute(Boolean statusChangeResult)
		{
			if (statusChangeResult)
			{
				((StatusActivity) activity).onCreate(bundle);
			}
		}
	}

	public static class EfficientAdapter extends BaseAdapter implements
			Filterable
	{
		private LayoutInflater	mInflater;
		private Context			context;

		public EfficientAdapter(Context context)
		{
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent)
		{
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.adapter_content, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.userName = (TextView) convertView
						.findViewById(R.id.textViewName);
				holder.status = (TextView) convertView
						.findViewById(R.id.textViewStatus);
				holder.level = (TextView) convertView
						.findViewById(R.id.textViewLevel);
				holder.statusColor = (FrameLayout) convertView
						.findViewById(R.id.layoutStatusColor);
				holder.updateTime = (TextView) convertView
						.findViewById(R.id.textViewUpdateTime);
				holder.updateDate = (TextView) convertView
						.findViewById(R.id.textViewUpdateDate);
				holder.entry = (LinearLayout) convertView
						.findViewById(R.id.entry);

				convertView.setOnClickListener(new OnClickListener()
				{
					private int	pos	= position;

					@Override
					public void onClick(View v)
					{
						Toast.makeText(context, "Click-" + String.valueOf(pos),
								Toast.LENGTH_SHORT).show();
					}
				});

				convertView.setTag(holder);
			}
			else
			{
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			holder.userName.setText(((User) getItem(position)).getFirstName()
					+ " " + ((User) getItem(position)).getLastName());
			holder.status.setText(((User) getItem(position)).getStatus()
					.getDescription());
			holder.statusColor.setBackgroundColor(((User) getItem(position))
					.getStatus().getColor());

			Date levelChange = ((User) getItem(position)).getLevelChange();
			Date statusChange = ((User) getItem(position)).getStatusChange();

			if (((User) getItem(position)).getMobilePhone() == SupportMonitorUtil
					.getCurrentUser(listData, activity.getContentResolver())
					.getMobilePhone())
			{
				holder.entry.setBackgroundColor(0xffb8a399);
			}

			String updateTextTime = "";
			String updateTextDate = "";
			if (levelChange.after(statusChange))
			{
				updateTextTime = new SimpleDateFormat("HH:mm")
						.format(levelChange);
				updateTextDate = new SimpleDateFormat("dd.MM.yyyy")
						.format(levelChange);
			}
			else
			{
				updateTextTime = new SimpleDateFormat("HH:mm")
						.format(statusChange);
				updateTextDate = new SimpleDateFormat("dd.MM.yyyy")
						.format(statusChange);
			}

			holder.updateTime.setText(updateTextTime);
			holder.updateDate.setText(updateTextDate);

			switch (((User) getItem(position)).getLevel())
			{
			case FIRST_LEVEL:
				holder.level.setText(((User) getItem(position)).getLevel()
						.getDescription());
				holder.level.setVisibility(View.VISIBLE);
				break;
			case SECOND_LEVEL:
				holder.level.setText(((User) getItem(position)).getLevel()
						.getDescription());
				holder.level.setVisibility(View.VISIBLE);
				break;
			default:
				holder.level.setText("");
				holder.level.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		static class ViewHolder
		{
			TextView		userName;
			TextView		status;
			TextView		level;
			TextView		updateTime;
			TextView		updateDate;
			FrameLayout		statusColor;
			LinearLayout	entry;
		}

		@Override
		public Filter getFilter()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public int getCount()
		{
			return listData.size();
		}

		@Override
		public Object getItem(int position)
		{
			return listData.get(position);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		String selectedItem = (String) navigationList.getItem(itemPosition);

		selectedItem = selectedItem.substring(7);

		List<User> filteredUserList = requestedStatusObject
				.getGroupMembersByGroupName(selectedItem);

		listData = filteredUserList;

		userListAdapter.notifyDataSetChanged();
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		statusChangeRequestTask.cancel(true);
		statusChangeRequestTask = null;
		statusRequestTask.cancel(true);
		statusRequestTask = null;
	}
}
