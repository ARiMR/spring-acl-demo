package pl.arimr.springacldemo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.arimr.springacldemo.domain.Wallet;
import pl.arimr.springacldemo.repositories.WalletRepository;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SpringAclDemoApplicationTests {

    private static Long TOM_WALLET_ID = 1L;
    private static Long JOSH_WALLET_ID = 2L;
    private static Long ANNA_WALLET_ID = 3L;

    private static String TOM_WALLET_DESC = "Wallet of Tom";
    private static String JOSH_WALLET_DESC = "Wallet of Josh";
    private static String ANNA_WALLET_DESC = "Wallet of Anna";

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MutableAclService aclService;

    @Test
    @WithMockUser(username = "tom")
    public void tom_can_read_own_wallet() {
        List<Wallet> wallets = walletRepository.findAll();
        Assert.assertEquals(1L, wallets.size());
        Assert.assertEquals(TOM_WALLET_DESC, wallets.get(0).getDescription());
    }

    @Test
    @WithMockUser(username = "tom")
    public void tom_can_edit_own_wallet() {
        Wallet wallet = walletRepository.getOne(TOM_WALLET_ID);
        Assert.assertEquals(TOM_WALLET_DESC, wallet.getDescription());
        wallet.setAmount(BigDecimal.valueOf(900L));
        walletRepository.save(wallet);
    }

    @Test
    @WithMockUser(username = "josh")
    public void josh_can_read_own_wallet() {
        List<Wallet> wallets = walletRepository.findAll();
        Assert.assertEquals(1L, wallets.size());
        Assert.assertEquals(JOSH_WALLET_DESC, wallets.get(0).getDescription());
    }

    @Test
    @WithMockUser(username = "anna")
    public void anna_can_read_own_wallet_and_toms_wallet() {
        List<Wallet> wallets = walletRepository.findAll();
        Assert.assertEquals(2L, wallets.size());
    }

    @Test
    @WithMockUser(roles = {"WALLET_ADMIN"})
    public void admin_role_can_read_two_of_three_wallets() {
        List<Wallet> wallets = walletRepository.findAll();
        Assert.assertEquals(2L, wallets.size());
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "anna")
    public void anna_can_not_edit_own_wallet() {
        Wallet wallet = walletRepository.getOne(ANNA_WALLET_ID);
        Assert.assertEquals(ANNA_WALLET_DESC, wallet.getDescription());
        wallet.setAmount(BigDecimal.valueOf(900L));
        walletRepository.save(wallet);
    }

    @Test
    @WithMockUser(username = "josh")
    public void josh_can_edit_own_wallet() {
        Wallet wallet = walletRepository.getOne(JOSH_WALLET_ID);
        Assert.assertEquals(JOSH_WALLET_DESC, wallet.getDescription());
        wallet.setAmount(BigDecimal.valueOf(900L));
        walletRepository.save(wallet);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(username = "josh")
    public void josh_can_not_delete_own_wallet() {
        Wallet wallet = walletRepository.getOne(JOSH_WALLET_ID);
        Assert.assertEquals(JOSH_WALLET_DESC, wallet.getDescription());
        walletRepository.delete(wallet);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void admin_role_use_acl_service() {
        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(Wallet.class.getCanonicalName(), JOSH_WALLET_ID);

        MutableAcl acl = (MutableAcl) aclService.readAclById(objectIdentity);
        Assert.assertNotNull(acl);
        Assert.assertEquals(acl.getEntries().size(),3);

        acl.insertAce(3, BasePermission.WRITE,new PrincipalSid("anna"),true);
        aclService.updateAcl(acl);

        acl = (MutableAcl) aclService.readAclById(objectIdentity);
        Assert.assertNotNull(acl);
        Assert.assertEquals(acl.getEntries().size(),4);
    }

}
