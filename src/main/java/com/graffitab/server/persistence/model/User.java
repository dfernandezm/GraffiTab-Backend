package com.graffitab.server.persistence.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.graffitab.server.persistence.dao.Identifiable;

/**
 * Created by david.
 */
@Getter
@Setter
@Entity
@Table(name="gt_user")
public class User implements Identifiable<Long>, UserDetails {

	public enum AccountStatus {
		PENDING_ACTIVATION, ACTIVE, SUSPENDED, RESET_PASSWORD;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "guid", nullable = false)
	private String guid;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "firstname", nullable = false)
	private String firstName;

	@Column(name = "lastname", nullable = false)
	private String lastName;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "website", nullable = true)
	private String website;

	@Column(name = "about", nullable = true)
	private String about;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AccountStatus accountStatus = AccountStatus.PENDING_ACTIVATION;

	@Transient
	private Boolean followedByCurrentUser = Boolean.FALSE;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "following",
			   joinColumns = {@JoinColumn(name = "following_id")},
			   inverseJoinColumns = {@JoinColumn(name = "user_id")})
	@OrderColumn(name = "order_key")
	private List<User> followers = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "following",
			   joinColumns = {@JoinColumn(name = "user_id")},
			   inverseJoinColumns = {@JoinColumn(name = "following_id")})
	@OrderColumn(name = "order_key")
	private List<User> following = new ArrayList<>();

	@OneToMany(targetEntity = Device.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id")
	@OrderColumn(name = "order_key")
	private List<Asset> assets = new ArrayList<>();

	@OneToMany(targetEntity = Device.class, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id")
	@OrderColumn(name = "order_key")
	private List<Device> devices = new ArrayList<>();

	@ElementCollection
    @MapKeyColumn(name = "metadata_key")
    @Column(name="metadata_value")
    @CollectionTable(name="gt_user_metadata", joinColumns = @JoinColumn(name="user_id"))
	private Map<String, String> metadataItems = new HashMap<>();


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority g = new SimpleGrantedAuthority("ROLE_USER");

		return Collections.singletonList(g);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * @param followee
	 * @return true if the current user is following the specified user
	 */
	public boolean isFollowing(User followee) {
		return following.contains(followee);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
