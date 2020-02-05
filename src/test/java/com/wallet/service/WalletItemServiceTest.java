package com.wallet.service;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.repository.WalletItemRepository;
import com.wallet.util.enums.TypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WalletItemServiceTest {
    
    @MockBean
    WalletItemRepository repository;
    
    @Autowired
    WalletItemService walletItemService;
    
    private static final Date DATE = new Date();
    private static final TypeEnum TYPE = TypeEnum.EN;
    private static final String DESCRIPTION = "Conta de luz";
    private static final BigDecimal VALUE = BigDecimal.valueOf(65);
    
    @Test
    public void testSave() {
        BDDMockito.given(repository.save(Mockito.any(WalletItem.class))).willReturn(getMockWalletItem());
        WalletItem response = walletItemService.save(new WalletItem());
        
        assertNotNull(response);
        assertEquals(response.getDescription(), DESCRIPTION);
        assertEquals(response.getValue().compareTo(VALUE), 0);
    }
    
    @Test
    public void testFindBetweenDates() {
        List<WalletItem> list = new ArrayList<>();
        list.add(getMockWalletItem());
        Page<WalletItem> page = new PageImpl(list);
        
        BDDMockito.given(repository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(Mockito.anyLong(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(PageRequest.class))).willReturn(page);
        Page<WalletItem> response = walletItemService.findBetweenDates(1L, new Date(), new Date(), 0);
        
        assertNotNull(response);
        assertEquals(response.getContent().size(), 1);
        assertEquals(response.getContent().get(0).getDescription(), DESCRIPTION);
    }
    
    private WalletItem getMockWalletItem() {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        WalletItem walletItem = new WalletItem(1L, DATE, TYPE, DESCRIPTION, VALUE, wallet);
        
        return walletItem;
    }
}
