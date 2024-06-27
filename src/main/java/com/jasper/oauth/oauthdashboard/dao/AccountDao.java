package com.jasper.oauth.oauthdashboard.dao;

import com.jasper.oauth.oauthdashboard.entity.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDao extends JpaRepository<Account, Integer> {
  Optional<Account> findByUserName(String userName);

  List<Account> findAllByAdditionalScopesLike(String scopeLikeString);
}
