package com.hcmus.wiberback.repository;

import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.CallCenter;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallCenterRepository extends MongoRepository<CallCenter, String> {

  Optional<CallCenter> findCallCenterByAccount(Account account);

  boolean existsByAccount(Account account);
}
