package benciao.android.supportmonitor.domain;

import java.util.ArrayList;
import java.util.List;

public class DomainDataWrapper
{
	private List<User>	userList;
	private List<Group>	groupList;

	public DomainDataWrapper(List<User> userList, List<Group> groupList)
	{
		this.userList = userList;
		this.groupList = groupList;
	}

	public List<User> getUserList()
	{
		return userList;
	}

	public List<Group> getGroupList()
	{
		return groupList;
	}

	public List<User> getGroupMembersByGroupName(String selectedItem)
	{
		Long groupId = null;
		List<User> result = new ArrayList<User>();

		for (Group group : getGroupList())
		{
			if (group.getName().equalsIgnoreCase(selectedItem))
			{
				groupId = group.getId();
				break;
			}
		}

		for (User user : getUserList())
		{
			if (groupId == null)
			{
				result.add(user);
			}
			else
			{
				if (user.getGroupId().longValue() == groupId.longValue())
				{
					result.add(user);
				}
			}
		}

		return result;
	}
}
