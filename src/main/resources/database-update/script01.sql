--liquibase formatted sql
--changeset david:v100cs01
ALTER TABLE person MODIFY password varchar(100);

--changeset georgi:v100cs02
ALTER TABLE person MODIFY password varchar(300);

--changeset david:v100cs03
RENAME TABLE person TO gt_user;
ALTER TABLE gt_user ADD CONSTRAINT gt_user_avatar_fk FOREIGN KEY (avatarId) REFERENCES avatar(id);
ALTER TABLE gt_user ADD CONSTRAINT gt_user_cover_fk FOREIGN KEY (coverId) REFERENCES cover(id);
ALTER TABLE gt_user CHANGE externalId external_id varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE gt_user CHANGE avatarId avatar_id bigint(20) DEFAULT NULL;
ALTER TABLE gt_user CHANGE coverId cover_id bigint(20) DEFAULT NULL;

--changeset david:v100cs04
ALTER TABLE avatar CHANGE image image varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE cover CHANGE image image varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL;