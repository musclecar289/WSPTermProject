
/**
 * Author:  Nicholas Clemmons
 * Created: Mar 23, 2017
 */

drop table ALBUMTABLE;

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