package meeting.meeting_room_reservation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import meeting.meeting_room_reservation.domain.MeetingRoom;
import meeting.meeting_room_reservation.domain.Reservation;
import meeting.meeting_room_reservation.domain.User;
import meeting.meeting_room_reservation.model.dto.ResvDto;
import meeting.meeting_room_reservation.model.dto.ResvResponseDto;
import meeting.meeting_room_reservation.model.dto.ResvRmResponseDto;
import meeting.meeting_room_reservation.repository.MeetingRoomRepository;
import meeting.meeting_room_reservation.repository.ReservationRepository;
import meeting.meeting_room_reservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    private final UserRepository userRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final ReservationRepository reservationRepository;

    /**
     * *******************************
     * 회의실 콤보 리스트 조회
     *
     * @return List<ResvRmResponseDto>
     * *******************************
     */
    public List<ResvRmResponseDto> selectMeetingRoomList() {
        return meetingRoomRepository.findAll().stream()
                .map(resvRm -> ResvRmResponseDto.builder()
                                                .roomCd(resvRm.getRoomCd())
                                                .roomNm(resvRm.getRoomNm())
                                                .capacity(resvRm.getCapacity())
                                                .build())
                                                .collect(Collectors.toList());
    }

    /**
     * *******************************
     * 회의실 예약건 월 단위 조회
     *
     * @param roomCd
     * @param date
     * @return List<ResvResponseDto>
     * *******************************
     */
    public List<ResvResponseDto> selectMonthlyResvList(String roomCd, String date) {

        YearMonth yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyyMM"));

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfNextMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<Reservation> reservations = reservationRepository
                                .findAllByMeetingRoomRoomCdAndStartTimeBetween(roomCd, startOfMonth, startOfNextMonth);

        return reservations.stream().map(resv -> ResvResponseDto.builder()
                                                                .resvNo(resv.getResvNo())
                                                                .userNm(resv.getUserNm())
                                                                .roomCd(resv.getMeetingRoom().getRoomCd())
                                                                .startTime(resv.getStartTime())
                                                                .endTime(resv.getEndTime())
                                                                .build())
                                                                .collect(Collectors.toList());
    }


    /**
     * *******************************
     * 회의실 예약건 일 단위 조회
     *
     * @param roomCd
     * @param date
     * @return List<ResvResponseDto>
     * *******************************
     */
    public List<ResvResponseDto> selectReservationList(String roomCd, String date) {

        LocalDate yyyyMMdd = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDateTime startOfDay   = yyyyMMdd.atStartOfDay();
        LocalDateTime startOfNext  = startOfDay.plusDays(1);

        List<Reservation> reservations = reservationRepository.findAllByMeetingRoomRoomCdAndStartTimeBetween(
                                                                                    roomCd, startOfDay, startOfNext);
        return reservations.stream()
                .map(resv -> ResvResponseDto.builder()
                                            .resvNo(resv.getResvNo())
                                            .userNm(resv.getUserNm())
                                            .roomCd(resv.getMeetingRoom().getRoomCd())
                                            .startTime(resv.getStartTime())
                                            .endTime(resv.getEndTime())
                                            .build())
                                            .collect(Collectors.toList());
    }

    /**
     * *******************************
     * 회의실 예약 신규 등록
     * 
     * @param resvDto
     * @return ResvResponseDto
     * *******************************
     */
    @Transactional
    public ResvResponseDto saveReservation(ResvDto resvDto) {

        // 시간 형식 유효성 체크
        if (!isTimeValid(resvDto.getStartTime(), resvDto.getEndTime())) {
            throw new IllegalArgumentException("시간 형식이 잘못되었습니다. (00분, 30분 단위)");
        }

        // 회의실 정보 조회
        MeetingRoom room = meetingRoomRepository.findByRoomCd(resvDto.getRoomCd())
                                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회의실입니다."));
        // 예약 현황 조회(예약 시간 중복 방지)
        boolean isDuplicate = reservationRepository.existsByOverlapping(
                room.getRoomCd(), resvDto.getStartTime(), resvDto.getEndTime()
        );
        if (isDuplicate) {
            throw new IllegalStateException("이미 해당 시간에 예약이 존재합니다.");
        }

        logger.info("사용자 ID: {}", resvDto.getUserId());

        // 사용자 정보 조회
        User user = userRepository.findByUserId(resvDto.getUserId())
                                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 회의실 예약 저장
        Reservation reservation = reservationRepository.save(Reservation.builder()
                                                       .user(user)
                                                       .meetingRoom(room)
                                                       .userNm(user.getUserNm())
                                                       .startTime(resvDto.getStartTime())
                                                       .endTime(resvDto.getEndTime())
                                                       .build());
        // 예약 정보 반환
        return ResvResponseDto.builder()
                              .resvNo(reservation.getResvNo())
                              .userNm(user.getUserNm())
                              .roomCd(room.getRoomCd())
                              .startTime(reservation.getStartTime())
                              .endTime(reservation.getEndTime())
                              .build();
    }

    /**
     * *******************************
     * 회의실 예약 수정
     *
     * @param resvDto
     * @return ResvResponseDto
     * *******************************
     */
    @Transactional
    public ResvResponseDto modifyReservation(Long resvNo, ResvDto resvDto) {

        Reservation reservation = reservationRepository.findById(resvNo)
                                                .orElseThrow(() -> new NoSuchElementException("예약을 찾을 수 없습니다."));

        if (!reservation.getUser().getUserId().equals(resvDto.getUserId())) {
            throw new IllegalStateException("최초 예약자의 id와 일치하지 않습니다. 예약 변경 불가");
        }

        // 변경하려는 시간대가 겹치는 예약건 조회
        List<Reservation> duplicatedResv = reservationRepository.findOverlappingReservations(
                resvDto.getRoomCd(), resvDto.getStartTime(), resvDto.getEndTime()
        );

        int dupResvCnt = duplicatedResv.size();

        // 시간대가 겹치는 예약이 2개이상 일 경우 예약 변경 불가
        if (dupResvCnt > 1) {
            throw new IllegalStateException("이미 해당 시간에 예약이 존재합니다.");
        }

        // 시간대가 겹치는 예약건이 1건이고
        if (dupResvCnt == 1) {
            // 현재 로그인된 사용자가 변경하려는 예약건일 경우 변경 가능
            if(duplicatedResv.get(0).getUser().getUserId().equals(resvDto.getUserId())
                                                        && duplicatedResv.get(0).getResvNo().equals(resvNo)) {
                return updateResvation(resvNo, resvDto);
            } else {
                // 로그인된 사용자의 예약건이 아닌 경우
                throw new IllegalStateException("이미 해당 시간에 예약이 존재합니다.");
            }
        }

        return updateResvation(resvNo, resvDto);
    }

    /**
     * *******************************
     * 회의실 예약 취소(삭제)
     *
     * @param resvNo
     * *******************************
     */
    public void deleteReservation(Long resvNo) {
        if (!reservationRepository.existsById(resvNo)) {
            throw new NoSuchElementException("존재하지 않는 예약입니다.");
        }
        reservationRepository.deleteById(resvNo);
    }

    /**
     * *******************************
     * 시간 유효성 검증
     *
     * @param start
     * @param end
     * @return boolean
     * *******************************
     */
    private boolean isTimeValid(LocalDateTime start, LocalDateTime end) {
        return start.isBefore(end)
                && (start.getMinute() == 0 || start.getMinute() == 30)
                && (end.getMinute() == 0 || end.getMinute() == 30);
    }

    /**
     * *******************************
     * 예약 update 로직 분리
     *
     * @param resvNo
     * @param resvDto
     * @return ResvResponseDto
     * *******************************
     */
    private ResvResponseDto updateResvation(Long resvNo, ResvDto resvDto) {
        int updated = reservationRepository.updateReservationTime(resvNo,
                                                                  resvDto.getRoomCd(),
                                                                  resvDto.getStartTime(),
                                                                  resvDto.getEndTime());
        if (updated == 0) {
            throw new NoSuchElementException("수정 대상 예약이 없습니다. 예약번호: " + resvNo);
        }

        Reservation updatedResv = reservationRepository.findById(resvNo)
                                                .orElseThrow(() -> new NoSuchElementException("예약을 찾을 수 없습니다."));

        return ResvResponseDto.builder()
                              .resvNo(updatedResv.getResvNo())
                              .userNm(updatedResv.getUserNm())
                              .roomCd(updatedResv.getMeetingRoom().getRoomCd())
                              .startTime(updatedResv.getStartTime())
                              .endTime(updatedResv.getEndTime())
                              .build();
    }
}