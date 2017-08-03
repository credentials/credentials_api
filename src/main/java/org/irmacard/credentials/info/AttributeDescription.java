/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.credentials.info;

import java.io.Serializable;

import org.w3c.dom.Element;

public class AttributeDescription implements Serializable{
	private static final long serialVersionUID = 4609118645897084209L;
	private String name;
	private TranslatedString hrName;
	private TranslatedString description;

	AttributeDescription(Element e) {
		name = e.getAttribute("id");
		hrName = new TranslatedString(e.getElementsByTagName("Name").item(0));
		description = new TranslatedString(e.getElementsByTagName("Description").item(0));
	}

	public AttributeDescription(String name, TranslatedString hrName, TranslatedString description) {
		this.name = name;
		this.hrName = hrName;
		this.description = description;
	}

	/**
	 * @return name of the attribute
	 */
	public String getName() {
		return name;
	}

	public TranslatedString getHrName() {
		return hrName;
	}

	/**
	 * @return description of the attribute
	 */
	public TranslatedString getDescription() {
		return description;
	}

	public String toString() {
		return name + ": " + description;
	}
}
