

drop table COLLECTION;

create table COLLECTION(
	COLLECTION_NAME VARCHAR(20) NOT NULL,
	OWNER VARCHAR(255),
	primary key (COLLECTION_NAME, OWNER),
	foreign key (OWNER) references USERTABLE(USERNAME)
);

insert into COLLECTION (COLLECTION_NAME, OWNER)
    values ('My First Collection', 'john');
insert into COLLECTION (COLLECTION_NAME, OWNER)
    values ('My Second Collection', 'john');
