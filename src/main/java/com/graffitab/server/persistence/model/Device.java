package com.graffitab.server.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.graffitab.server.persistence.dao.Identifiable;

@NamedQueries({
	@NamedQuery(
		name = "Device.findDevicesWithToken",
		query = "select d from Device d where d.token = :token and d.osType = :osType"
	)
})

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "device")
public class Device implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum OSType {
		IOS, ANDROID;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "token", nullable = false)
	private String token;

	@Enumerated(EnumType.STRING)
	@Column(name = "os_type", nullable = false)
	private OSType osType;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public static Device device(OSType osType, String token) {
		Device device = new Device();
		device.setToken(token);
		device.setOsType(osType);
		return device;
	}
}
