package com.example.cryptoscraping.dao;

import com.example.cryptoscraping.models.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoDAO extends JpaRepository<Crypto, Long> {
    Crypto findByName(String name);
}
