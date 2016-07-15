package com.jesusviloriag.webmediacenter.repository;

import com.jesusviloriag.webmediacenter.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
