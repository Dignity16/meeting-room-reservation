DELETE FROM reservations;
DELETE FROM meeting_rooms;
DELETE FROM users;

INSERT INTO users (user_id, user_nm, password, email) VALUES
                  ('e101010', '김재원', 'pwd10', 'e101010@gmail.com'),
                  ('e202020', '송길수', 'pwd20', 'e202020@gmail.com'),
                  ('e303030', '이수현', 'pwd30', 'e303030@gmail.com'),
                  ('e404040', '이범석', 'pwd40', 'e404040@gmail.com');

INSERT INTO meeting_rooms (room_cd, room_nm, capacity) VALUES
                          ('A101', '대회의실', 10),
                          ('B202', '소회의실', 20),
                          ('C303', '큐빅룸', 15);