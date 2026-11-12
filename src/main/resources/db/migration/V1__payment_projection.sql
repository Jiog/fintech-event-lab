CREATE TABLE payment_projection (
    payment_id UUID PRIMARY KEY,
    amount NUMERIC(19, 4) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('EUR', 'USD', 'GBP')),
    occurred_at TIMESTAMPTZ NOT NULL,
    projected_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    kafka_partition INTEGER NOT NULL,
    kafka_offset BIGINT NOT NULL
);
