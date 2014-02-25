package org.cube.bender.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cube.bender.Bender;
import org.cube.bender.BenderExeption;
import org.cube.bender.BenderSource;
import org.cube.bender.BenderVisitor;
import org.cube.bender.Binding;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XmlSource extends BenderSource {
	
	private InputStream stream;
	
	private Locator saxLocator;
	
	private BenderVisitor visitor;

	public XmlSource(InputStream stream) {
		this.stream = stream;
	}

	/* (non-Javadoc)
	 * @see org.cube.bender.BenderSource#accept(org.cube.bender.BenderVisitor)
	 */
	@Override
	protected void accept(BenderVisitor visitor) throws BenderExeption {
		this.visitor = visitor;
		try {
			SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
			sax.parse(stream, new SAXHandle());
		} catch (Exception e) {
			// TODO locator
			throw new BenderExeption("Error parsing input source", e);
		}
	}


	private class SAXHandle extends DefaultHandler {
		
		public void setDocumentLocator(Locator locator) {
			saxLocator = locator;
		}

		public void endDocument() throws SAXException {
			visitor.visitEnd();
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			pushPath(qName);
			try {
				Binding<Bender> binding = getBinding(getAbsolutePath());
				if (binding != null) {
					Object v = invoke(binding, attributes);
				}
				
//				Object v = vstack.peek().invoke(getPath(), attributes);
//				if (v != null && v instanceof XmlVisitor) {
//					vstack.add(new ReflectedVisitor((XmlVisitor) v));
//				}
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			popPath();
			if (vstack.size() > 1) {
				vstack.pop();
			}
		}
		
		private Object invoke(Binding<Bender> binding, Attributes attrs) throws Exception {
			Method m = binding.getMethod();
			Class<?>[] types = m.getParameterTypes();
			Object[] params = new Object[types.length];
			for (int i = 0; i < types.length; i++) {
				String value = attrs.getValue(i);
				if (String.class.equals(types[i])) {
					params[i] = value;
				} else if (Boolean.class.equals(types[i])) {
					params[i] = Boolean.parseBoolean(value);
				} else {
					if (value == null) value = "0";
					if (Integer.class.equals(types[i])) {
						params[i] = Integer.parseInt(attrs.getValue(i));
					} else if (Double.class.equals(types[i])) {
						params[i] = Double.parseDouble(attrs.getValue(i));
					} else if (Float.class.equals(types[i])) {
						params[i] = Boolean.parseBoolean(attrs.getValue(i));
					} else if (Long.class.equals(types[i])) {
						params[i] = Long.parseLong(attrs.getValue(i));
					} else if (Byte.class.equals(types[i])) {
						params[i] = Byte.parseByte(attrs.getValue(i));
					} else {
						throw new IllegalArgumentException("Cannot convert from " + attrs.getValue(i) + " to " + types[i]);
					}
				}
			}
			return m.invoke(visitor, params);
		}

	}
}
