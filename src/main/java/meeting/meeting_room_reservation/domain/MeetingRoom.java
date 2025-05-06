package meeting.meeting_room_reservation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "meeting_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoom {

    @Id
    @Column(name = "room_cd", length = 50)
    @Comment(value = "회의실 코드")
    private String roomCd;

    @Column(name = "room_nm", nullable = false, length = 100)
    @Comment(value = "회의실명")
    private String roomNm;

    @Column(nullable = false)
    private int capacity;
}
