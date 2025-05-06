package meeting.meeting_room_reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import meeting.meeting_room_reservation.model.dto.ResvDto;
import meeting.meeting_room_reservation.model.dto.ResvResponseDto;
import meeting.meeting_room_reservation.model.dto.ResvRmResponseDto;
import meeting.meeting_room_reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Reservation API", description = "회의실 예약 관련 API")
@RestController
@RequestMapping("/meeting-rooms")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    // private final ResvMapstruct resvMapstruct;

    @Operation(summary = "회의실 종류 콤보 리스트 조회", description = "회의실 종류를 조회합니다.")
    @GetMapping("/room-category")
    public ResponseEntity<List<ResvRmResponseDto>> getMeetingRoomList() {
        return ResponseEntity.ok(reservationService.selectMeetingRoomList());
    }

    @Operation(summary = "회의실 예약 월간 조회", description = "회의실 예약을 조회합니다.")
    @GetMapping("/reservations/monthly")
    public ResponseEntity<List<ResvResponseDto>> findMonthlyResvList(@RequestParam String roomCd,
                                                                     @RequestParam String date) {
        return ResponseEntity.ok(reservationService.selectMonthlyResvList(roomCd, date));
    }
    
    @Operation(summary = "회의실 예약 일간 조회", description = "회의실 예약을 조회합니다.")
    @GetMapping("/reservations/daily")
    public ResponseEntity<List<ResvResponseDto>> findReservationList(@RequestParam String roomCd,
                                                                    @RequestParam String date) {
        return ResponseEntity.ok(reservationService.selectReservationList(roomCd, date));
    }

    @Operation(summary = "회의실 예약 생성", description = "회의실 예약을 신규 등록합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ResvResponseDto> createReservation(@RequestBody ResvDto resvDto) {
        return ResponseEntity.ok(reservationService.saveReservation(resvDto));
    }

    @Operation(summary = "회의실 예약 수정", description = "회의실 예약을 수정합니다.")
    @PutMapping("/reservations/{resvNo}")
    public ResponseEntity<ResvResponseDto> updateReservation(@PathVariable Long resvNo,
                                                             @RequestBody ResvDto resvDto) {
        return ResponseEntity.ok(reservationService.modifyReservation(resvNo, resvDto));
    }

    @Operation(summary = "회의실 예약 취소", description = "회의실 예약을 삭제합니다.")
    @DeleteMapping("/reservations/{resvNo}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long resvNo) {
        reservationService.deleteReservation(resvNo);
        return ResponseEntity.noContent().build();
    }
}