--liquibase formatted sql
--changeset david:v100cs01
ALTER TABLE person MODIFY password varchar(100);

--changeset georgi:v100cs02
ALTER TABLE person MODIFY password varchar(300);

--changeset david:v100cs03
RENAME TABLE person TO gt_user;
ALTER TABLE gt_user CHANGE externalId external_id varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE gt_user CHANGE avatarId avatar_id bigint(20) DEFAULT NULL;
ALTER TABLE gt_user CHANGE coverId cover_id bigint(20) DEFAULT NULL;
ALTER TABLE gt_user ADD CONSTRAINT gt_user_avatar_fk FOREIGN KEY (avatar_id) REFERENCES avatar(id);
ALTER TABLE gt_user ADD CONSTRAINT gt_user_cover_fk FOREIGN KEY (avatar_id) REFERENCES cover(id);

--changeset david:v100cs04
ALTER TABLE avatar CHANGE image image varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL;
ALTER TABLE cover CHANGE image image varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL;

--changeset lucia:v100cs05
ALTER TABLE avatar DROP FOREIGN KEY del_avatar_on_person;
ALTER TABLE avatar DROP COLUMN userId;
ALTER TABLE cover DROP FOREIGN KEY del_cover_on_person;
ALTER TABLE cover DROP COLUMN userId;

--changeset lucia:v100cs06
ALTER TABLE graffiti DROP FOREIGN KEY del_graffiti_on_tag;
ALTER TABLE graffiti DROP COLUMN tagId;

--changeset georgi:v100cs07
alter table gt_user drop foreign key gt_user_avatar_fk;
alter table gt_user drop foreign key gt_user_cover_fk;
DROP TABLE avatar;
DROP TABLE cover;
CREATE TABLE asset (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	guid varchar(40) NOT NULL,
	asset_type varchar(20) NOT NULL,
	user_id bigint(20) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT asset_user_fk FOREIGN KEY (user_id) REFERENCES gt_user (id)
)

--changeset georgi:v100cs08
alter table asset add order_key int(11) not null;

--changeset georgi:v100cs09
alter table gt_user add guid varchar(40) NOT NULL;

--changeset david:v100cs10
ALTER TABLE following DROP FOREIGN KEY del_following_on_person_1;
ALTER TABLE following DROP FOREIGN KEY del_following_on_person_2;
ALTER TABLE following CHANGE followingId following_id bigint(20) not null;
ALTER TABLE following CHANGE userId user_id bigint(20) not null;
ALTER TABLE following ADD CONSTRAINT user_following_fk1 FOREIGN KEY (following_id) REFERENCES gt_user(id);
ALTER TABLE following ADD CONSTRAINT user_following_fk2 FOREIGN KEY (user_id) REFERENCES gt_user(id);

--changeset georgi:v100cs11
alter table gt_user add status varchar(50) not null;
CREATE TABLE gt_user_metadata (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	user_id bigint(20) NOT NULL,
	metadata_key varchar(50) NOT NULL,
	metadata_value varchar(300) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT gt_user_metadata_fk FOREIGN KEY (user_id) REFERENCES gt_user (id)
)

--changeset georgi:v100cs12
ALTER TABLE gt_user DROP COLUMN avatar_id;
ALTER TABLE gt_user DROP COLUMN cover_id;

--changeset georgi:v100cs13
ALTER TABLE gt_user DROP COLUMN external_id;

--changeset georgi:v100cs14
ALTER TABLE device DROP FOREIGN KEY del_device_on_person;
ALTER TABLE device CHANGE userId user_id bigint(20) NOT NULL;
ALTER TABLE device MODIFY token varchar(1000) NOT NULL;
ALTER TABLE device CHANGE os os_type varchar(11) NOT NULL;

--changeset georgi:v100cs15
ALTER TABLE device ADD CONSTRAINT device_user_fk FOREIGN KEY (user_id) REFERENCES gt_user (id)

--changeset georgi:v100cs16
alter table device add order_key int(11) not null;