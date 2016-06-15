package com.graffitab.server.persistence.model.externalprovider;

import javax.persistence.Column;
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
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.graffitab.server.persistence.dao.Identifiable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@NamedQueries({
	@NamedQuery(
		name = "ExternalProvider.findExternalProvider",
		query = "select e "
			  + "from ExternalProvider e "
			  + "where e.externalProviderType = :externalProviderType "
			  + "and e.externalUserId = :externalUserId "
			  + "and e.accessToken = :accessToken"
	),
	@NamedQuery(
		name = "ExternalProvider.findExternalProviderWithoutAccessToken",
		query = "select e "
			  + "from ExternalProvider e "
			  + "where e.externalProviderType = :externalProviderType "
			  + "and e.externalUserId = :externalUserId"
	)
})

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "external_provider")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "external_provider_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ExternalProvider implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "external_provider_type", nullable = false, insertable = false, updatable = false)
	private ExternalProviderType externalProviderType;

	@Column(name = "external_user_id", nullable = false)
	private String externalUserId;

	@Column(name = "access_token", nullable = false)
	private String accessToken;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public ExternalProvider() {

	}

	public ExternalProvider(ExternalProviderType externalProviderType, String externalUserId, String accessToken) {
		this.externalProviderType = externalProviderType;
		this.externalUserId = externalUserId;
		this.accessToken = accessToken;
	}

	public static ExternalProvider provider(ExternalProviderType externalProviderType, String externalUserId, String accessToken) {
		ExternalProvider toAdd = null;
        switch (externalProviderType) {
        	case FACEBOOK:
        		toAdd = new ExternalProviderFacebook(externalUserId, accessToken);
        		break;
        	case TWITTER:
        		toAdd = new ExternalProviderTwitter(externalUserId, accessToken);
        		break;
        	case GOOGLE:
        		toAdd = new ExternalProviderGoogle(externalUserId, accessToken);
        		break;
        }

        return toAdd;
	}
}
