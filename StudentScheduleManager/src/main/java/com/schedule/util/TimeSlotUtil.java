package com.schedule.util;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间段工具类
 */
public class TimeSlotUtil {
    
    /**
     * 时间段信息
     */
    public static class TimeSlot {
        private int period; // 节次
        private String periodName; // 节次名称
        private LocalTime startTime; // 开始时间
        private LocalTime endTime; // 结束时间
        private String timeRange; // 时间范围字符串
        private String description; // 描述
        
        public TimeSlot(int period, String periodName, LocalTime startTime, LocalTime endTime, String description) {
            this.period = period;
            this.periodName = periodName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.timeRange = startTime.toString() + "-" + endTime.toString();
            this.description = description;
        }
        
        // Getters
        public int getPeriod() { return period; }
        public String getPeriodName() { return periodName; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public String getTimeRange() { return timeRange; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return periodName + " " + timeRange;
        }
    }
    
    // 定义所有时间段
    private static final Map<Integer, TimeSlot> timeSlots = new HashMap<>();
    
    static {
        // 上午
        timeSlots.put(1, new TimeSlot(1, "第1节", LocalTime.of(8, 0), LocalTime.of(8, 45), "上午"));
        timeSlots.put(2, new TimeSlot(2, "第2节", LocalTime.of(8, 55), LocalTime.of(9, 40), "上午"));
        timeSlots.put(3, new TimeSlot(3, "第3节", LocalTime.of(10, 10), LocalTime.of(10, 55), "上午"));
        timeSlots.put(4, new TimeSlot(4, "第4节", LocalTime.of(11, 5), LocalTime.of(11, 50), "上午"));
        
        // 下午
        timeSlots.put(5, new TimeSlot(5, "第5节", LocalTime.of(14, 30), LocalTime.of(15, 15), "下午"));
        timeSlots.put(6, new TimeSlot(6, "第6节", LocalTime.of(15, 25), LocalTime.of(16, 10), "下午"));
        timeSlots.put(7, new TimeSlot(7, "第7节", LocalTime.of(16, 40), LocalTime.of(17, 25), "下午"));
        timeSlots.put(8, new TimeSlot(8, "第8节", LocalTime.of(17, 35), LocalTime.of(18, 20), "下午"));
        
        // 晚上
        timeSlots.put(9, new TimeSlot(9, "第9节", LocalTime.of(19, 10), LocalTime.of(19, 55), "晚上"));
        timeSlots.put(10, new TimeSlot(10, "第10节", LocalTime.of(20, 5), LocalTime.of(20, 50), "晚上"));
        timeSlots.put(11, new TimeSlot(11, "第11节", LocalTime.of(21, 0), LocalTime.of(21, 45), "晚上"));
    }
    
    /**
     * 获取所有时间段
     */
    public static Map<Integer, TimeSlot> getAllTimeSlots() {
        return new HashMap<>(timeSlots);
    }
    
    /**
     * 根据节次获取时间段
     */
    public static TimeSlot getTimeSlotByPeriod(int period) {
        return timeSlots.get(period);
    }
    
    /**
     * 根据时间获取对应的节次
     */
    public static TimeSlot getTimeSlotByTime(LocalTime time) {
        for (TimeSlot slot : timeSlots.values()) {
            if (!time.isBefore(slot.getStartTime()) && !time.isAfter(slot.getEndTime())) {
                return slot;
            }
        }
        return null;
    }
    
    /**
     * 获取时间段数组（用于界面显示）
     */
    public static String[] getTimeSlotStrings() {
        String[] result = new String[timeSlots.size()];
        int index = 0;
        for (int i = 1; i <= 11; i++) {
            TimeSlot slot = timeSlots.get(i);
            if (slot != null) {
                result[index++] = slot.toString();
            }
        }
        return result;
    }
    
    /**
     * 获取时间段范围数组（用于数据库匹配）
     */
    public static String[] getTimeRangeStrings() {
        String[] result = new String[timeSlots.size()];
        int index = 0;
        for (int i = 1; i <= 11; i++) {
            TimeSlot slot = timeSlots.get(i);
            if (slot != null) {
                result[index++] = slot.getTimeRange();
            }
        }
        return result;
    }
    
    /**
     * 根据时间范围字符串获取时间段
     */
    public static TimeSlot getTimeSlotByRange(String timeRange) {
        for (TimeSlot slot : timeSlots.values()) {
            if (slot.getTimeRange().equals(timeRange)) {
                return slot;
            }
        }
        return null;
    }
} 