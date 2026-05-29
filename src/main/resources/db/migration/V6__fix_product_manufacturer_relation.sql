ALTER TABLE products DROP COLUMN IF EXISTS manufacturer_id;

CREATE TABLE IF NOT EXISTS product_manufacturer (
    product_id     INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    manufacturer_id INTEGER NOT NULL REFERENCES manufacturers(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, manufacturer_id)
);
