package com.graffitab.server.persistence.model.streamable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.util.BooleanToStringConverter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "streamable")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "streamable_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Streamable implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum StreamableType {
		GRAFFITI
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "date", nullable = false)
	private Long date;

	@Enumerated(EnumType.STRING)
	@Column(name = "streamable_type", nullable = false, insertable = false, updatable = false)
	private StreamableType streamableType;

	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "is_private", nullable = false)
	private Boolean isPrivate;

	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "is_flagged", nullable = false)
	private Boolean isFlagged;

	@OneToOne(targetEntity = Asset.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "asset_id")
	private Asset asset;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Streamable() {

	}

	public Streamable(StreamableType streamableType) {
		this.streamableType = streamableType;
		this.date = System.currentTimeMillis();
	}
}
