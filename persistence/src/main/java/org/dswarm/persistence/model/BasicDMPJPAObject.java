package org.dswarm.persistence.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * An abstract POJO class for entities with a name and where the identifier should be generated by the database.
 * 
 * @author tgaengler
 */
@XmlRootElement
@MappedSuperclass
public abstract class BasicDMPJPAObject extends DMPJPAObject {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * The name of the entity.
	 */
	@Column(name = "NAME")
	private String				name;

	/**
	 * Gets the name of the entity.
	 * 
	 * @return the name of the entity
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name of the entity.
	 * 
	 * @param name the name of the entity
	 */
	public void setName(final String name) {

		this.name = name;
	}

	@Override
	public boolean completeEquals(final Object obj) {

		return BasicDMPJPAObject.class.isInstance(obj) && super.completeEquals(obj) && Objects.equal(((BasicDMPJPAObject) obj).getName(), getName());
	}
}