--liquibase formatted sql
--changeset david:v100cs01
ALTER TABLE person MODIFY password varchar(100);

