package org.jp.web.beans.location;

import java.io.Serializable;

import org.jp.web.beans.MasterBean;

/**
 * encuestame:  system online surveys
 * Copyright (C) 2009  encuestame Development Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Id: UnitTerritoryBean.java Date: 26/05/2009 12:40:46
 * @author juanpicado
 * package: org.jp.web.beans.territory
 * @version 1.0
 */
public class UnitLocationBean extends MasterBean implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2927179009540176488L;
	private String tid;
	private Integer locationTypeId;
	private String descriptionLocation;
	private Integer level;
	private Integer state;
	private String latitude;
	private String longitude;
	private Boolean isGeo;
	/**
	 * @return the tid
	 */
	public String getTid() {
		return tid;
	}
	/**
	 * @param tid the tid to set
	 */
	public void setTid(String tid) {
		this.tid = tid;
	}
	/**
	 * @return the locationTypeId
	 */
	public Integer getLocationTypeId() {
		return locationTypeId;
	}
	/**
	 * @param locationTypeId the locationTypeId to set
	 */
	public void setLocationTypeId(Integer locationTypeId) {
		this.locationTypeId = locationTypeId;
	}
	/**
	 * @return the descriptionLocation
	 */
	public String getDescriptionLocation() {
		return descriptionLocation;
	}
	/**
	 * @param descriptionLocation the descriptionLocation to set
	 */
	public void setDescriptionLocation(String descriptionLocation) {
		this.descriptionLocation = descriptionLocation;
	}
	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}
	/**
	 * @return the state
	 */
	public Integer getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the isGeo
	 */
	public Boolean getIsGeo() {
		return isGeo;
	}
	/**
	 * @param isGeo the isGeo to set
	 */
	public void setIsGeo(Boolean isGeo) {
		this.isGeo = isGeo;
	}
	
	
	
	

}