CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE product
(
    id                 INTEGER          NOT NULL,
    name               VARCHAR(255),
    description        VARCHAR(255),
    available_quantity DOUBLE PRECISION NOT NULL,
    price              DECIMAL,
    category_id        INTEGER,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);