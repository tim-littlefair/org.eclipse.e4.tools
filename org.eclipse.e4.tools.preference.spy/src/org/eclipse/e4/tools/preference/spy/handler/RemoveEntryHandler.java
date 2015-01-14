/*******************************************************************************
 * Copyright (c) 2015 vogella GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.tools.preference.spy.handler;

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tools.preference.spy.constants.PreferenceSpyEventTopics;
import org.eclipse.e4.tools.preference.spy.model.PreferenceEntry;
import org.eclipse.e4.ui.services.IServiceConstants;

public class RemoveEntryHandler {
	@Execute
	public void execute(
			IEventBroker eventBroker,
			@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<PreferenceEntry> preferenceEntries) {
		eventBroker
				.post(PreferenceSpyEventTopics.PREFERENCESPY_PREFERENCE_ENTRIES_DELETE,
						preferenceEntries);
	}

}