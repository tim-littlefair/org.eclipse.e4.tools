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
package org.eclipse.e4.tools.preference.spy.addon;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.tools.preference.spy.Activator;
import org.eclipse.e4.tools.preference.spy.constants.PreferenceConstants;
import org.eclipse.e4.tools.preference.spy.constants.PreferenceSpyEventTopics;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

/**
 * This model addon is used to register an IPreferenceChangeListener for all
 * {@link EclipsePreferences} and it fires an
 * {@link PreferenceSpyEventTopics#PREFERENCESPY_PREFERENCE_CHANGED} event via
 * the {@link IEventBroker}.<br/>
 * The Object, which is send within the
 * {@link PreferenceSpyEventTopics#PREFERENCESPY_PREFERENCE_CHANGED} event is a
 * PreferenceChangeEvent.
 *
 */
@SuppressWarnings("restriction")
public class PreferenceSpyAddon {

	@Inject
	private Logger LOG;

	@Inject
	private IEventBroker eventBroker;

	private IEclipsePreferences bundleDefaultsScopePreferences = BundleDefaultsScope.INSTANCE.getNode("");
	private IEclipsePreferences configurationScopePreferences = ConfigurationScope.INSTANCE.getNode("");
	private IEclipsePreferences defaultScopePreferences = DefaultScope.INSTANCE.getNode("");
	private IEclipsePreferences instanceScopePreferences = InstanceScope.INSTANCE.getNode("");

	private ChangedPreferenceListener preferenceChangedListener = new ChangedPreferenceListener();

	@Inject
	@Optional
	public void initialzePreferenceSpy(
			@Preference(value = PreferenceConstants.TRACE_PREFERENCES) boolean tracePreferences) {
		if (tracePreferences) {
			registerVisitors();
		} else {
			deregisterVisitors();
		}
	}

	private void registerVisitors() {
		addPreferenceListener(bundleDefaultsScopePreferences);
		addPreferenceListener(configurationScopePreferences);
		addPreferenceListener(defaultScopePreferences);
		addPreferenceListener(instanceScopePreferences);
	}

	private void addPreferenceListener(IEclipsePreferences rootPreference) {
		try {
			rootPreference.accept(new IPreferenceNodeVisitor() {
				@Override
				public boolean visit(IEclipsePreferences node) throws BackingStoreException {
					node.addPreferenceChangeListener(preferenceChangedListener);
					return true;
				}
			});
		} catch (BackingStoreException e) {
			LOG.error(e);
		}
	}

	private void deregisterVisitors() {
		removePreferenceListener(bundleDefaultsScopePreferences);
		removePreferenceListener(configurationScopePreferences);
		removePreferenceListener(defaultScopePreferences);
		removePreferenceListener(instanceScopePreferences);
	}

	private void removePreferenceListener(IEclipsePreferences rootPreference) {
		try {
			rootPreference.accept(new IPreferenceNodeVisitor() {
				@Override
				public boolean visit(IEclipsePreferences node) throws BackingStoreException {
					node.removePreferenceChangeListener(preferenceChangedListener);
					return true;
				}
			});
		} catch (BackingStoreException e) {
			LOG.error(e);
		}
	}

	@PostConstruct
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.TRACE_PREFERENCES, false);
	}

	private final class ChangedPreferenceListener implements IPreferenceChangeListener {

		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			eventBroker.post(PreferenceSpyEventTopics.PREFERENCESPY_PREFERENCE_CHANGED, event);
		}
	}
}
