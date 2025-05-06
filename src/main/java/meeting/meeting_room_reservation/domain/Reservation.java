package meeting.meeting_room_reservation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment(value = "예약 번호")
    private Long resvNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment(value = "사용자 정보")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_cd", nullable = false)
    @Comment(value = "회의실 정보")
    private MeetingRoom meetingRoom;

    @Column(nullable = false)
    @Comment(value = "사용자명")
    private String userNm;

    @Column(nullable = false)
    @Comment(value = "예약 시작 시간")
    private LocalDateTime startTime;

    @Comment(value = "예약 종료 시간")
    @Column(nullable = false)
    private LocalDateTime endTime;

}
