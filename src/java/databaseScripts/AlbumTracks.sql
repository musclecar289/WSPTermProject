

drop table ALBUM_TRACKS;

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