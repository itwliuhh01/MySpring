package com.lagou.edu.service.impl;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;

/**
 * @author 应癫
 */
@Service("transferService")
public class TransferServiceImpl implements TransferService {

//    private AccountDao accountDao = (AccountDao)BeanFactory.getInstance().getBean("jdbcAccountDaoImpl");

    @Autowired
    private AccountDao accountDao1;

    public AccountDao getAccountDao1() {
        return accountDao1;
    }

    public void setAccountDao1(AccountDao accountDao1) {
        this.accountDao1 = accountDao1;
    }

    @Override
    @Transactional
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
            Account from = accountDao1.queryAccountByCardNo(fromCardNo);
            Account to = accountDao1.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney() - money);
            to.setMoney(to.getMoney() + money);

            accountDao1.updateAccountByCardNo(to);
            int i = 1 / 0;
            accountDao1.updateAccountByCardNo(from);
    }
}
