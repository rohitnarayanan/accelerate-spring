package accelerate.spring.cache;

import accelerate.commons.data.DataBean;
import accelerate.spring.CommonTestConstants;

/**
 * {@link DataBean} extension for {@link DataMapCacheTest}
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since August 15, 2019
 */
public class TestDataBean extends DataBean {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * field 1
	 */
	private int beanId;

	/**
	 * field 2
	 */
	private String beanName;

	/**
	 * default constructor for Jackson deserialization
	 */
	public TestDataBean() {
	}

	/**
	 * overloaded constructor
	 * 
	 * @param aBeanId
	 */
	public TestDataBean(int aBeanId) {
		this.beanId = aBeanId;
		this.beanName = CommonTestConstants.BEAN_NAME_VALUE + aBeanId;
	}

	/**
	 * overloaded constructor
	 * 
	 * @param aBeanId
	 * @param aBeanName
	 */
	public TestDataBean(int aBeanId, String aBeanName) {
		this.beanId = aBeanId;
		this.beanName = aBeanName;
	}

	/**
	 * Getter method for "beanId" property
	 * 
	 * @return beanId
	 */
	public int getBeanId() {
		return this.beanId;
	}

	/**
	 * Setter method for "beanId" property
	 * 
	 * @param aBeanId
	 */
	public void setBeanId(int aBeanId) {
		this.beanId = aBeanId;
	}

	/**
	 * Getter method for "beanName" property
	 * 
	 * @return beanName
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Setter method for "beanName" property
	 * 
	 * @param aBeanName
	 */
	public void setBeanName(String aBeanName) {
		this.beanName = aBeanName;
	}
}