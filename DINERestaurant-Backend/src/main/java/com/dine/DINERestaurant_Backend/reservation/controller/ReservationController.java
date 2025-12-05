package com.dine.DINERestaurant_Backend.reservation.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.reservation.entity.Reservation;
import com.dine.DINERestaurant_Backend.reservation.entity.RestaurantTable;
import com.dine.DINERestaurant_Backend.reservation.service.ReservationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class    ReservationController {

    @Autowired  private ReservationService reservationService;
    @Autowired private JwtUtil jwtUtil;

    /**
     * Kiểm tra bàn trống
     * GET /reservations/available?date=2024-11-15&time=18:00&guestCount=4
     */
    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guestCount) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<RestaurantTable> tables = reservationService.getAvailableTables(date, time, guestCount);
            response.put("success", true);
            response.put("data", tables);
            response.put("count", tables.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    private Integer getCurrentUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token không hợp lệ");
        }
        String token = authHeader.substring(7); // bỏ "Bearer "

        // Giải mã token (dùng cách bạn đang có trong dự án)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("${jwt.secret}") // ← thay bằng key thật của bạn (tìm trong JwtTokenProvider hoặc application.yml)
                    .parseClaimsJws(token)
                    .getBody();

            String userIdStr = claims.getSubject(); // vì sub = userId trong token của bạn
            return Integer.parseInt(userIdStr);
        } catch (Exception e) {
            throw new RuntimeException("Token hết hạn hoặc không hợp lệ");
        }
    }
    /**
     * Đặt bàn mới
     * POST /reservations
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(@RequestHeader("Authorization") String authHeader,
                                                                 @RequestBody Reservation reservation) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = authHeader.substring(7);
            Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));
            reservation.setUserId(userId);
            Reservation saved = reservationService.createReservation(reservation);

            response.put("success", true);
            response.put("message", "Reservation created successfully");
            response.put("data", saved);
            response.put("qrCode", saved.getQrCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Lịch đặt bàn của user
     * GET /reservations/my/{userId}
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getUserReservations(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy token từ header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(401).body(response);
            }
            String token = authHeader.substring(7); // bỏ "Bearer "

            Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            response.put("success", true);
            response.put("data", reservations);
            response.put("count", reservations.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    /**
//     * Chi tiết đặt bàn
//     * GET /reservations/{id}
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getReservationById(@PathVariable Integer id) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Reservation reservation = reservationService.getReservationById(id);
//
//            if (reservation == null) {
//                response.put("success", false);
//                response.put("message", "Reservation not found");
//                return ResponseEntity.status(404).body(response);
//            }
//
//            response.put("success", true);
//            response.put("data", reservation);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

    /**
     * Sửa đặt bàn
     * PUT /reservations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReservation(
            @PathVariable Integer id,
            @RequestBody Reservation reservation) {

        Map<String, Object> response = new HashMap<>();

        try {
            Reservation updated = reservationService.updateReservation(id, reservation);

            if (updated == null) {
                response.put("success", false);
                response.put("message", "Reservation not found");
                return ResponseEntity.status(404).body(response);
            }

            response.put("success", true);
            response.put("data", updated);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Hủy đặt bàn
     * PUT /reservations/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelReservation(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean success = reservationService.cancelReservation(id);

            if (success) {
                response.put("success", true);
                response.put("message", "Reservation cancelled");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Reservation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check-in bằng QR code
     * POST /reservations/checkin
     */
    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkInWithQR(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String qrCode = request.get("qrCode");
            Reservation reservation = reservationService.checkInWithQR(qrCode);

            if (reservation != null) {
                response.put("success", true);
                response.put("message", "Check-in successful");
                response.put("data", reservation);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid QR code or reservation already used");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}