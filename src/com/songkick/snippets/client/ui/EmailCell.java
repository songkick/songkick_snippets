package com.songkick.snippets.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.songkick.common.model.EmailAddress;

public class EmailCell extends AbstractCell<EmailAddress> {
	private static final String PADDING = "4px";
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			EmailAddress value, SafeHtmlBuilder sb) {
		if (value==null) {
			return;
		}
		
		sb.appendHtmlConstant("<div style=\"border-bottom: 1px solid black;\">");
		sb.appendHtmlConstant("<div style=\"padding-left: "
				+ PADDING + "; padding-right:" + PADDING + "; padding-top: "
				+ PADDING + ";\">");
		sb.append(SafeHtmlUtils.fromString(value.getEmail()));
		if (value.isPrimary()) {
			sb.append(SafeHtmlUtils.fromString(" (primary)"));
		}
		sb.appendHtmlConstant("</div>");
		sb.appendHtmlConstant("</div>");
	}

}
