package meeting.meeting_room_reservation.repository;

import meeting.meeting_room_reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findAllByMeetingRoomRoomCdAndStartTimeBetween(String roomCd, LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT resv FROM Reservation resv
         WHERE resv.meetingRoom.roomCd = :roomCd
           AND resv.startTime < :end
           AND resv.endTime > :start
    """)
    List<Reservation> findOverlappingReservations(String roomCd, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT COUNT(resv) > 0 FROM Reservation resv
         WHERE resv.meetingRoom.roomCd = :roomCd
           AND resv.startTime < :end
           AND resv.endTime > :start
    """)
    boolean existsByOverlapping(String roomCd, LocalDateTime start, LocalDateTime end);

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Reservation resv
          SET resv.meetingRoom.roomCd = :roomCd,
              resv.startTime = :startTime,
              resv.endTime = :endTime
        WHERE resv.resvNo = :resvNo
    """)
    int updateReservationTime(
            @Param("resvNo") Long resvNo,
            @Param("roomCd") String roomCd,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
