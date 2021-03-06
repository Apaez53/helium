/**
 * DownloadResponseDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente;

public class DownloadResponseDocument  implements java.io.Serializable {
    private int id;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes attributes;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveOptions archiveOptions;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex[] annexes;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile[] signatureFiles;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile[] visualFiles;

    private net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps steps;

    public DownloadResponseDocument() {
    }

    public DownloadResponseDocument(
           int id,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes attributes,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveOptions archiveOptions,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex[] annexes,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile[] signatureFiles,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile[] visualFiles,
           net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps steps) {
           this.id = id;
           this.attributes = attributes;
           this.archiveOptions = archiveOptions;
           this.annexes = annexes;
           this.signatureFiles = signatureFiles;
           this.visualFiles = visualFiles;
           this.steps = steps;
    }


    /**
     * Gets the id value for this DownloadResponseDocument.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this DownloadResponseDocument.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the attributes value for this DownloadResponseDocument.
     * 
     * @return attributes
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this DownloadResponseDocument.
     * 
     * @param attributes
     */
    public void setAttributes(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.DocumentAttributes attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the archiveOptions value for this DownloadResponseDocument.
     * 
     * @return archiveOptions
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveOptions getArchiveOptions() {
        return archiveOptions;
    }


    /**
     * Sets the archiveOptions value for this DownloadResponseDocument.
     * 
     * @param archiveOptions
     */
    public void setArchiveOptions(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.ArchiveOptions archiveOptions) {
        this.archiveOptions = archiveOptions;
    }


    /**
     * Gets the annexes value for this DownloadResponseDocument.
     * 
     * @return annexes
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex[] getAnnexes() {
        return annexes;
    }


    /**
     * Sets the annexes value for this DownloadResponseDocument.
     * 
     * @param annexes
     */
    public void setAnnexes(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Annex[] annexes) {
        this.annexes = annexes;
    }


    /**
     * Gets the signatureFiles value for this DownloadResponseDocument.
     * 
     * @return signatureFiles
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile[] getSignatureFiles() {
        return signatureFiles;
    }


    /**
     * Sets the signatureFiles value for this DownloadResponseDocument.
     * 
     * @param signatureFiles
     */
    public void setSignatureFiles(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.SignatureFile[] signatureFiles) {
        this.signatureFiles = signatureFiles;
    }


    /**
     * Gets the visualFiles value for this DownloadResponseDocument.
     * 
     * @return visualFiles
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile[] getVisualFiles() {
        return visualFiles;
    }


    /**
     * Sets the visualFiles value for this DownloadResponseDocument.
     * 
     * @param visualFiles
     */
    public void setVisualFiles(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.VisualFile[] visualFiles) {
        this.visualFiles = visualFiles;
    }


    /**
     * Gets the steps value for this DownloadResponseDocument.
     * 
     * @return steps
     */
    public net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps getSteps() {
        return steps;
    }


    /**
     * Sets the steps value for this DownloadResponseDocument.
     * 
     * @param steps
     */
    public void setSteps(net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws.cliente.Steps steps) {
        this.steps = steps;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DownloadResponseDocument)) return false;
        DownloadResponseDocument other = (DownloadResponseDocument) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.id == other.getId() &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes()))) &&
            ((this.archiveOptions==null && other.getArchiveOptions()==null) || 
             (this.archiveOptions!=null &&
              this.archiveOptions.equals(other.getArchiveOptions()))) &&
            ((this.annexes==null && other.getAnnexes()==null) || 
             (this.annexes!=null &&
              java.util.Arrays.equals(this.annexes, other.getAnnexes()))) &&
            ((this.signatureFiles==null && other.getSignatureFiles()==null) || 
             (this.signatureFiles!=null &&
              java.util.Arrays.equals(this.signatureFiles, other.getSignatureFiles()))) &&
            ((this.visualFiles==null && other.getVisualFiles()==null) || 
             (this.visualFiles!=null &&
              java.util.Arrays.equals(this.visualFiles, other.getVisualFiles()))) &&
            ((this.steps==null && other.getSteps()==null) || 
             (this.steps!=null &&
              this.steps.equals(other.getSteps())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getId();
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        if (getArchiveOptions() != null) {
            _hashCode += getArchiveOptions().hashCode();
        }
        if (getAnnexes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAnnexes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAnnexes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSignatureFiles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSignatureFiles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSignatureFiles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getVisualFiles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVisualFiles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVisualFiles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSteps() != null) {
            _hashCode += getSteps().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DownloadResponseDocument.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DownloadResponseDocument"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "DocumentAttributes"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("archiveOptions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "archive-options"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "ArchiveOptions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("annexes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "annexes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Annex"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "annex"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signatureFiles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signature-files"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "SignatureFile"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "file"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("visualFiles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "visual-files"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "VisualFile"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "file"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("steps");
        elemField.setXmlName(new javax.xml.namespace.QName("", "steps"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.indra.es/portafirmasws/cws", "Steps"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
