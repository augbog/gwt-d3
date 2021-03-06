/**
 * Copyright (c) 2013, Anthony Schiochet and Eric Citaire
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * The names Anthony Schiochet and Eric Citaire may not be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL BOSTOCK BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.gwtd3.demo.client.testcases.scales;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.JsArrays;
import com.github.gwtd3.api.scales.PowScale;
import com.github.gwtd3.demo.client.test.AbstractTestCase;
import com.google.gwt.user.client.ui.ComplexPanel;

public class TestPowScale extends AbstractTestCase {

	@Override
	public void doTest(final ComplexPanel sandbox) {

		// sqrt
		PowScale sqrt = D3.scale.sqrt();
		assertEquals(0.5, sqrt.exponent());

		// exponent
		PowScale scale = D3.scale.pow();
		assertEquals(1.0, scale.exponent());
		scale.exponent(5);
		assertEquals(5.0, scale.exponent());

		// get default domain
		scale = D3.scale.pow();
		assertEquals(2, scale.domain().length());
		assertEquals(0, scale.domain().getValue(0).asInt());
		assertEquals(1, scale.domain().getValue(1).asInt());

		// set the domain, keep the default range [0,1]
		scale.domain(10, 100);
		assertEquals(2, scale.domain().length());
		assertEquals(10, scale.domain().getValue(0).asInt());
		assertEquals(100, scale.domain().getValue(1).asInt());

		scale.domain("5", "6");
		assertEquals(2, scale.domain().length());
		assertEquals("5", scale.domain().getValue(0).asString());
		assertEquals("6", scale.domain().getValue(1).asString());

		scale.domain(-1, 0, 1).range(JsArrays.asJsArray(new String[] { "red", "white", "blue" }));
		assertEquals(3, scale.domain().length());
		assertEquals(-1, scale.domain().getValue(0).asInt());
		assertEquals(0, scale.domain().getValue(1).asInt());
		assertEquals(1, scale.domain().getValue(2).asInt());

		// default range
		scale = D3.scale.pow();
		assertEquals(0.0, scale.range().getNumber(0));
		assertEquals(1.0, scale.range().getNumber(1));

		// set the range
		scale.range(0, 100);
		assertEquals(0.0, scale.range().getNumber(0));
		assertEquals(100.0, scale.range().getNumber(1));

		scale.range(0, 100, 200);
		assertEquals(0.0, scale.range().getNumber(0));
		assertEquals(100.0, scale.range().getNumber(1));
		assertEquals(200.0, scale.range().getNumber(2));

		scale.range("blah", "bloh", "bluh");
		assertEquals("blah", scale.range().getString(0));
		assertEquals("bloh", scale.range().getString(1));
		assertEquals("bluh", scale.range().getString(2));

		// range round
		scale.rangeRound(0, 100);
		assertEquals(0.0, scale.range().getNumber(0));
		assertEquals(100.0, scale.range().getNumber(1));

		scale.rangeRound(0, 100, 200);
		assertEquals(0.0, scale.range().getNumber(0));
		assertEquals(100.0, scale.range().getNumber(1));
		assertEquals(200.0, scale.range().getNumber(2));

		scale.rangeRound("blah", "bloh", "bluh");
		assertEquals("blah", scale.range().getString(0));
		assertEquals("bloh", scale.range().getString(1));
		assertEquals("bluh", scale.range().getString(2));

		// clamp
		assertEquals(false, scale.clamp());
		scale.clamp(true);
		assertEquals(true, scale.clamp());

		// ticks
		scale = D3.scale.pow();
		scale.domain(10, 20);
		assertEquals(3, scale.ticks(2).length());
		assertEquals(10.0, scale.ticks(2).getNumber(0));
		assertEquals(15.0, scale.ticks(2).getNumber(1));
		assertEquals(20.0, scale.ticks(2).getNumber(2));

		assertEquals(11, scale.ticks(11).length());
		assertEquals(10.0, scale.ticks(11).getNumber(0));
		assertEquals(11.0, scale.ticks(11).getNumber(1));
		assertEquals(15.0, scale.ticks(11).getNumber(5));
		assertEquals(20.0, scale.ticks(11).getNumber(10));

		// tickFormat
		scale = D3.scale.pow();
		scale.domain(10, 100);
		assertEquals("10", scale.tickFormat().format(10));
		assertEquals("10", scale.tickFormat(2).format(10));
		assertEquals("100", scale.tickFormat(2).format(100));
		// FIXME: does not work see issue #50
		// assertEquals("015.00", scale.tickFormat(20, "$,.2f").format(50));
		// FIXME: and the tickFormat(count,function) version

		// nice
		scale = D3.scale.pow();
		scale.domain(1.02, 4.98);
		assertEquals(1.02, scale.domain().getNumber(0));
		assertEquals(4.98, scale.domain().getNumber(1));
		scale.nice();
		assertEquals(1.0, scale.domain().getNumber(0));
		assertEquals(5.0, scale.domain().getNumber(1));

		// apply the function
		scale = D3.scale.pow();
		scale.domain(1, 10).range(0, 10);
		assertEquals(-1, scale.apply(0).asInt());
		assertEquals(10, scale.apply(10).asInt());
		assertEquals(110, scale.apply(100).asInt());
		assertEquals(-12, scale.apply(-10).asInt());

		// invert
		assertEquals(91, scale.invert(100).asInt());
		assertEquals(-89, scale.invert(-100).asInt());

		// copy
		scale.domain(1, 2);
		PowScale copy = scale.copy();
		copy.domain(2, 3);
		assertEquals(1.0, scale.domain().getNumber(0));
		assertEquals(2.0, scale.domain().getNumber(1));

	}
}
