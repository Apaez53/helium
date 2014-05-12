/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package es.caib.proves.signatura.services.custodiadocumentos;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class was generated by Apache CXF 3.0.0-milestone2 2014-04-30T16:41:34.603+02:00 Generated source version: 3.0.0-milestone2
 * 
 */

@javax.jws.WebService(serviceName = "CustodiaService", portName = "CustodiaDocumentos", targetNamespace = "https://proves.caib.es/signatura/services/CustodiaDocumentos", wsdlLocation = "file:/C:/Users/javierg/workspace_helium_30/z/WebContent/WEB-INF/custodia.wsdl", endpointInterface = "es.caib.proves.signatura.services.custodiadocumentos.Custodia")
public class CustodiaDocumentosImpl implements Custodia {

	private static final Logger LOG = Logger.getLogger(CustodiaDocumentosImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#purgarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] purgarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation purgarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#obtenerInformeDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] obtenerInformeDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation obtenerInformeDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoXAdES(byte[] in0 )*
	 */
	public byte[] custodiarDocumentoXAdES(byte[] in0) {
		LOG.info("Executing operation custodiarDocumentoXAdES");
		System.out.println(in0);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#eliminarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] eliminarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation eliminarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#recuperarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] recuperarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation recuperarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoSMIMEV2(byte[] in0 )*
	 */
	public byte[] custodiarDocumentoSMIMEV2(byte[] in0) {
		LOG.info("Executing operation custodiarDocumentoSMIMEV2");
		System.out.println(in0);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#recuperarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] recuperarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation recuperarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#obtenerInformeDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] obtenerInformeDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation obtenerInformeDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#verificarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] verificarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation verificarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#verificarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] verificarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation verificarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoV2(byte[] in0 )*
	 */
	public byte[] custodiarDocumentoV2(byte[] in0) {
		LOG.info("Executing operation custodiarDocumentoV2");
		System.out.println(in0);

		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;

		// output DOM XML to console
		Transformer transformer = null;
		String output = null;
		try {
			icBuilder = icFactory.newDocumentBuilder();

			Document doc = icBuilder.newDocument();
			Element mainRootElement = doc.createElementNS("https://proves.caib.es/signatura/services/CustodiaDocumentos", "CustodiaResponse");
			doc.appendChild(mainRootElement);

			// append child elements to root element
			mainRootElement.appendChild(getNodeVerifyResponse(doc, "1", "2", "3", "Custodiado"));

			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			byte[] _return = output.getBytes();
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	private static Node getNodeVerifyResponse(Document doc, String id, String resultMajor, String resultMinor, String resultMessage) {
		Element result = doc.createElement("VerifyResponse");
		result.setAttribute("id", id);
		Node nodo = getResult(doc, id, resultMajor, resultMinor, resultMessage);
		result.appendChild(nodo);
		return result;
	}

	private static Node getResult(Document doc, String id, String resultMajor, String resultMinor, String resultMessage) {
		Element result = doc.createElement("Result");
		result.setAttribute("id", id);
		result.appendChild(getElements(doc, result, "ResultMajor", resultMajor));
		result.appendChild(getElements(doc, result, "ResultMinor", resultMinor));
		result.appendChild(getElements(doc, result, "ResultMessage", resultMessage));
		return result;
	}

	// utility method to create text node
	private static Node getElements(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#reservarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] reservarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation reservarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#reservarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] reservarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation reservarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#eliminarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] eliminarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation eliminarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumento(java.lang.String in0 )*
	 */
	public byte[] custodiarDocumento(java.lang.String in0) {
		LOG.info("Executing operation custodiarDocumento");
		System.out.println(in0);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#purgarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] purgarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation purgarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarReservaDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] consultarReservaDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation consultarReservaDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarDocumentoSMIME(java.lang.String in0 )*
	 */
	public byte[] custodiarDocumentoSMIME(java.lang.String in0) {
		LOG.info("Executing operation custodiarDocumentoSMIME");
		System.out.println(in0);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarPDFFirmadoV2(byte[] in0 )*
	 */
	public byte[] custodiarPDFFirmadoV2(byte[] in0) {
		LOG.info("Executing operation custodiarPDFFirmadoV2");
		System.out.println(in0);
		try {
			byte[] _return = in0;
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarDocumentoV2(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] consultarDocumentoV2(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation consultarDocumentoV2");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = "bon dia".getBytes();
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#custodiarPDFFirmado(java.lang.String in0 )*
	 */
	public byte[] custodiarPDFFirmado(java.lang.String in0) {
		LOG.info("Executing operation custodiarPDFFirmado");
		System.out.println(in0);
		try {
			byte[] _return = "bon dia".getBytes();
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.caib.proves.signatura.services.custodiadocumentos.Custodia#consultarDocumento(java.lang.String in0 ,)java.lang.String in1 ,)java.lang.String in2 )*
	 */
	public byte[] consultarDocumento(java.lang.String in0, java.lang.String in1, java.lang.String in2) {
		LOG.info("Executing operation consultarDocumento");
		System.out.println(in0);
		System.out.println(in1);
		System.out.println(in2);
		try {
			byte[] _return = new byte[] {};
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

}