package org.qiu.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: Global unique ID generator implemented using the Snowflake algorithm.
 * @Author: QiuXuan
 * @Email: qiu_2022@aliyun.com
 * @Project: creativityHub
 * @Date: 2024/5/11 17:22
 * @Version 1.0
 * @Since 1.0
 **/
@Component
public class SnowflakeIdGenerator {

    // The initial timestamp, assumed here is 2024-01-01 00:00:00
    private final long epoch = 1704067200000L;

    // Number of bits for worker ID
    private final long workerIdBits = 5L;
    // Number of bits for data center ID
    private final long dataCenterIdBits = 5L;
    // Number of bits for sequence
    private final long sequenceBits = 12L;
    // Maximum worker ID
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // Maximum data center ID
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    // Left shift for worker ID
    private final long workerIdShift = sequenceBits;
    // Left shift for data center ID
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    // Left shift for timestamp
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    // Sequence mask
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Constructor to initialize worker ID and data center ID.
     * @param workerId      Worker ID
     * @param dataCenterId  Data center ID
     */
    public SnowflakeIdGenerator(@Value("${snowflake.worker-id}") long workerId,
                                @Value("${snowflake.data-center-id}") long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID must be between 0 and %d", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("Data Center ID must be between 0 and %d", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * Generate a unique ID.
     * @return Unique ID
     */
    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常，请检查系统时间设置！");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * Get the next millisecond timestamp.
     * @param lastTimestamp Last timestamp
     * @return Next millisecond timestamp
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * Get the current timestamp.
     * @return Current timestamp
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
