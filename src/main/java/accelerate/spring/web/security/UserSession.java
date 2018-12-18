package accelerate.spring.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import accelerate.spring.web.beans.WebSession;

/**
 * {@link UserDetails} implementation
 *
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since December 11, 2017
 */
public class UserSession extends WebSession implements UserDetails {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link Logger} instance
	 */
	protected static final Logger _logger = LoggerFactory.getLogger(UserSession.class);

	/**
	 * {@link List} of authorities assigned to the user
	 */
	private List<GrantedAuthority> authorities = null;

	/**
	 * Flag to determine if account is disabled
	 */
	private boolean accountDisabled = false;

	/**
	 * Flag to determine if account is expired
	 */
	private boolean accountExpired = false;

	/**
	 * Flag to determine if account is locked
	 */
	private boolean accountLocked = false;

	/**
	 * Flag to determine if credentials are expired
	 */
	private boolean credentialsExpired = false;

	/**
	 * Default Constructor
	 */
	public UserSession() {
		this.authorities = new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getAuthorities( )
	 */
	/**
	 * @return
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	/**
	 * @return
	 */
	@Override
	public String getPassword() {
		return super.getPassword();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	/**
	 * @return
	 */
	@Override
	public String getUsername() {
		return super.getUsername();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isAccountNonExpired()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isAccountNonExpired() {
		return !this.accountExpired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isAccountNonLocked()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isAccountNonLocked() {
		return !this.accountLocked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isCredentialsNonExpired()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return !this.credentialsExpired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	/**
	 * @return
	 */
	@Override
	public boolean isEnabled() {
		return !this.accountDisabled;
	}

	/**
	 * Setter method for "authorities" property
	 * 
	 * @param aAuthorities
	 */
	public void setAuthorities(List<GrantedAuthority> aAuthorities) {
		this.authorities = aAuthorities;
	}

	/**
	 * Setter method for "accountExpired" property
	 * 
	 * @param aAccountExpired
	 */
	public void setAccountExpired(boolean aAccountExpired) {
		this.accountExpired = aAccountExpired;
	}

	/**
	 * Setter method for "accountLocked" property
	 * 
	 * @param aAccountLocked
	 */
	public void setAccountLocked(boolean aAccountLocked) {
		this.accountLocked = aAccountLocked;
	}

	/**
	 * Setter method for "credentialsExpired" property
	 * 
	 * @param aCredentialsExpired
	 */
	public void setCredentialsExpired(boolean aCredentialsExpired) {
		this.credentialsExpired = aCredentialsExpired;
	}

	/**
	 * Setter method for "accountDisabled" property
	 * 
	 * @param aAccountDisabled
	 */
	public void setAccountDisabled(boolean aAccountDisabled) {
		this.accountDisabled = aAccountDisabled;
	}
}