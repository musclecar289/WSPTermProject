/**
 * Author:  Caleb Dunham
 * Created: Mar 31, 2017
 */

drop table COLLECTION_ITEMS;
drop table ALBUM_TRACKS;
drop table ALBUMTABLE;
drop table COLLECTION;
drop table GROUPTABLE;
drop table USERTABLE;

--------------------------------Credentials.sql---------------------------------
create table USERTABLE (
    USERNAME varchar(255) UNIQUE NOT NULL,
    PASSWORD char(64),                      /* SHA-256 encryption */
    EMAIL varchar(255) UNIQUE,
    primary key (USERNAME)
);

create table GROUPTABLE (
    ID INT NOT NULL AUTO_INCREMENT,
    GROUPNAME varchar(255),
    USERNAME varchar(255),
    primary key (id)
);

insert into USERTABLE (username, email, password)
    values ('root', 'root@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, username) values ('admingroup', 'root');
insert into GROUPTABLE (groupname, username) values ('customergroup', 'root');
insert into USERTABLE (username, email, password)
    values ('admin', 'admin@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, username) values ('admingroup', 'admin');
insert into USERTABLE (username, email, password)
    values ('john','john@uco.edu',
        'c4289629b08bc4d61411aaa6d6d4a0c3c5f8c1e848e282976e29b6bed5aeedc7');
insert into GROUPTABLE (groupname, username) values ('customergroup', 'john');




-----------------------------------Album.sql------------------------------------
create table ALBUMTABLE (
    ALBUM_ID INT NOT NULL AUTO_INCREMENT,
    TITLE varchar(255),
    ARTIST varchar(255),
    YEAR INT,
    NUMBER_OF_TRACKS INT,
    NUMBER_OF_DISCS INT,
    GENRE varchar(255),
    ALBUMCOUNT INT,
    primary key (ALBUM_ID)
);

insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Paranoid', 'Black Sabbath', 1970, 8, 1, 'Heavy metal', 2);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Rising', 'Rainbow', 1976, 6, 1, 'Heavy metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Led Zeppelin', 'Led Zeppelin', 1969, 9, 1, 'Hard rock', 3);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Obscured by Clouds', 'Pink Floyd', 1972, 10, 1, 'Progressive Rock', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('The Dark Side of the Moon', 'Pink Floyd', 1973, 10, 1, 'Progressive Rock', 2);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Wish You Were Here', 'Pink Floyd', 1975, 5, 1, 'Progressive Rock', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('Animals', 'Pink Floyd', 1976, 5, 1, 'Progressive Rock', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
    values ('The Wall', 'Pink Floyd', 1979, 26, 2, 'Heavy metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Ride the Lightning', 'Metallica', 1984, 8, 1, 'Thrash metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Master of Puppets', 'Metallica', 1986, 8, 1, 'Thrash metal', 2);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('...And Justice for All', 'Metallica', 1988, 9, 1, 'Thrash metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Metallica', 'Metallica', 1991, 12, 1, 'Thrash metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Load', 'Metallica', 1996, 14, 1, 'Heavy metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Reload', 'Metallica', 1997, 13, 1, 'Heavy metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('Garage Inc.', 'Metallica', 1998, 11, 1, 'Heavy metal', 1);
insert into ALBUMTABLE (TITLE, ARTIST, YEAR, NUMBER_OF_TRACKS, NUMBER_OF_DISCS, GENRE, ALBUMCOUNT)
   values ('St. Anger', 'Metallica', 2003, 11, 1, 'Heavy metal', 1);



---------------------------------Collection.sql---------------------------------
create table COLLECTION (
	COLLECTION_NAME VARCHAR(20) NOT NULL,
	OWNER VARCHAR(255),
	primary key (COLLECTION_NAME, OWNER),
	foreign key (OWNER) references USERTABLE(USERNAME)
);

insert into COLLECTION (COLLECTION_NAME, OWNER)
    values ('My First Collection', 'john');
insert into COLLECTION (COLLECTION_NAME, OWNER)
    values ('My Second Collection', 'john');




--------------------------------AlbumTracks.sql---------------------------------
create table ALBUM_TRACKS (
	NAME VARCHAR(30) NOT NULL,
	ALBUM VARCHAR(255) NOT NULL,
        TRACKNUMBER INT,
	SIDE INT, 
	TRACKLENGTH VARCHAR(8),
	primary key (NAME, ALBUM)
);

insert into ALBUM_TRACKS (TRACKNUMBER, NAME, ALBUM, SIDE, TRACKLENGTH)
    values ('2', 'Paranoid', 'Paranoid', 1, '2:48');
insert into ALBUM_TRACKS (TRACKNUMBER, NAME, ALBUM, SIDE, TRACKLENGTH)
    values ('1', 'War Pigs', 'Paranoid', 1, '7:57');
insert into ALBUM_TRACKS (TRACKNUMBER, NAME, ALBUM, SIDE, TRACKLENGTH)
    values ('3', 'Planet Caravan', 'Paranoid', 1, '4:32');
insert into ALBUM_TRACKS (TRACKNUMBER, NAME, ALBUM, SIDE, TRACKLENGTH)
    values ('4', 'Iron Man', 'Paranoid', 1, '5:56');




------------------------------CollectionItems.sql-------------------------------
create table COLLECTION_ITEMS (
	ALBUM_ID INT,
	COLLECTION_NAME VARCHAR(20),
	primary key (album_ID, collection_name),
	foreign key (album_ID) references ALBUMTABLE(album_ID),
	foreign key (collection_name) references COLLECTION(collection_name)
);

insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (1, 'My First Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (3, 'My First Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (5, 'My First Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (7, 'My First Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (1, 'My Second Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (2, 'My Second Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (4, 'My Second Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (6, 'My Second Collection');
insert into COLLECTION_ITEMS (ALBUM_ID, COLLECTION_NAME)
    values (8, 'My Second Collection');




