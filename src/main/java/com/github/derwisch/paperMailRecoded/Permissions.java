package com.github.derwisch.paperMailRecoded;

import org.bukkit.permissions.Permission;

public class Permissions {
	public static final Permission CREATE_CHEST_SELF_PERM = new Permission(Strings.CREATE_CHEST_SELF_STR);
	public static final Permission CREATE_CHEST_ALL_PERM = new Permission(Strings.CREATE_CHEST_ALL_STR);
	public static final Permission SEND_ITEM_PERM = new Permission(Strings.SEND_ITEM_STR);
	public static final Permission SEND_TEXT_PERM = new Permission(Strings.SEND_TEXT_STR);
	public static final Permission COSTS_EXEMPT = new Permission(Strings.COSTS_EXEMPT);
	
	public class Strings {
		public static final String CREATE_CHEST_SELF_STR = "papermail.createchest.self";
		public static final String CREATE_CHEST_ALL_STR = "papermail.createchest.everyone";
		public static final String SEND_ITEM_STR = "papermail.send.item";
		public static final String SEND_TEXT_STR = "papermail.send.text";
		public static final String COSTS_EXEMPT = "papermail.costs.exempt";
	}
}
