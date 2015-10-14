--liquibase formatted sql
--changeset david:v100cs01
ALTER TABLE person MODIFY password varchar(100);

--changeset georgi:v100cs02
ALTER TABLE person MODIFY password varchar(300);
