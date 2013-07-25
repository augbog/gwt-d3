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
/**
 * 
 */
package com.github.gwtd3.ui.colors;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.behaviour.Drag;
import com.github.gwtd3.api.behaviour.Drag.DragEventType;
import com.github.gwtd3.api.core.Color;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.svg.Symbol;
import com.github.gwtd3.api.svg.Symbol.Type;
import com.github.gwtd3.ui.svg.GradientBuilder;
import com.github.gwtd3.ui.svg.SVGDocumentContainer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:schiochetanthoni@gmail.com">Anthony Schiochet</a>
 * 
 */
public abstract class ComponentSelector extends SVGDocumentContainer implements HasValue<Double> {

	/**
	 * 
	 */
	private static final int rectangeWidth = 20;

	protected Selection rectangleSelector;
	// protected Selection text;
	// protected Selection selectedValueText;

	private int steps;
	protected Selection componentSymbol;
	private Symbol symbol;

	private boolean dragging;

	protected double value;

	private HandlerRegistration registration;

	/**
	 * 
	 */
	public ComponentSelector() {
		super();
		getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		getElement().getStyle().setProperty("userSelect", "none");
		getElement().getStyle().setProperty("webkitUserSelect", "none");
		setPixelSize(60, 300);
		// FIXMEtransform().translate(0, 10);
		init();
	}

	private void init() {
		symbol = D3.svg().symbol().type(Type.TRIANGLE_UP);

		rectangleSelector = select().append("rect")
				.attr("x", 0)
				.attr("y", 0)
				.attr("width", ComponentSelector.rectangeWidth)
				.attr("height", 250);

		rectangleSelector.node().getStyle().setCursor(Cursor.POINTER);
		rectangleSelector.node().getStyle().setProperty("userSelect", "none");
		rectangleSelector.node().getStyle().setProperty("webkitUserSelect", "none");

		componentSymbol = select().append("path")
				.attr("fill", "blue")
				.attr("transform", "translate(" + (Integer.parseInt(rectangleSelector.attr("width")) + 4) + "," + 0 + ") rotate(-90)")
				.attr("d", symbol);

		Drag dragBehavior = D3.behavior().drag().on(DragEventType.drag, new DatumFunction<Void>() {
			@Override
			public Void apply(final Element context, final Value d, final int index) {
				updateColorFromMouse(rectangleSelector);
				return null;
			}
		});
		rectangleSelector.call(dragBehavior);
		componentSymbol.call(dragBehavior);
		componentSymbol.node().getStyle().setCursor(Cursor.POINTER);
		componentSymbol.node().getStyle().setCursor(Cursor.POINTER);

		createGradient(getGradientSteps());
	}

	/**
	 * @return
	 */
	protected abstract int getGradientSteps();

	/**
	 * @return
	 */
	protected abstract String getSelectedValueText();

	/**
	 * @return
	 */
	protected double yToValue(final double y) {
		String attr = rectangleSelector.attr("height");
		int height = Integer.parseInt(attr);
		return ((getComponentMax() * y) / height);
	}

	/**
	 * @return the maximum value of the value
	 */
	protected abstract double getComponentMax();

	private void createGradient(final int steps) {
		this.steps = steps;
		// text.text("number of steps: " + steps);
		String name = this.getClass().getName();
		name = name.substring(name.lastIndexOf('.') + 1);
		GradientBuilder builder = GradientBuilder.createVerticalLinearGradient(this, name + steps);
		for (int i = 0; i <= steps; i += 1) {
			builder.appendStop(i * (100 / steps), createColor(i * (getComponentMax() / steps)));
		}

		rectangleSelector.attr("fill", builder.toFillUrl());
	}

	/**
	 * @param i
	 * @return
	 */
	protected abstract Color createColor(double value);

	private void position(final double y) {

		componentSymbol
		.attr("transform", "translate(" + (Integer.parseInt(rectangleSelector.attr("width")) + 4) + "," + y + ") rotate(-90)");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.TakesValue#getValue()
	 */
	@Override
	public Double getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.TakesValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(final Double value) {
		setValue(value, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(final Double value, final boolean fireEvents) {
		double oldValue = this.value;
		this.value = value;
		// selectedValueText.text(getSelectedValueText());
		componentSymbol.attr("fill", createColor(value.doubleValue()).toString());
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Double> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * 
	 */
	protected void updateGradient() {
		createGradient(steps);
	}

	private void updateColorFromMouse(final Selection selection) {
		double mouseY = D3.mouseY(selection.node());
		double value = yToValue(mouseY);
		setValue(value, true);
		position(mouseY);
	}

}
