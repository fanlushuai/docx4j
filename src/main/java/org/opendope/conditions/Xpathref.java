package org.opendope.conditions;

import static org.docx4j.model.datastorage.XPathEnhancerParser.enhanceXPath;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.docx4j.XmlUtils;
import org.docx4j.model.datastorage.BindingHandler;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlPart;
import org.docx4j.openpackaging.parts.opendope.XPathsPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "xpathref")
public class Xpathref implements Evaluable {

	private static Logger log = LoggerFactory.getLogger(Xpathref.class);
	
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String id;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

	public boolean evaluate(WordprocessingMLPackage pkg, 
			Map<String, CustomXmlPart> customXmlDataStorageParts,
			Map<String, Condition> conditionsMap,
			Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
		
		//org.opendope.xpaths.Xpaths.Xpath xpath = XPathsPart.getXPathById(xPaths, id);	
		org.opendope.xpaths.Xpaths.Xpath xpath = xpathsMap.get(id);
		
		String val = BindingHandler.xpathGetString(pkg,
				customXmlDataStorageParts, xpath.getDataBinding()
						.getStoreItemID(), xpath.getDataBinding()
						.getXpath(), xpath.getDataBinding()
						.getPrefixMappings());

		return Boolean.parseBoolean(val);		
		
    }
    
	public void listXPaths( List<org.opendope.xpaths.Xpaths.Xpath> theList, 
			Map<String, Condition> conditionsMap,
			Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {
		
		//org.opendope.xpaths.Xpaths.Xpath xpath = XPathsPart.getXPathById(xPaths, id);	
		org.opendope.xpaths.Xpaths.Xpath xpath = xpathsMap.get(id);
    	theList.add(xpath);
		
	}
	
	/**
	 * Map the IDs used in this condition to new values; useful for merging ConditionParts.
	 * 
	 * @param xpathIdMap
	 * @param conditionIdMap
	 * @since 3.0.0
	 */
	public void mapIds(Map<String, String> xpathIdMap, Map<String, String> conditionIdMap) {
		
		if (xpathIdMap==null) return;
		
		String newId = xpathIdMap.get(getId());
		if (newId!=null) {
			setId(newId);
		}
	}
	
	public String toString(Map<String, Condition> conditionsMap,
			Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap) {

		//org.opendope.xpaths.Xpaths.Xpath xpath = XPathsPart.getXPathById(xPaths, id);	
		org.opendope.xpaths.Xpaths.Xpath xpath = xpathsMap.get(id);
		return xpath.getDataBinding().getXpath(); 	
	}
	
	public Condition repeat(String xpathBase,
			int index,
			Map<String, Condition> conditionsMap,
			Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap)	{
		
		// this Xpathref is a clone already, 
		// but it points to the original xpathObj
		
		//org.opendope.xpaths.Xpaths.Xpath xpathObj = XPathsPart.getXPathById(xPaths, id);	
		org.opendope.xpaths.Xpaths.Xpath xpathObj = xpathsMap.get(id);
		String thisXPath = xpathObj.getDataBinding().getXpath();

		if (thisXPath.trim().startsWith("count") ) {
			
			// we are trying to count rows of the repeat eg
			// eg xpath="count(/oda:answers/oda:repeat[@qref='r1_OE']/oda:row[1]/oda:repeat[@qref='r2_d4']/oda:row)&lt;=7"
			// or xpath="count(/oda:answers/oda:repeat[@qref='r1_OE']/oda:row)=999"
			
			// We want to enhance EXCEPT for the deepest repeat.
			int pos = thisXPath.indexOf(xpathBase) + xpathBase.length();
			
			if (thisXPath.substring(pos).contains("oda:repeat")) {
				// There are deeper repeats in thisXPath than xpathBase,
				// so enhance ..
				// NB this code is currently specific to oda:answers XML
			} else {
				System.out.println("retaining: " + thisXPath);
				return null; // ?
			}
		} 
		
		final String newPath = enhanceXPath(xpathBase, index + 1, thisXPath);

		if (log.isDebugEnabled() && !thisXPath.equals(newPath)) {
			log.debug("xpath prefix enhanced " + thisXPath + " to " + newPath);
		}

		// Clone the xpath
		org.opendope.xpaths.Xpaths.Xpath newXPathObj = createNewXPathObject(xpathsMap,
				newPath, xpathObj, index);

		// point this at it
		this.id = newXPathObj.getId();		
		
		return null;
	}	
	
	private org.opendope.xpaths.Xpaths.Xpath createNewXPathObject(Map<String, org.opendope.xpaths.Xpaths.Xpath> xpathsMap,
			String newPath, org.opendope.xpaths.Xpaths.Xpath xpathObj, int index) {
		
//		org.opendope.xpaths.Xpaths.Xpath newXPathObj = XmlUtils.deepCopy(xpathObj);
		org.opendope.xpaths.Xpaths.Xpath newXPathObj = new org.opendope.xpaths.Xpaths.Xpath();		
		
		String newXPathId = xpathObj.getId() + "_" + index;
		newXPathObj.setId(newXPathId);
		
		org.opendope.xpaths.Xpaths.Xpath.DataBinding dataBinding = new org.opendope.xpaths.Xpaths.Xpath.DataBinding();
		newXPathObj.setDataBinding(dataBinding);
		
		dataBinding.setXpath(newPath);
		dataBinding.setStoreItemID(
				xpathObj.getDataBinding().getStoreItemID());
		dataBinding.setPrefixMappings(
				xpathObj.getDataBinding().getPrefixMappings());
				
		//xPaths.getXpath().add(newXPathObj);
		org.opendope.xpaths.Xpaths.Xpath preExistingSanity = xpathsMap.put(newXPathId, newXPathObj); 
		if (preExistingSanity!=null) {
			if (preExistingSanity.getDataBinding().getXpath().equals(newXPathObj.getDataBinding().getXpath())) {
				log.debug("Duplicate identical XPath being added: " + newXPathId);
			} else {
				log.error("Duplicate XPath " + newXPathId + ": "
						+ "\n"+ newXPathObj.getDataBinding().getXpath() + " overwriting "
						+ "\n"+ preExistingSanity.getDataBinding().getXpath());				
			}
		}
		
		return newXPathObj;
	}
	
}
