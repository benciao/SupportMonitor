package benciao.android.supportmonitor.domain;

import java.util.Date;

public class User
{
	private Long	mobilePhone;
	private String	firstName;
	private String	lastName;
	private String	email;
	private int		status;
	private int		level;
	private Date	statusChange;
	private Date	levelChange;
	private Long	secondMobilePhone;
	private Long	landlinePhone;
	private Long	groupId;

	public User(String firstName, String lastName, String email,
			Long mobilePhone, SupportStatus status, SupportLevel level,
			Long secondMobilePhone, Long landlinePhone, Long groupId)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobilePhone = mobilePhone;
		this.secondMobilePhone = secondMobilePhone;
		this.landlinePhone = landlinePhone;
		this.status = status.getId();
		this.level = level.getId();
		this.groupId = groupId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Long getMobilePhone()
	{
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone)
	{
		this.mobilePhone = mobilePhone;
	}

	public SupportStatus getStatus()
	{
		return SupportStatus.getSupportStatusForId(status);
	}

	public void setStatus(SupportStatus status)
	{
		this.status = status.getId();
	}

	public SupportLevel getLevel()
	{
		return SupportLevel.getSupportLevelForId(level);
	}

	public void setLevel(SupportLevel level)
	{
		this.level = level.getId();
	}

	public Long getSecondMobilePhone()
	{
		return secondMobilePhone;
	}

	public void setSecondMobilePhone(Long secondMobilePhone)
	{
		this.secondMobilePhone = secondMobilePhone;
	}

	public Long getLandlinePhone()
	{
		return landlinePhone;
	}

	public void setLandlinePhone(Long landlinePhone)
	{
		this.landlinePhone = landlinePhone;
	}

	public Long getGroupId()
	{
		return groupId;
	}

	public void setGroupId(Long groupId)
	{
		this.groupId = groupId;
	}

	public Date getStatusChange()
	{
		return statusChange;
	}

	public void setStatusChange(Date statusChange)
	{
		this.statusChange = statusChange;
	}

	public Date getLevelChange()
	{
		return levelChange;
	}

	public void setLevelChange(Date levelChange)
	{
		this.levelChange = levelChange;
	}

	public enum SupportStatus
	{

		AVAILABLE(1, "voll verfügbar", 0xff0d530b),
		LIMITED_AVAILABLE(2, "eingeschränkt verfügbar", 0xfffecb00),
		NOT_AVAILABLE(3, "nicht verfügbar", 0xffa5a69b);

		private int		id;
		private String	description;
		private int		color;

		SupportStatus(int id, String description, int color)
		{
			this.id = id;
			this.description = description;
			this.color = color;
		}

		public int getId()
		{
			return id;
		}

		public String getDescription()
		{
			return description;
		}

		public int getColor()
		{
			return color;
		}

		public static CharSequence[] getSupportStatusDescriptions()
		{
			CharSequence[] result = new CharSequence[SupportStatus.values().length];

			int index = 0;
			for (SupportStatus status : SupportStatus.values())
			{
				result[index] = status.getDescription();
				index++;
			}

			return result;
		}

		public static SupportStatus getSupportStatusForId(int id)
		{
			for (SupportStatus status : SupportStatus.values())
			{
				if (status.getId() == id)
				{
					return status;
				}
			}

			return null;
		}

		public static SupportStatus getSupportStatusForDescription(
				String description)
		{
			for (SupportStatus status : SupportStatus.values())
			{
				if (status.getDescription() == description)
				{
					return status;
				}
			}

			return null;
		}
	}

	public enum SupportLevel
	{

		FIRST_LEVEL(1, "1", 0xffc33222),
		SECOND_LEVEL(2, "2", 0xff004b7d),
		NONE(3, "keine Bereitschaft", 0xffa5a69b);

		private int		id;
		private String	description;
		private int		color;

		SupportLevel(int id, String description, int color)
		{
			this.id = id;
			this.description = description;
			this.color = color;
		}

		public int getId()
		{
			return id;
		}

		public String getDescription()
		{
			return description;
		}

		public int getColor()
		{
			return color;
		}

		public static SupportLevel getSupportLevelForId(int id)
		{
			for (SupportLevel level : SupportLevel.values())
			{
				if (level.getId() == id)
				{
					return level;
				}
			}

			return null;
		}
	}
}
