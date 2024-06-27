package com.jasper.oauth.oauthdashboard.service;

import com.jasper.oauth.oauthdashboard.dao.AccountDao;
import com.jasper.oauth.oauthdashboard.dao.AccountRoleDao;
import com.jasper.oauth.oauthdashboard.dao.RoleScopeDao;
import com.jasper.oauth.oauthdashboard.dao.ScopeDao;
import com.jasper.oauth.oauthdashboard.entity.Account;
import com.jasper.oauth.oauthdashboard.entity.AccountRole;
import com.jasper.oauth.oauthdashboard.entity.Role;
import com.jasper.oauth.oauthdashboard.entity.Scope;
import com.jasper.oauth.oauthdashboard.model.PageParam;
import com.jasper.oauth.oauthdashboard.model.account.AccountDetailResponse;
import com.jasper.oauth.oauthdashboard.model.account.AccountListResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountRoleService {

}
