package meeting.meeting_room_reservation.service;

import meeting.meeting_room_reservation.domain.MeetingRoom;
import meeting.meeting_room_reservation.domain.Reservation;
import meeting.meeting_room_reservation.domain.User;
import meeting.meeting_room_reservation.model.dto.ResvDto;
import meeting.meeting_room_reservation.model.dto.ResvResponseDto;
import meeting.meeting_room_reservation.model.dto.ResvRmResponseDto;
import meeting.meeting_room_reservation.repository.MeetingRoomRepository;
import meeting.meeting_room_reservation.repository.ReservationRepository;
import meeting.meeting_room_reservation.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {
    private ReservationService reservationService;
    private UserRepository userRepository;
    private MeetingRoomRepository meetingRoomRepository;
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        meetingRoomRepository = mock(MeetingRoomRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        reservationService = new ReservationService(userRepository, meetingRoomRepository, reservationRepository);
    }

    @Test
    @DisplayName("회의실 콤보 리스트 조회")
    void selectMeetingRoomListTest() {
        when(meetingRoomRepository.findAll()).thenReturn(List.of(
                MeetingRoom.builder().roomCd("A101").roomNm("대회의실").capacity(10).build(),
                MeetingRoom.builder().roomCd("B202").roomNm("소회의실").capacity(15).build()
        ));
        List<ResvRmResponseDto> roomCodes = reservationService.selectMeetingRoomList();
        assertEquals(2, roomCodes.size());
        assertTrue(roomCodes.stream().anyMatch(room -> room.getRoomCd().equals("A101")));
        assertTrue(roomCodes.stream().anyMatch(room -> room.getRoomCd().equals("B202")));
    }

    @Test
    @DisplayName("회의실 예약건 조회")
    void selectReservationListTest() {
        String roomCd = "A101";
        String date = "20250501";
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 1, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 1, 11, 0);

        User user = User.builder()
                        .userId("e101010")
                        .userNm("김재원")
                        .email("e101010@gmail.com")
                        .password("pwd10")
                        .build();

        MeetingRoom room = MeetingRoom.builder()
                                      .roomCd(roomCd)
                                      .roomNm("대회의실")
                                      .capacity(10)
                                      .build();

        Reservation reservation = Reservation.builder()
                                             .resvNo(1L)
                                             .user(user)
                                             .meetingRoom(room)
                                             .userNm(user.getUserNm())
                                             .startTime(startTime)
                                             .endTime(endTime)
                                             .build();

        when(reservationRepository.findAllByMeetingRoomRoomCdAndStartTimeBetween(
                eq(roomCd), eq(LocalDate.of(2025, 5, 1).atStartOfDay()),
                eq(LocalDate.of(2025, 5, 2).atStartOfDay())
        )).thenReturn(List.of(reservation));

        List<ResvResponseDto> results = reservationService.selectReservationList(roomCd, date);

        assertEquals(1, results.size());
        ResvResponseDto result = results.get(0);
        assertEquals(1L, result.getResvNo());
        assertEquals("김재원", result.getUserNm());
        assertEquals("A101", result.getRoomCd());
        assertEquals(startTime, result.getStartTime());
        assertEquals(endTime, result.getEndTime());

        verify(reservationRepository).findAllByMeetingRoomRoomCdAndStartTimeBetween(
                eq(roomCd), eq(LocalDate.of(2025, 5, 1).atStartOfDay()),
                eq(LocalDate.of(2025, 5, 2).atStartOfDay())
        );
    }

    @Test
    @DisplayName("회의실 예약 생성")
    void saveReservationTest() {
        ResvDto dto = ResvDto.builder()
                              .userId("e101010")
                              .roomCd("A101")
                              .startTime(LocalDateTime.of(2025, 5, 1, 10, 0))
                              .endTime(LocalDateTime.of(2025, 5, 1, 11, 0))
                              .build();

        when(userRepository.findByUserId("e101010")).thenReturn(Optional.of(new User("e101010", "김재원", "pwd10", "e101010@gmail.com")));
        when(meetingRoomRepository.findByRoomCd("A101")).thenReturn(Optional.of(new MeetingRoom("A101", "대회의실", 10)));
        when(reservationRepository.existsByOverlapping(any(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any())).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            return Reservation.builder()
                              .resvNo(1L)
                              .user(r.getUser())
                              .meetingRoom(r.getMeetingRoom())
                              .startTime(r.getStartTime())
                              .endTime(r.getEndTime())
                              .userNm(r.getUserNm())
                              .build();
        });

        reservationService.saveReservation(dto);

        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("회의실 예약 생성(예약 시간 중복)")
    void saveReservationDuplicateTest() {
        ResvDto dto = ResvDto.builder()
                             .userId("e101010")
                             .roomCd("A101")
                             .startTime(LocalDateTime.of(2025, 5, 1, 10, 0))
                             .endTime(LocalDateTime.of(2025, 5, 1, 11, 0))
                             .build();

        when(userRepository.findByUserId("e101010")).thenReturn(Optional.of(new User("e101010", "김재원", "pwd10", "e101010@gmail.com")));
        when(meetingRoomRepository.findByRoomCd("A101")).thenReturn(Optional.of(new MeetingRoom("A101", "대회의실", 10)));
        when(reservationRepository.existsByOverlapping(any(), any(), any())).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class, () -> reservationService.saveReservation(dto));
        assertEquals("이미 해당 시간에 예약이 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회의실 예약 수정")
    void modifyReservationTest() {
        Long resvNo = 1L;
        User user = User.builder().userId("e101010").userNm("김재원").password("pwd10").email("e101010@gmail.com").build();
        MeetingRoom room = MeetingRoom.builder().roomCd("A101").roomNm("대회의실").capacity(10).build();

        Reservation existing = Reservation.builder()
                                          .resvNo(resvNo)
                                          .user(user)
                                          .userNm("김재원")
                                          .meetingRoom(room)
                                          .startTime(LocalDateTime.of(2025, 5, 1, 10, 0))
                                          .endTime(LocalDateTime.of(2025, 5, 1, 11, 0))
                                          .build();

        ResvDto updateDto = ResvDto.builder()
                                   .userId("e101010")
                                   .roomCd("A101")
                                   .startTime(LocalDateTime.of(2025, 5, 1, 12, 0))
                                   .endTime(LocalDateTime.of(2025, 5, 1, 13, 0))
                                   .build();

        when(reservationRepository.findById(resvNo)).thenReturn(Optional.of(existing));
        when(reservationRepository.findOverlappingReservations(any(), any(), any())).thenReturn(List.of(existing));
        when(reservationRepository.updateReservationTime(anyLong(), anyString(), any(), any())).thenReturn(1);
        when(reservationRepository.findById(resvNo)).thenReturn(Optional.of(
                Reservation.builder()
                           .resvNo(resvNo)
                           .user(user)
                           .userNm("김재원")
                           .meetingRoom(room)
                           .startTime(updateDto.getStartTime())
                           .endTime(updateDto.getEndTime())
                           .build()
        ));

        ResvResponseDto result = reservationService.modifyReservation(resvNo, updateDto);

        assertEquals(resvNo, result.getResvNo());
        assertEquals("김재원", result.getUserNm());
        assertEquals(updateDto.getStartTime(), result.getStartTime());
        assertEquals(updateDto.getEndTime(), result.getEndTime());
    }

    @Test
    @DisplayName("회의실 예약 취소(삭제)")
    void deleteReservationTest() {
        Long resvNo = 1L;

        when(reservationRepository.existsById(resvNo)).thenReturn(true);

        reservationService.deleteReservation(resvNo);

        verify(reservationRepository).existsById(resvNo);
        verify(reservationRepository).deleteById(resvNo);
    }
}
