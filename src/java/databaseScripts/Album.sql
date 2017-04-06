
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