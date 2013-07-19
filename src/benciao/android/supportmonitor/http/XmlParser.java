package benciao.android.supportmonitor.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import benciao.android.supportmonitor.domain.DomainDataWrapper;
import benciao.android.supportmonitor.domain.Group;
import benciao.android.supportmonitor.domain.Group.SupportType;
import benciao.android.supportmonitor.domain.User;
import benciao.android.supportmonitor.domain.User.SupportLevel;
import benciao.android.supportmonitor.domain.User.SupportStatus;

public class XmlParser
{
	public static boolean parseRegistration(String response)
	{
		return response.contains("<existence>true</existence>");
	}

	public static boolean parseBooleanResult(String response)
	{
		return response.contains("<result>true</result>");
	}

	public static DomainDataWrapper parseStatus(InputStream response)
	{
		DomainDataWrapper domainDataWrapper = null;
		List<User> userList = new ArrayList<User>();
		List<Group> groupList = new ArrayList<Group>();

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(response);

			XPathFactory xfactory = XPathFactory.newInstance();
			XPath xpath = xfactory.newXPath();
			XPathExpression expr = xpath.compile("//users/user");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList users = (NodeList) result;
			for (int i = 0; i < users.getLength(); i++)
			{
				Node node = users.item(i);
				Element xmlUser = (Element) node;

				// first-name
				expr = xpath.compile("first-name");
				NodeList nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				String firstName = nodes.item(0).getTextContent();

				// last-name
				expr = xpath.compile("last-name");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				String lastName = nodes.item(0).getTextContent();

				// mobile-phone
				expr = xpath.compile("mobile-phone");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				Long mobilePhone = Long.parseLong(nodes.item(0)
						.getTextContent());

				// second-mobile-phone
				expr = xpath.compile("second-mobile-phone");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				String temp = nodes.item(0).getTextContent();
				Long secondMobilePhone = temp.equalsIgnoreCase("") ? 0 : Long
						.parseLong(temp);

				// landline-phone
				expr = xpath.compile("landline-phone");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				temp = nodes.item(0).getTextContent();
				Long landlinePhone = temp.equalsIgnoreCase("") ? 0 : Long
						.parseLong(temp);

				// email
				expr = xpath.compile("email");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				String email = nodes.item(0).getTextContent();

				// status
				expr = xpath.compile("status");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				int status = Integer.parseInt(nodes.item(0).getTextContent());

				// status-change
				expr = xpath.compile("status-change");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				long statusChange = Long.parseLong(nodes.item(0)
						.getTextContent());

				// level
				expr = xpath.compile("level");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				int level = Integer.parseInt(nodes.item(0).getTextContent());

				// level-change
				expr = xpath.compile("level-change");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				long levelChange = Long.parseLong(nodes.item(0)
						.getTextContent());

				// group-id
				expr = xpath.compile("group-id");
				nodes = (NodeList) expr.evaluate(xmlUser,
						XPathConstants.NODESET);
				Long groupId = Long.parseLong(nodes.item(0).getTextContent());

				User user = new User(firstName, lastName, email, mobilePhone,
						SupportStatus.getSupportStatusForId(status),
						SupportLevel.getSupportLevelForId(level),
						secondMobilePhone, landlinePhone, groupId);

				user.setStatusChange(new Date(statusChange));
				user.setLevelChange(new Date(levelChange));
				userList.add(user);
			}

			expr = xpath.compile("//groups/group");

			result = expr.evaluate(doc, XPathConstants.NODESET);

			NodeList groups = (NodeList) result;
			for (int i = 0; i < groups.getLength(); i++)
			{
				Node node = groups.item(i);
				Element xmlGroup = (Element) node;

				// name
				expr = xpath.compile("name");
				NodeList nodes = (NodeList) expr.evaluate(xmlGroup,
						XPathConstants.NODESET);
				String name = nodes.item(0).getTextContent();

				// description
				expr = xpath.compile("description");
				nodes = (NodeList) expr.evaluate(xmlGroup,
						XPathConstants.NODESET);
				String description = nodes.item(0).getTextContent();

				// id
				expr = xpath.compile("id");
				nodes = (NodeList) expr.evaluate(xmlGroup,
						XPathConstants.NODESET);
				Long id = Long.parseLong(nodes.item(0).getTextContent());

				// support-type
				expr = xpath.compile("support-type");
				nodes = (NodeList) expr.evaluate(xmlGroup,
						XPathConstants.NODESET);
				int supportType = Integer.parseInt(nodes.item(0)
						.getTextContent());

				Group group = new Group(name, description,
						SupportType.getSupportTypeForId(supportType), id);

				groupList.add(group);
			}

			domainDataWrapper = new DomainDataWrapper(userList, groupList);
		}
		catch (Exception ex)
		{
			Log.e("Exception", ex.getMessage());
		}
		return domainDataWrapper;
	}
}
