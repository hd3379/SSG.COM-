insert into product(product_number, name, price)
values ('1000000001', '이마트 생수', 800),
       ('1000000002',  '신라면 멀티팩', 4200),
       ('1000000003',  '바나나 한 송이', 3500),
       ('1000000004',  '삼겹살 500g', 12000),
       ('1000000005',  '오리온 초코파이', 3000);

insert into sale(product_number, sale_price)
values ('1000000001', 100),
       ('1000000002',  500),
       ('1000000003',  2000),
       ('1000000004',  300),
       ('1000000005',  400);

insert into stock(product_number, quantity)
values ('1000000001', 1000),
       ('1000000002',  500),
       ('1000000003',  200),
       ('1000000004',  100),
       ('1000000005',  300);