package com.wallet.service;

import com.wallet.entity.WalletItem;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

public interface WalletItemService {
    WalletItem save(WalletItem walletItem);
    Page<WalletItem> findBetweenDates(Long wallet, Date start, Date end, int page);
    Optional<WalletItem> findById(Long id);
    void deleteById(Long id);
}
