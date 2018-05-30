package com.liferay.blade.cli.util;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_FixedWidth;
import de.vandermeer.asciithemes.TA_GridThemes;

import java.util.Collection;

public class ExtensionConfigEntryTableAdapter implements TableAdapter<Collection<ExtensionConfigEntry>> {

	public static final String get(Collection<ExtensionConfigEntry> t) {
		ExtensionConfigEntryTableAdapter adapter = new ExtensionConfigEntryTableAdapter();

		return adapter.apply(t);
	}

	@Override
	public String apply(Collection<ExtensionConfigEntry> t) {
		AsciiTable at = new AsciiTable();

		at.getRenderer().setCWC(new CWC_FixedWidth().add(15).add(40).add(30));
		at.addRule();
		at.addRow(null, null, "List of Available Blade Extensions");
		at.addRule();
		at.addRow("name", "description", "link");
		at.addRule();

		for (ExtensionConfigEntry entry : t) {
			at.addRow(entry.getName(), entry.getDescription(), entry.getLocation());
			at.addRule();
		}

		at.getContext().setGridTheme(TA_GridThemes.NONE);

		return at.render();
	}

}