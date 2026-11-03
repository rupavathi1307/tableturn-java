-- The branch and its tables. Run at every start-up, so every table is created only if missing.
-- A table's state is not stored: it is derived from its current seating (none yet: every table is free).

CREATE TABLE IF NOT EXISTS branches (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS tables (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id          BIGINT      NOT NULL REFERENCES branches (id),
    no                 VARCHAR(8)  NOT NULL,
    seats              INT         NOT NULL,
    section            VARCHAR(10) NOT NULL,
    current_seating_id BIGINT,
    cleared_at         TIMESTAMP WITH TIME ZONE,
    UNIQUE (branch_id, no)
);

-- The waitlist. A party's position is counted when it joins (1 + the parties waiting) and stored.
CREATE TABLE IF NOT EXISTS parties (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT      NOT NULL REFERENCES branches (id),
    name      VARCHAR(60) NOT NULL,
    covers    INT         NOT NULL,
    phone     VARCHAR(16),
    state     VARCHAR(10) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    position  INT         NOT NULL
);
 