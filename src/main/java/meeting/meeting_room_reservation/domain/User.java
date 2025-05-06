package meeting.meeting_room_reservation.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id", length = 50)
    @Comment(value = "사용자 ID")
    private String userId;

    @Column(nullable = false, name = "user_nm", length = 100)
    @Comment(value = "사용자명")
    private String userNm;

    @Column(nullable = false)
    @Comment(value = "비밀번호")
    private String password;

    @Column(nullable = false)
    @Comment(value = "이메일")
    private String email;
}

