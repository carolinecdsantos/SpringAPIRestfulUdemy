package com.wallet.controller;

import com.wallet.dto.WalletItemDTO;
import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.response.Response;
import com.wallet.service.WalletItemService;
import com.wallet.util.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("wallet-item")
public class WalletItemController {
    
    @Autowired
    private WalletItemService walletItemService;
    
    @PostMapping
    public ResponseEntity<Response<WalletItemDTO>> create(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {
        Response<WalletItemDTO> response = new Response<WalletItemDTO>();
        
        if (result.hasErrors()) {
            result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        WalletItem walletItem = walletItemService.save(this.convertDtoToEntity(dto));
        response.setData(this.convertEntityToDto(walletItem));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping(value = "/{walletId}")
    public ResponseEntity<Response<Page<WalletItemDTO>>> findBetweenDates(@PathVariable("walletId") Long walletId,
              @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
              @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate,
              @RequestParam(name = "page", defaultValue = "0") int page) {
        Response<Page<WalletItemDTO>> response = new Response<Page<WalletItemDTO>>();
        Page<WalletItem> items = walletItemService.findBetweenDates(walletId, startDate, endDate, page);
        Page<WalletItemDTO> dto = items.map(i -> this.convertEntityToDto(i));
        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }
    
//    @GetMapping(value = "/type/{walletId}")
//    public ResponseEntity<Response<List<WalletItemDTO>>> findByWalletIdAndType(@PathVariable("walletId") Long walletId, @RequestParam("type") String type) {
//        Response<List<WalletItemDTO>> response = new Response<List<WalletItemDTO>>();
//        List<WalletItem> list = walletItemService.findByWalletTypeAndId(walletId, TypeEnum.getEnum(type));
//        
//        List<WalletItemDTO> dto = new ArrayList<>();
//        list.forEach(i -> dto.add(this.convertEntityToDto(i)));
//        response.setData(dto);
//        return ResponseEntity.ok().body(response);
//    }
//    
//    @GetMapping(value = "/total/{walletId}")
//    public ResponseEntity<Response<BigDecimal>> sumByWalletId(@PathVariable("walletId") Long walletId) {
//        Response<BigDecimal> response = new Response<BigDecimal>();
//        BigDecimal value = walletItemService.sumByWalletId(walletId);
//        response.setData(value == null ? BigDecimal.ZERO : value);
//        
//        return ResponseEntity.ok().body(response);
//    }
    
    @PutMapping
    public ResponseEntity<Response<WalletItemDTO>> update(@Valid @RequestBody WalletItemDTO dto, BindingResult result) {
        Response<WalletItemDTO> response = new Response<WalletItemDTO>();
        Optional<WalletItem> walletItem = walletItemService.findById(dto.getId());
        
        if (!walletItem.isPresent()) {
            result.addError(new ObjectError("walletItem", "WalletItem não encontrado"));
        }else {
            if (walletItem.get().getWallet().getId().compareTo(dto.getWallet_id()) != 0 ) {
                result.addError(new ObjectError("WalletItemChanged", "Você não pode alterar a carteira"));
            }
        }
        
        if (result.hasErrors()) {
            result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        WalletItem saved = walletItemService.save(this.convertDtoToEntity(dto));
        
        response.setData(this.convertEntityToDto(saved));
        return ResponseEntity.ok().body(response);
    }
    
    @DeleteMapping(value = "/{walletId}")
    public ResponseEntity<Response<String>> delete(@PathVariable("walletId") Long walletId) {
        Response<String> response = new Response<String>();
        Optional<WalletItem> walletItem = walletItemService.findById(walletId);
        
        if (!walletItem.isPresent()) {
            response.getErrors().add("Carteira de Id" + walletId + "não encontrada");
            
            return ResponseEntity.ok().body(response);
        }
        
        walletItemService.deleteById(walletId);
        response.setData("Carteira " + walletId + "deletada com sucesso");
        
        return ResponseEntity.ok().body(response);
    }
    
    private WalletItem convertDtoToEntity(WalletItemDTO dto) {
         WalletItem walletItem = new WalletItem();
         walletItem.setDate(dto.getDate());
         walletItem.setDescription(dto.getDescription());
         walletItem.setId(dto.getId());
         walletItem.setType(TypeEnum.getEnum(dto.getType()));
         walletItem.setValue(dto.getValue());

         Wallet wallet = new Wallet();
         wallet.setId(dto.getWallet_id());
         walletItem.setWallet(wallet);
         
         return walletItem;
    }
    
    private WalletItemDTO convertEntityToDto(WalletItem walletItem) {
        WalletItemDTO dto = new WalletItemDTO();
        dto.setId(walletItem.getId());
        dto.setDate(walletItem.getDate());
        dto.setDescription(walletItem.getDescription());
        dto.setType(walletItem.getType().getValue());
        dto.setValue(walletItem.getValue());
        Wallet wallet = walletItem.getWallet();
        dto.setWallet_id(wallet.getId());
        
        return dto;
    }
    
}
