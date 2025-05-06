-- users 테이블
CREATE TABLE IF NOT EXISTS users (
    user_id  VARCHAR(50) PRIMARY KEY,
    user_nm  VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL
);

-- meeting_rooms 테이블
CREATE TABLE IF NOT EXISTS meeting_rooms (
    room_cd  VARCHAR(50) PRIMARY KEY,
    room_nm  VARCHAR(100) NOT NULL,
    capacity INT          NOT NULL
);

-- reservations 테이블
CREATE TABLE IF NOT EXISTS reservations (
    resv_no    BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id    VARCHAR(50)    NOT NULL,
    user_nm    VARCHAR(100)   NOT NULL,
    room_cd    VARCHAR(50)    NOT NULL,
    start_time DATETIME       NOT NULL,
    end_time   DATETIME       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (room_cd) REFERENCES meeting_rooms(room_cd)
);
