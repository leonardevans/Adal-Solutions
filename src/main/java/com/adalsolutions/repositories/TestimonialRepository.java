package com.adalsolutions.repositories;

import com.adalsolutions.models.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialRepository  extends JpaRepository<Testimonial, Integer> {
}
