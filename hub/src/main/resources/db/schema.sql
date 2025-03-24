CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS p_company;
DROP TABLE IF EXISTS p_hub;

CREATE TABLE p_hub (
   id UUID NOT NULL DEFAULT uuid_generate_v4(),
   name VARCHAR(100) NOT NULL,
   address VARCHAR(100) NOT NULL,
   latitude DOUBLE PRECISION NOT NULL,
   longitude DOUBLE PRECISION NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   created_by VARCHAR(50) NOT NULL,
   updated_at TIMESTAMP,
   updated_by VARCHAR(50),
   deleted_at TIMESTAMP,
   deleted_by VARCHAR(50),
   PRIMARY KEY (id)
);

CREATE TABLE p_company (
   id UUID NOT NULL DEFAULT uuid_generate_v4(),
   hub_id UUID NOT NULL,
   name VARCHAR(255) NOT NULL,
   address VARCHAR(255) NOT NULL,
   type VARCHAR(50) NOT NULL,
   username VARCHAR(50) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   created_by VARCHAR(50) NOT NULL,
   updated_at TIMESTAMP,
   updated_by VARCHAR(50),
   deleted_at TIMESTAMP,
   deleted_by VARCHAR(50),
   FOREIGN KEY (hub_id) REFERENCES p_hub(id),
   PRIMARY KEY (id)
);