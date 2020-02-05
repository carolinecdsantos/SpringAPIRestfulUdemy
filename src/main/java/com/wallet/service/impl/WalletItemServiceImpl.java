package com.wallet.service.impl;

import com.wallet.entity.WalletItem;
import com.wallet.repository.WalletItemRepository;
import com.wallet.service.WalletItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class WalletItemServiceImpl implements WalletItemService {
    @Autowired
    WalletItemRepository repository;
    
    @Value("${pagination.items_per_page}")
    private int itemsPerPage;
    
    @Override
    public WalletItem save(WalletItem walletItem) {
        return repository.save(walletItem);    
    }

    @Override
    public Page<WalletItem> findBetweenDates(Long wallet, Date start, Date end, int page) {
        @SuppressWarnings("deprecation")
        PageRequest pageRequest = PageRequest.of(page, itemsPerPage);
        
        return repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(wallet, start, end, pageRequest);
    }

    @Override
    public Optional<WalletItem> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
