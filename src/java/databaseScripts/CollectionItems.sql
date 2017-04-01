

drop table COLLECTION_ITEMS;

create table COLLECTION_ITEMS(
	album_ID INT,
	collection_name VARCHAR(20),
	primary key (album_ID, collection_name),
	foreign key (album_ID) references ALBUMTABLE(album_ID),
	foreign key (collection_name) references COLLECTION(collection_name)
);