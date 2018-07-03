-- We have 3 users and one role
insert into acl_sid (id, principal, sid) values
(1, 1, 'tom'),
(2, 1, 'josh'),
(3, 1, 'anna'),
(4, 0, 'ROLE_WALLET_ADMIN');

-- We have one acl restricted entity
insert into acl_class (id, class) values
(1, 'pl.arimr.springacldemo.domain.Wallet');

-- We have 3 wallets
insert into wallet(id,uuid,amount,description) values
(1,'RANDOM_UUID_1', 1000, 'Wallet of Tom'),
(2,'RANDOM_UUID_2', 1000, 'Wallet of Josh'),
(3,'RANDOM_UUID_3', 1000, 'Wallet of Anna');
-- if we add wallet with no rules everybody can use it
--(4,'RANDOM_UUID_4', 1000, 'Free access wallet');

-- Each wallets are owned by ROLE_WALLET_ADMIN
insert into acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) values
(1, 1, 1, null, 4, 0),
(2, 1, 2, null, 4, 0),
(3, 1, 3, null, 4, 0);

-- mask: 1=READ,2=WRITE,3=DELETE,4=ADMINISTER
-- granting: 0=disallow,1=allow
-- ace_order: for acl_object_identity
insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values
-- Tom can read own wallet
(1, 1, 1, 1, 1, 1, 1, 1),
-- Tom can write own wallet
(2, 1, 2, 1, 2, 1, 1, 1),
-- Tom can delete own wallet
(3, 1, 3, 1, 4, 1, 1, 1),
-- Anna can read Tom's wallet
(4, 1, 4, 3, 1, 1, 1, 1),
-- ROLE_WALLET_ADMIN can not read Tom's wallet
(5, 1, 5, 4, 1, 0, 1, 1),
-- ROLE_WALLET_ADMIN can read Tom's wallet
(6, 1, 6, 4, 1, 1, 1, 1),

-- Josh can read own wallet
(7, 2, 1, 2, 1, 1, 1, 1),
-- Josh can write own wallet
(8, 2, 2, 2, 2, 1, 1, 1),
-- ROLE_WALLET_ADMIN can read Josh's wallet
(9, 2, 3, 4, 1, 1, 1, 1),

-- Anna can read own wallet
(10, 3, 1, 3, 1, 1, 1, 1),
-- ROLE_WALLET_ADMIN can read Anna's wallet
(11, 3, 2, 4, 1, 1, 1, 1);

