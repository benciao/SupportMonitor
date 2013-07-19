package benciao.android.supportmonitor.domain;

public class Group
{
	private int		supportType;
	private Long	id;
	private String	name;
	private String	description;

	public Group(String name, String description, SupportType supportType,
			Long id)
	{
		this.name = name;
		this.description = description;
		this.supportType = supportType.getId();
		this.id = id;
	}

	public SupportType getSupportType()
	{
		return SupportType.getSupportTypeForId(supportType);
	}

	public void setSupportType(SupportType supportType)
	{
		this.supportType = supportType.getId();
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public enum SupportType
	{
		ST_24_7(1, "24/7"),

		ST_8_20(2, "08:00 - 20:00");

		private int		id;
		private String	description;

		SupportType(int id, String description)
		{
			this.id = id;
			this.description = description;
		}

		public int getId()
		{
			return id;
		}

		public String getDescription()
		{
			return description;
		}

		public static SupportType getSupportTypeForId(int id)
		{
			for (SupportType type : SupportType.values())
			{
				if (type.getId() == id)
				{
					return type;
				}
			}

			return null;
		}
	}
}
