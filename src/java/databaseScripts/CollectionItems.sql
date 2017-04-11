

drop table COLLECTION_ITEMS;

create table COLLECTION_ITEMS(
	ALBUM_ID INT,
	COLLECTION_NAME VARCHAR(20),
        OWNER VARCHAR(255),
	primary key (album_ID, collection_name, owner),
	foreign key (album_ID) references ALBUMTABLE(album_ID),
	foreign key (collection_name) references COLLECTION(collection_name)
        foreign key (owner) references USERTABLE(username)
);

insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME, OWNER)
    values (1, 'My First Collection', 'john');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME, OWNER)
    values (1, 'My First Collection', 'john');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME, OWNER)
    values (1, 'My First Collection', 'john');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME, OWNER)
    values (2, 'My Second Collection', 'john');

-- DELETE FROM collection_items 
-- WHERE COLLECTION_NAME='string nameOfCollection'
--       AND OWNER='string username';
-- 
-- DELETE FROM collection
-- WHERE COLLECTION_NAME='string nameOfCollection'
--       AND OWNER='string username';
-- 
-- DELETE FROM collection
-- WHERE COLLECTION_NAME='My First Collection'
--       AND OWNER='john';
-- 
-- DELETE FROM collection_items 
-- WHERE ALBUM_ID='int Album_ID' AND
--       OWNER='string username' AND
--       COLLECTION_NAME='string nameOfCollection'; 
-- 
-- DELETE FROM collection_items 
-- WHERE ALBUM_ID='1' AND
--       OWNER='john' AND
--       COLLECTION_NAME='My First Collection'; 
-- 