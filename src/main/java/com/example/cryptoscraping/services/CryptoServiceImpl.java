package com.example.cryptoscraping.services;

import com.example.cryptoscraping.dao.CryptoDAO;
import com.example.cryptoscraping.models.Crypto;
import org.springframework.stereotype.Service;

@Service
public class CryptoServiceImpl implements ICryptoService{
    
    private CryptoDAO dao;

    public CryptoServiceImpl(CryptoDAO dao) {
        this.dao = dao;
    }

    @Override
    public Crypto create(Crypto crypto) {
        return this.dao.save(crypto);
    }
}
