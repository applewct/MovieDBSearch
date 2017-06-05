CREATE TABLE Movies (
    id NUMBER,
    title VARCHAR2(200),
    imdbID NUMBER,
    spanishTitle VARCHAR2(200),
    imdbPictureURL VARCHAR2(200),
    year NUMBER,
    rtID VARCHAR2(100),
    rtAllCriticsRating NUMBER,
    rtAllCriticsNumReviews NUMBER,
    rtAllCriticsNumFresh NUMBER,
    rtAllCriticsNumRotten NUMBER,
    rtAllCriticsScore NUMBER,
    rtTopCriticsRating NUMBER,
    rtTopCriticsNumReviews NUMBER,
    rtTopCriticsNumFresh NUMBER,
    rtTopCriticsNumRotten NUMBER,
    rtTopCriticsScore NUMBER,
    rtAudienceRating NUMBER,
    rtAudienceNumRatings NUMBER,
    rtAudienceScore NUMBER,
    rtPictureURL VARCHAR2(200),
    PRIMARY KEY (id)
);

CREATE TABLE MovieActors (
    movieID NUMBER,
    actorID VARCHAR2(100),
    actorName VARCHAR2(100),
    ranking NUMBER,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    PRIMARY KEY (movieID, actorID)
);

CREATE TABLE MovieDirectors (
    movieID NUMBER,
    directorID VARCHAR2(100),
    directorName VARCHAR2(100),
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    PRIMARY KEY (movieID, directorID)
);

CREATE TABLE MovieGenres (
    movieID NUMBER,
    genre VARCHAR2(100),
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    PRIMARY KEY (movieID, genre)
);

CREATE TABLE MovieCountries (
    movieID NUMBER(10),
    country VARCHAR2(100),
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    PRIMARY KEY (movieID)
);

CREATE TABLE Tags (
    id NUMBER,
    value VARCHAR2(100),
    PRIMARY KEY (id)
);

CREATE TABLE MovieTags (
    movieID NUMBER,
    tagID NUMBER,
    tagWeight NUMBER,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    FOREIGN KEY (tagID) REFERENCES Tags(id) ON DELETE CASCADE,
    PRIMARY KEY (movieID, tagID)
);

CREATE TABLE UserTags (
    userID NUMBER,
    movieID NUMBER,
    tagID NUMBER,
    date_day NUMBER,
    date_month NUMBER,
    date_year NUMBER,
    date_hour NUMBER,
    date_minute NUMBER,
    date_second NUMBER,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    FOREIGN KEY (tagID) REFERENCES Tags(id) ON DELETE CASCADE,
    PRIMARY KEY (userID, movieID, tagID)
);

CREATE TABLE UserRates (
    userID NUMBER,
    movieID NUMBER,
    rating NUMBER,
    date_day NUMBER,
    date_month NUMBER,
    date_year NUMBER,
    date_hour NUMBER,
    date_minute NUMBER,
    date_second NUMBER,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE,
    PRIMARY KEY (userID, movieID)
);
