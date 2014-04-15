package es.caib.sdesapplin2.signatura.services.custodiadocumentos;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.0.0-milestone2
 * 2014-04-14T16:58:34.397+02:00
 * Generated source version: 3.0.0-milestone2
 * 
 */
@WebServiceClient(name = "CustodiaService", 
                  wsdlLocation = "file:/C:/Users/javierg/workspace_helium_30/svzd/WebContent/WEB-INF/custodia.wsdl",
                  targetNamespace = "http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos") 
public class CustodiaService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos", "CustodiaService");
    public final static QName CustodiaDocumentos = new QName("http://sdesapplin2.caib.es/signatura/services/CustodiaDocumentos", "CustodiaDocumentos");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Users/javierg/workspace_helium_30/svzd/WebContent/WEB-INF/custodia.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(CustodiaService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/C:/Users/javierg/workspace_helium_30/svzd/WebContent/WEB-INF/custodia.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public CustodiaService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public CustodiaService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CustodiaService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CustodiaService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CustodiaService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public CustodiaService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    

    /**
     *
     * @return
     *     returns Custodia
     */
    @WebEndpoint(name = "CustodiaDocumentos")
    public Custodia getCustodiaDocumentos() {
        return super.getPort(CustodiaDocumentos, Custodia.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Custodia
     */
    @WebEndpoint(name = "CustodiaDocumentos")
    public Custodia getCustodiaDocumentos(WebServiceFeature... features) {
        return super.getPort(CustodiaDocumentos, Custodia.class, features);
    }

}
