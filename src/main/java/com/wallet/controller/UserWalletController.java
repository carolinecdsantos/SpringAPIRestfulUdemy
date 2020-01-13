package com.wallet.controller;

import com.wallet.dto.UserWalletDTO;
import com.wallet.entity.User;
import com.wallet.entity.UserWallet;
import com.wallet.entity.Wallet;
import com.wallet.response.Response;
import com.wallet.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("user-wallet")
public class UserWalletController {

    @Autowired
    private UserWalletService userWalletService;

    @PostMapping
    public ResponseEntity<Response<UserWalletDTO>> create(@Valid @RequestBody UserWalletDTO userWalletDTO, BindingResult result) {
        Response<UserWalletDTO> response = new Response<UserWalletDTO>();

        if (result.hasErrors()) {
            result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));

            return ResponseEntity.badRequest().body(response);
        }

        UserWallet userWallet = userWalletService.save(this.convertDtoToEntity(userWalletDTO));
        response.setData(this.convertEntityToDto(userWallet));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private UserWalletDTO convertEntityToDto(UserWallet userWallet) {
        UserWalletDTO userWalletDTO = new UserWalletDTO();
        userWalletDTO.setId(userWallet.getId());
        userWalletDTO.setUser_id(userWallet.getUser().getId());
        userWalletDTO.setWallet_id(userWallet.getWallet().getId());

        return userWalletDTO;
    }

    private UserWallet convertDtoToEntity(UserWalletDTO userWalletDTO) {
        UserWallet userWallet = new UserWallet();
        User user = new User();
        Wallet wallet = new Wallet();

        userWallet.setId(userWalletDTO.getId());
        user.setId(userWalletDTO.getUser_id());
        userWallet.setUser(user);
        wallet.setId(userWalletDTO.getWallet_id());
        userWallet.setWallet(wallet);

        return userWallet;
    }
}
