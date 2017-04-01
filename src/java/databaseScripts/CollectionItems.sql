

drop table COLLECTION_ITEMS;

create table COLLECTION_ITEMS(
	ALBUM_ID INT,
	COLLECTION_NAME VARCHAR(20),
	primary key (album_ID, collection_name),
	foreign key (album_ID) references ALBUMTABLE(album_ID),
	foreign key (collection_name) references COLLECTION(collection_name)
);

insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (1, 'My First Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (2, 'My Second Collection');