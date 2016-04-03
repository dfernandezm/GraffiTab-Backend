package com.graffitab.server.persistence.model.user;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.joda.time.DateTime;

import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.util.DateTimeToLongConverter;

@NamedQueries({
	@NamedQuery(
		name = "UserSession.findSessionsByUser",
		query = "select us from UserSession us where us.user = :user"
	),
	@NamedQuery(
		name = "UserSession.deleteSession",
		query = "delete "
			  + "from UserSession us "
			  + "where sessionId = :sessionId"
	)
})

@Getter
@Setter
@Entity
@Table(name="session")
public class UserSession implements Identifiable<Long>, Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "session_id", nullable = false)
	private String sessionId;

	@Column(name = "content", nullable = true, columnDefinition = "BLOB")
	@Lob @Basic
    private byte[] content;

	@Convert(converter = DateTimeToLongConverter.class)
	@Column(name = "created_on", nullable = false)
	private DateTime createdOn;

	@ManyToOne
    private User user;

	@Version
    private Integer version;
}
