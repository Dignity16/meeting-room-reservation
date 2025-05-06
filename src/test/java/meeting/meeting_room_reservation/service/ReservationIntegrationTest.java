package meeting.meeting_room_reservation.service;

import jakarta.transaction.Transactional;
import meeting.meeting_room_reservation.domain.MeetingRoom;
import meeting.meeting_room_reservation.domain.User;
import meeting.meeting_room_reservation.model.dto.ResvDto;
import meeting.meeting_room_reservation.model.dto.ResvResponseDto;
import meeting.meeting_room_reservation.repository.MeetingRoomRepository;
import meeting.meeting_room_reservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@Transactional
class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    private User user;
    private MeetingRoom room;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                                       .userId("e101010")
                                       .userNm("김재원")
                                       .email("e101010@gmail.com")
                                       .password("pwd10")
                                       .build());

        room = meetingRoomRepository.save(MeetingRoom.builder()
                                                     .roomCd("A101")
                                                     .roomNm("대회의실")
                                                     .capacity(10)
                                                     .build());
    }

    @Test
    @DisplayName("회의실 예약건 조회 - 월 단위")
    void selectMonthlyResvListIntgTest() {
        ResvDto resv = ResvDto.builder()
                              .userId(user.getUserId())
                              .roomCd(room.getRoomCd())
                              .startTime(LocalDateTime.of(2025, 5, 6, 10, 0))
                              .endTime(LocalDateTime.of(2025, 5, 6, 11, 0))
                              .build();

        reservationService.saveReservation(resv);
        List<ResvResponseDto> resvList = reservationService.selectMonthlyResvList(room.getRoomCd(), "202505");

        assertThat(resvList).hasSize(1);
        assertThat(resvList.get(0).getRoomCd()).isEqualTo("A101");
        assertThat(resvList.get(0).getUserNm()).isEqualTo("김재원");
        assertThat(resvList.get(0).getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 10, 0));
        assertThat(resvList.get(0).getEndTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 11, 0));
    }

    @Test
    @DisplayName("회의실 예약건 조회 - 일 단위")
    void selectReservationListIntgTest() {
        ResvDto resv = ResvDto.builder()
                              .userId(user.getUserId())
                              .roomCd(room.getRoomCd())
                              .startTime(LocalDateTime.of(2025, 5, 6, 10, 0))
                              .endTime(LocalDateTime.of(2025, 5, 6, 11, 0))
                              .build();

        reservationService.saveReservation(resv);
        List<ResvResponseDto> resvList = reservationService.selectReservationList(room.getRoomCd(), "20250506");

        assertThat(resvList).hasSize(1);
        assertThat(resvList.get(0).getRoomCd()).isEqualTo("A101");
        assertThat(resvList.get(0).getUserNm()).isEqualTo("김재원");
        assertThat(resvList.get(0).getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 10, 0));
        assertThat(resvList.get(0).getEndTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 11, 0));
    }

    @Test
    @DisplayName("회의실 예약 생성")
    void saveReservationIntgTest() {
        ResvDto resv = ResvDto.builder()
                              .userId(user.getUserId())
                              .roomCd(room.getRoomCd())
                              .startTime(LocalDateTime.of(2025, 5, 6, 10, 0))
                              .endTime(LocalDateTime.of(2025, 5, 6, 11, 0))
                              .build();

        ResvResponseDto saveResult = reservationService.saveReservation(resv);

        assertThat(saveResult.getUserNm()).isEqualTo("김재원");
        assertThat(saveResult.getRoomCd()).isEqualTo("A101");
        assertThat(saveResult.getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 10, 0));
        assertThat(saveResult.getEndTime()).isEqualTo(LocalDateTime.of(2025, 5, 6, 11, 0));
    }

    @Test
    @DisplayName("회의실 예약 생성 - 중복 예약")
    void saveReservationDuplicateIntgTest() {
        ResvDto resv1 = ResvDto.builder()
                               .userId(user.getUserId())
                               .roomCd(room.getRoomCd())
                               .startTime(LocalDateTime.of(2025, 5, 6, 10, 0))
                               .endTime(LocalDateTime.of(2025, 5, 6, 11, 0))
                               .build();
        reservationService.saveReservation(resv1);

        ResvDto resv2 = ResvDto.builder()
                               .userId(user.getUserId())
                               .roomCd(room.getRoomCd())
                               .startTime(LocalDateTime.of(2025, 5, 6, 10, 30))
                               .endTime(LocalDateTime.of(2025, 5, 6, 11, 30))
                               .build();

        assertThatThrownBy(() -> reservationService.saveReservation(resv2)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("회의실 예약 수정")
    void modifyReservationIntgTest() {
        ResvDto resv = ResvDto.builder()
                              .userId(user.getUserId())
                              .roomCd(room.getRoomCd())
                              .startTime(LocalDateTime.of(2025, 5, 8, 9, 0))
                              .endTime(LocalDateTime.of(2025, 5, 8, 10, 0))
                              .build();
        ResvResponseDto saveResult = reservationService.saveReservation(resv);

        ResvDto updateDto = ResvDto.builder()
                                   .userId(user.getUserId())
                                   .roomCd(room.getRoomCd())
                                   .startTime(LocalDateTime.of(2025, 5, 8, 13, 0))
                                   .endTime(LocalDateTime.of(2025, 5, 8, 14, 0))
                                   .build();
        ResvResponseDto updateResult = reservationService.modifyReservation(saveResult.getResvNo(), updateDto);

        assertThat(updateResult.getUserNm()).isEqualTo("김재원");
        assertThat(updateResult.getRoomCd()).isEqualTo("A101");
        assertThat(updateResult.getStartTime()).isEqualTo(LocalDateTime.of(2025, 5, 8, 13, 0));
        assertThat(updateResult.getEndTime()).isEqualTo(LocalDateTime.of(2025, 5, 8, 14, 0));
    }

    @Test
    @DisplayName("회의실 예약 삭제")
    void deleteReservationIntgTest() {
        ResvDto resv = ResvDto.builder()
                              .userId(user.getUserId())
                              .roomCd(room.getRoomCd())
                              .startTime(LocalDateTime.of(2025, 5, 9, 15, 0))
                              .endTime(LocalDateTime.of(2025, 5, 9, 16, 0))
                              .build();
        ResvResponseDto saveResult = reservationService.saveReservation(resv);

        reservationService.deleteReservation(saveResult.getResvNo());
        List<ResvResponseDto> resvList = reservationService.selectReservationList(user.getUserId(), "20250509");

        assertThat(resvList).isEmpty();
    }
}
