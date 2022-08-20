package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.model.dto.AccountDto;
import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.exception.AccountExistedException;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.service.AccountService;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

  private final AccountRepository accountRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account user = accountRepository.findAccountByPhone(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("Account not found with username: " + username));

    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

    return new User(user.getPhone(), user.getPassword(), authorities);
  }

  @Override
  public Account findAccountByPhone(String phone) {
    return accountRepository.findAccountByPhone(phone)
        .orElseThrow(() -> new AccountNotFoundException("Account not found", phone));
  }

  @Override
  public String saveAccount(AccountDto accountDto) {
    if (accountRepository.existsByPhone(accountDto.getPhone())) {
      throw new AccountExistedException("Account with phone already existed",
          accountDto.getPhone(), accountDto.getRole());
    }

    Account account = new Account();
    account.setPhone(accountDto.getPhone());
    account.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
    account.setRole(accountDto.getRole());

    return accountRepository.save(account).getId();
  }

  @Override
  public void saveAccount(Account account) {
    accountRepository.save(account);
  }

  @Override
  public String updatePasswordById(String id, AccountDto accountDto) {
    Account account = accountRepository
        .findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Account not found", id));
    account.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
    return accountRepository.save(account).getId();
  }
}
