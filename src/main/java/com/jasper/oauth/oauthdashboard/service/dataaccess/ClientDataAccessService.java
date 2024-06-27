package com.jasper.oauth.oauthdashboard.service.dataaccess;

import com.jasper.oauth.oauthdashboard.dao.ClientDao;
import com.jasper.oauth.oauthdashboard.entity.OauthClient;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** Define the data access function for client entity interact with the database by ClientDao */
@Service
@Slf4j
public class ClientDataAccessService {
  private final ClientDao clientDao;

  public ClientDataAccessService(ClientDao clientDao) {
    this.clientDao = clientDao;
  }

  // Get all clients from the database
  public Page<OauthClient> findAll(Pageable pageable) {
    return clientDao.findAll(pageable);
  }

  public Optional<OauthClient> findById(Integer id) {
    return clientDao.findById(id);
  }

  public Optional<OauthClient> findByClientCode(String code) {
    return clientDao.findByClientCode(code);
  }

  public OauthClient save(OauthClient client) {
    return clientDao.save(client);
  }

  public void deleteById(Integer id) {
    clientDao.deleteById(id);
  }

  public List<OauthClient> findAllByClientIdIn(Collection<Integer> clientIdList) {
    return clientDao.findAllByIdIn(clientIdList);
  }
}
