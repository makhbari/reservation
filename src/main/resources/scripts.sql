-- جدول زمان‌های خالی
CREATE TABLE available_slots
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY, -- شناسه یکتا برای هر زمان خالی
    start_time  DATETIME NOT NULL,                 -- زمان شروع
    end_time    DATETIME NOT NULL,                 -- زمان پایان
    is_reserved BOOLEAN DEFAULT FALSE,             -- وضعیت رزرو (پیش‌فرض: خالی)
    version     BIGINT  DEFAULT 0
);

-- جدول کاربران
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY, -- شناسه یکتا برای هر کاربر
    username   VARCHAR(255) NOT NULL UNIQUE,      -- نام کاربری
    email      VARCHAR(255) NOT NULL UNIQUE,      -- ایمیل کاربر
    password   VARCHAR(255) NOT NULL,             -- رمز عبور کاربر (هش شده)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP -- زمان ثبت نام کاربر
);

-- جدول رزروهای کاربران
CREATE TABLE reservation
(
    ID         BIGINT AUTO_INCREMENT PRIMARY KEY, -- شناسه یکتا برای هر رزرو کاربر
    userId     BIGINT       NOT NULL,             -- شناسه یکتا برای هر کاربر
    start_time TIMESTAMP    NOT NULL,             -- زمان شروع رزرو
    end_time   TIMESTAMP    NOT NULL,             -- زمان پایان رزرو
    status     VARCHAR(255) NOT NULL,             -- وضعیت رزرو
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP    NOT NULL,
    FOREIGN KEY (userId) REFERENCES users (id)
);

-- جدول لفو رزروهای کاربران
CREATE TABLE CancelReservation
(
    ID                  BIGINT AUTO_INCREMENT PRIMARY KEY, -- شناسه یکتا برای هر لغو رزرو کاربر
    userId              BIGINT    NOT NULL,                -- شناسه یکتا برای هر کاربر
    reservationId       BIGINT    NOT NULL,                -- شناسه یکتا برای هر رزرو کاربر
    cancellation_time   TIMESTAMP NOT NULL,                -- زمان لغو رزرو کاربر
    cancellation_reason VARCHAR(255),                      -- دلیل لغو رزرو کاربر
    description         VARCHAR(255),                      -- توضیحات
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at          TIMESTAMP NOT NULL,
    FOREIGN KEY (userId) REFERENCES users (id),
    FOREIGN KEY (reservationId) REFERENCES Reservation (id)
);

INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 09:00:00', '2025-02-12 10:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 10:00:00', '2025-02-12 11:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 11:00:00', '2025-02-12 12:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 12:00:00', '2025-02-12 13:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 13:00:00', '2025-02-12 14:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 14:00:00', '2025-02-12 15:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 15:00:00', '2025-02-12 16:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 16:00:00', '2025-02-12 17:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-12 09:00:00', '2025-02-12 10:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-13 16:00:00', '2025-02-13 17:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-13 09:00:00', '2025-02-13 10:00:00', FALSE, 0);
INSERT INTO available_slots (start_time, end_time, is_reserved, version)
VALUES ('2025-02-13 10:00:00', '2025-02-13 11:00:00', FALSE, 0);