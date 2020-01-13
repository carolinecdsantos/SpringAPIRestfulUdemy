package com.wallet.repository;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WalletItemRepositoryTest {
    
    private static final Date DATE = new Date(); 
    private static final TypeEnum TYPE = TypeEnum.EN;
    private static final BigDecimal VALUE = BigDecimal.valueOf(360);
    private static final String DESCRIPTION = "Descrição teste";
    
    private Long walletId = null;
    private Long walletItemId = null;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletItemRepository walletItemRepository;
    
    @Before
    public void initEach() {
        Wallet wallet = new Wallet();
        wallet.setValue(BigDecimal.valueOf(250));
        wallet.setName("OK");

        walletRepository.save(wallet);

        WalletItem walletItem = new WalletItem();
        walletItem.setDate(new Date());
        walletItem.setDescription("Description");
        walletItem.setType(TypeEnum.EN);
        walletItem.setValue(BigDecimal.valueOf(500));
        walletItem.setWallet(wallet);
        WalletItem walletItem1 = walletItemRepository.save(walletItem);

        walletItemId = walletItem1.getId();
        walletId = wallet.getId();
    }

    @After
    public void tearDown() {
        walletItemRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    public void testSave() {
        Wallet wallet2 = new Wallet();
        wallet2.setValue(BigDecimal.valueOf(2500));
        wallet2.setName("OK2");
        Wallet walletResponse = walletRepository.save(wallet2);
        
        WalletItem walletItem2 = new WalletItem(null, DATE, TypeEnum.EN, DESCRIPTION, VALUE, walletResponse);
        WalletItem response = walletItemRepository.save(walletItem2);
        
        assertNotNull(response);
        assertEquals(response.getDescription(), DESCRIPTION);
        assertEquals(response.getValue(), VALUE);
        assertEquals(response.getDate(), DATE);
        assertEquals(response.getType(), TYPE);
    }
    
    @Test(expected = TransactionSystemException.class)
    public void testSaveInvalidWalletItem() {
        WalletItem walletItem2 = new WalletItem(null, DATE, null, DESCRIPTION, null, null);
        walletItemRepository.save(walletItem2);
    }
    
    @Test
    public void testUpdateWalletItem() {
        Optional<WalletItem> walletItem2 = walletItemRepository.findById(walletItemId);
        
        if (walletItem2.isPresent()) {
            String description = "Descrição alterada";
            WalletItem walletItemChanged = walletItem2.get();
            walletItemChanged.setDescription(description);

            walletItemRepository.save(walletItemChanged);

            Optional<WalletItem> newWalletItem = walletItemRepository.findById(walletItemId);
            assertEquals(description, newWalletItem.get().getDescription());
        }
        
    }
    
    @Test
    public void testDeleteWalletItem() {
        Optional<WalletItem> walletItem2 = walletItemRepository.findById(walletItemId);
        
        if (walletItem2.isPresent()) {
            walletItemRepository.deleteById(walletItemId);
            Optional<WalletItem> response = walletItemRepository.findById(walletItemId);
            assertEquals(response, Optional.empty());
        }
    }
    
    @Test
    public void testFindBetweenDates() {
        Optional<Wallet> wallet1 = walletRepository.findById(walletId);

        LocalDateTime localDateTime = DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        Date currentDatePlusFiveDays = Date.from(localDateTime.plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
        Date currentDatePlusSevenDays = Date.from(localDateTime.plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        
        walletItemRepository.save(new WalletItem(null, currentDatePlusFiveDays, TYPE, DESCRIPTION, VALUE, wallet1.get()));
        walletItemRepository.save(new WalletItem(null, currentDatePlusSevenDays, TYPE, DESCRIPTION, VALUE, wallet1.get()));

        
        Page<WalletItem> response = walletItemRepository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(walletId, DATE, currentDatePlusFiveDays, PageRequest.of(0,10));
        
        assertEquals(response.getContent().size(), 2);
        assertEquals(response.getTotalElements(), 2);
        assertEquals(response.getContent().get(0).getWallet().getId(), walletId);
    }
    
    @Test
    public void testFindByType() {
        List<WalletItem> response = walletItemRepository.findByWalletIdAndType(walletId, TYPE);
        
        assertEquals(response.size(), 1);
        assertEquals(response.get(0).getType(), TYPE);
    }
    
    @Test 
    public void testSumByWallet()
    {
        Optional<Wallet> walletRequest = walletRepository.findById(walletId);
        if (walletRequest.isPresent()) {
            WalletItem newWalletItem = new WalletItem(null, DATE, TYPE, "Carteira2", BigDecimal.valueOf(60), walletRequest.get());
            walletItemRepository.save(newWalletItem);
            BigDecimal sumWalletResponse = walletItemRepository.sumByWalletId(walletRequest.get().getId());
            assertEquals(sumWalletResponse.compareTo(BigDecimal.valueOf(560)),0);
        }
    }

}
