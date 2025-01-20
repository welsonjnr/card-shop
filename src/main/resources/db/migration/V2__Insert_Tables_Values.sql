INSERT INTO `user` (`id`, `email`, `first_name`, `last_name`, `password`)
VALUES (1, 'admin1@email.com', 'Admin', 'Admin1', '$2a$10$D.9RTtUVZBqnhQCNXKmvZOTmCL2ueWejnYbzJULv68UMBY3DtNLTq'),
       (2, 'admin2@email.com', 'Admin', 'Admin2', '$2a$10$wRvZcvMK/a2My0i.Z0ayne.eMzUcH6loucVP2YM9noEwqu166axNS');

INSERT INTO `category` (`id`, `name`)
VALUES (1, 'Eletronics'),
       (2, 'Gadget'),
       (3, 'Electronics');

INSERT INTO `product` (`id`, `name`, `description`, `brand`, `inventory`, `price`, `category_id`)
VALUES (1, 'TV', 'Apple smart eletronics', 'Apple', 1, 400.00, 1),
       (2, 'Watch', 'Apple smart watch', 'Apple', 5, 50.00, 2),
       (3, 'Refrigerator', 'LG smart electronics', 'LG', 2, 200.00, 3),
       (4, 'TV', 'Samsung smart TV', 'Samsung', 7, 150.00, 3);

INSERT INTO `role`
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN');

INSERT INTO `user_roles`
VALUES (1, 2),
       (2, 2);