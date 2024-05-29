package com.example.demo.domain.cafe;

import com.example.demo.exception.CafegoryException;
import com.example.demo.exception.ExceptionType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BusinessHourOpenChecker implements OpenChecker<BusinessHour> {

    @Override
    public boolean checkByNowTime(DayOfWeek dayOfWeek, LocalTime businessStartTime, LocalTime businessEndTime,
                                  LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();

        boolean isOpenOvernight = isOpenOvernight(businessStartTime, businessEndTime);

        if (!isTodayBusinessDay(dayOfWeek, now, isOpenOvernight)) {
            return false;
        }
        // 24시간 영업인 경우 항상 참
        if (is24HourBusiness(businessStartTime, businessEndTime)) {
            return true;
        }
        // 새벽까지 영업하는 경우
        if (isOpenOvernight) {
            return isOpenOvernightNow(businessStartTime, businessEndTime, currentTime);
        }
        // 같은 날에 영업을 시작하고 종료하는 경우
        return isOpenDuringDay(businessStartTime, businessEndTime, currentTime);
    }

    private boolean isTodayBusinessDay(DayOfWeek businessDay, LocalDateTime now, boolean isOpenOvernight) {
        if (isOpenOvernight) {
            return businessDay.equals(now.getDayOfWeek()) || businessDay.equals(now.minusDays(1).getDayOfWeek());
        }
        return businessDay.equals(now.getDayOfWeek());
    }

    private boolean is24HourBusiness(LocalTime startTime, LocalTime endTime) {
        return startTime.equals(LocalTime.MIN) && endTime.equals(LocalTime.MAX);
    }

    private boolean isOpenOvernight(LocalTime startTime, LocalTime endTime) {
        return endTime.isBefore(startTime);
    }

    private boolean isOpenOvernightNow(LocalTime startTime, LocalTime endTime, LocalTime currentTime) {
        return currentTime.isAfter(startTime) || (currentTime.isBefore(endTime) && currentTime.isAfter(
                LocalTime.MIDNIGHT));
    }

    private boolean isOpenDuringDay(LocalTime startTime, LocalTime endTime, LocalTime currentTime) {
        return (currentTime.isAfter(startTime) || currentTime.equals(startTime)) && currentTime.isBefore(endTime);
    }

    @Override
    public boolean checkWithBusinessHours(List<BusinessHour> businessHours, LocalDateTime now) {
        if (!hasMatchingDayOfWeek(businessHours, now)) {
            throw new CafegoryException(ExceptionType.CAFE_NOT_FOUND_DAY_OF_WEEK);
        }
        return businessHours.stream()
                .anyMatch(
                        hour -> checkByNowTime(DayOfWeek.valueOf(hour.getDay()), hour.getStartTime(), hour.getEndTime(), now));
    }

    public boolean checkBetweenBusinessHours(LocalTime businessStartTime, LocalTime businessEndTime,
                                             LocalTime chosenStartTime, LocalTime chosenEndTime) {
        // 영업 시작시간이 당일, 영업 종료시간이 당일
        if (businessStartTime.isBefore(businessEndTime)) {
            return (businessStartTime.equals(chosenStartTime) || businessStartTime.isBefore(chosenStartTime)) && (businessEndTime.equals(chosenEndTime) || businessEndTime.isAfter(chosenEndTime));
        }
        // 영업 시작시간이 당일, 영업 종료시간이 다음날
        if (businessStartTime.isAfter(businessEndTime)) {
            LocalDateTime businessStartDateTime = LocalDateTime.of(LocalDate.now(), businessStartTime);
            LocalDateTime businessEndDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), businessEndTime);
            // 선택된 시작시간이 당일, 선택된 종료시간이 당일 || 선택된 시작시간이 다음날, 선택된 종료시간이 다음날
            if (chosenStartTime.isBefore(chosenEndTime)) {
                LocalDate chosenDate = LocalDate.now();

                boolean isChosenTimeOvernight = businessStartTime.isAfter(chosenStartTime);
                LocalDate date = isChosenTimeOvernight ? chosenDate.plusDays(1) : chosenDate;
                LocalDateTime chosenStartDateTime = LocalDateTime.of(date, chosenStartTime);
                LocalDateTime chosenEndDateTime = LocalDateTime.of(date, chosenEndTime);

                return (businessStartDateTime.isBefore(chosenStartDateTime) || businessStartDateTime.equals(chosenStartDateTime))
                        && (businessEndDateTime.isAfter(chosenEndDateTime) || businessEndDateTime.equals(chosenEndDateTime));
            }
            // 선택된 시작시간이 당일, 선택된 종료시간이 다음날
            if (chosenStartTime.isAfter(chosenEndTime)) {
                LocalDateTime chosenStartDateTime = LocalDateTime.of(LocalDate.now(), chosenStartTime);
                LocalDateTime chosenEndDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), chosenEndTime);
                return (businessStartDateTime.equals(chosenStartDateTime) || businessStartDateTime.isBefore(chosenStartDateTime)) && (businessEndDateTime.equals(chosenEndDateTime) || businessEndDateTime.isAfter(chosenEndDateTime));
            }
        }
        return false;
    }

    private boolean hasMatchingDayOfWeek(List<BusinessHour> businessHours, LocalDateTime now) {
        return businessHours.stream()
                .anyMatch(hour -> hour.existsMatchingDayOfWeek(now));
    }
}
