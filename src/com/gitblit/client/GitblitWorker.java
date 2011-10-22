/*
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.client;

import java.awt.Component;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.gitblit.Constants.RpcRequest;
import com.gitblit.GitBlitException.ForbiddenException;
import com.gitblit.GitBlitException.UnauthorizedException;

public abstract class GitblitWorker extends SwingWorker<Boolean, Void> {

	private final Component parent;

	private final RpcRequest request;

	public GitblitWorker(Component parent, RpcRequest request) {
		this.parent = parent;
		this.request = request;
	}

	protected RpcRequest getRequestType() {
		return request;
	}

	@Override
	protected Boolean doInBackground() throws IOException {
		return doRequest();
	}

	protected void done() {
		try {
			Boolean success = get();
			if (success) {
				onSuccess();
			} else {
				onFailure();
			}
		} catch (Throwable t) {
			if (t instanceof ForbiddenException) {
				Utils.explainForbidden(parent, request);
			} else if (t instanceof UnauthorizedException) {
				Utils.explainUnauthorized(parent, request);
			} else {
				Utils.showException(parent, t);
			}
		}
	}

	protected abstract Boolean doRequest() throws IOException;

	protected abstract void onSuccess();

	protected void onFailure() {
	}

	protected void showFailure(String message, Object... args) {
		String msg = MessageFormat.format(message, args);
		JOptionPane.showMessageDialog(parent, msg, Translation.get("gb.error"),
				JOptionPane.ERROR_MESSAGE);
	}
}