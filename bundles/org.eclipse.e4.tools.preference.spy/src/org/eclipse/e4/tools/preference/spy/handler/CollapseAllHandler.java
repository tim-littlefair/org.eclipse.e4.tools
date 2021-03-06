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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tools.preference.spy.parts.TreeViewerPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class CollapseAllHandler {

	@Execute
	public void execute(MPart part) {
		Object partImpl = part.getObject();
		if (partImpl instanceof TreeViewerPart) {
			((TreeViewerPart) partImpl).getViewer().collapseAll();
		}
	}

}