package pl.arimr.springacldemo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import pl.arimr.springacldemo.domain.Wallet;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Wallet> findAll();

    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    Wallet getOne(Long id);

    @SuppressWarnings("unchecked")
    @Override
    @PreAuthorize("hasPermission(#wallet, 'WRITE')")
    Wallet save(@Param("wallet") Wallet wallet);

    @SuppressWarnings("unchecked")
    @Override
    @PreAuthorize("hasPermission(#wallet, 'DELETE')")
    void delete(@Param("wallet") Wallet wallet);

}
