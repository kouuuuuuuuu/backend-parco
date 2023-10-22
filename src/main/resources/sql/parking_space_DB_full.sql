CREATE DATABASE IF NOT EXISTS parking_spaces_DB;
USE parking_spaces_DB;

CREATE TABLE IF NOT EXISTS PLO (
ploID VARCHAR(13) PRIMARY KEY,
balance DOUBLE NOT NULL,
phoneNumber VARCHAR(11) NOT NULL,
fullName NVARCHAR(256) NOT NULL,
password VARCHAR(200) NOT NULL,
email VARCHAR(100),
status INT NOT NULL,
identify VARCHAR(3000),
parkingName NVARCHAR(200) NOT NULL,
description NVARCHAR(1000),
address NVARCHAR(1000) NOT NULL,
latitude DECIMAL(10,6) NOT NULL,
longtitude DECIMAL(10,6) NOT NULL,
parkingStatusID INT,
slot INT NOT NULL,
currentSlot INT NOT NULL,
role VARCHAR(10) DEFAULT 'PLO',
length DOUBLE NOT NULL,
width DOUBLE NOT NULL,
waitingTime TIME ,
cancelBookingTime TIME,
contractLink VARCHAR(3000),
registerContract TIMESTAMP,
browseContract TIMESTAMP,
contractDuration TIMESTAMP,
star INT
);

CREATE TABLE IF NOT EXISTS Customer (
customerID VARCHAR(13) PRIMARY KEY,
wallet_balance DOUBLE NOT NULL,
identify VARCHAR(3000),
role VARCHAR(10) DEFAULT 'CUSTOMER',
phoneNumber VARCHAR(11) NOT NULL,
fullName NVARCHAR(256) NOT NULL,
password VARCHAR(200) NOT NULL,
email VARCHAR(100),
registrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
status INT
);

CREATE TABLE IF NOT EXISTS CustomerTransaction (
transactionID INT AUTO_INCREMENT PRIMARY KEY,
depositAmount DOUBLE NOT NULL,
rechargeTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
customerID VARCHAR(13),
bankCode VARCHAR(100),
status INT
);

CREATE TABLE IF NOT EXISTS Reservation (
reservationID INT AUTO_INCREMENT PRIMARY KEY,
customerID VARCHAR(13) NOT NULL,
ploID VARCHAR(13) NOT NULL,
statusID INT NOT NULL,
licensePlateID INT NOT NULL,
startTime TIMESTAMP NOT NULL,
endTime TIMESTAMP NOT NULL,
price DOUBLE NOT NULL,
checkOut TIMESTAMP ,
checkIn TIMESTAMP 
);

CREATE TABLE IF NOT EXISTS ReservationStatus (
statusID INT AUTO_INCREMENT PRIMARY KEY,
statusName NVARCHAR(200)
);

CREATE TABLE IF NOT EXISTS LicensePlate (
licensePlateID INT AUTO_INCREMENT PRIMARY KEY,
customerID VARCHAR(13),
licensePlate VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS Rating (
ratingID INT AUTO_INCREMENT PRIMARY KEY,
star INT,
content NVARCHAR(500),
customerID VARCHAR(13),
ploID VARCHAR(13),
reservationID INT,
feedbackDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Notifications (
notiID INT AUTO_INCREMENT PRIMARY KEY,
recipient_type VARCHAR(10),
recipient_id VARCHAR(13),
sender_type VARCHAR(10),
sender_id VARCHAR(13),
conten NVARCHAR(1000),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP	
);

CREATE TABLE IF NOT EXISTS ReservationMethod (
methodID INT AUTO_INCREMENT PRIMARY KEY,
methodName NVARCHAR(50),
startTime TIME,
endTime TIME
);

CREATE TABLE IF NOT EXISTS ParkingMethod (
parkingMethodID INT AUTO_INCREMENT PRIMARY KEY,
ploID VARCHAR(13),
methodID INT,
price DOUBLE
);

CREATE TABLE IF NOT EXISTS Admin (
adminID VARCHAR(10) PRIMARY KEY,
image VARCHAR(3000),
role VARCHAR(10) DEFAULT 'ADMIN',
phoneNumber VARCHAR(11) NOT NULL,
fullName NVARCHAR(256) NOT NULL,
password VARCHAR(200) NOT NULL,
email VARCHAR(100),
status INT	
);

CREATE TABLE IF NOT EXISTS Image (
imageID INT AUTO_INCREMENT PRIMARY KEY,
ploID VARCHAR(13),
imageLink VARCHAR(3000)
);

CREATE TABLE IF NOT EXISTS PloTransaction (
historyID INT AUTO_INCREMENT PRIMARY KEY,
method NVARCHAR(1000),
ploID VARCHAR(13),
status INT,
depositAmount DOUBLE,
transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
transactionResultDate TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ParkingStatus (
parkingStatusID INT AUTO_INCREMENT PRIMARY KEY,
statusName NVARCHAR(50)
);

-- Customer Transaction
ALTER TABLE CustomerTransaction
ADD CONSTRAINT FK_CustomerTransaction_Customer
FOREIGN KEY (customerID) 
REFERENCES Customer(customerID);

-- License Plate
ALTER TABLE LicensePlate
ADD CONSTRAINT FK_LicensePlate_Customer
FOREIGN KEY (customerID) 
REFERENCES Customer(customerID);

-- Reservation
ALTER TABLE Reservation
ADD CONSTRAINT FK_Reservation_Customer
FOREIGN KEY (customerID) 
REFERENCES Customer(customerID),
ADD CONSTRAINT FK_Reservation_PLO
FOREIGN KEY (ploID) 
REFERENCES PLO(ploID),
ADD CONSTRAINT FK_Reservation_ReservationStatus
FOREIGN KEY (statusID) 
REFERENCES ReservationStatus(statusID),
ADD CONSTRAINT FK_Reservation_LicensePlate
FOREIGN KEY (licensePlateID) 
REFERENCES LicensePlate(licensePlateID);

-- Rating 
ALTER TABLE Rating
ADD CONSTRAINT FK_Rating_Customer
FOREIGN KEY (customerID) 
REFERENCES Customer(customerID),
ADD CONSTRAINT FK_Rating_PLO
FOREIGN KEY (ploID) 
REFERENCES PLO(ploID),
ADD CONSTRAINT FK_Rating_Reservation
FOREIGN KEY (reservationID) 
REFERENCES Reservation(reservationID);

-- PLO 
ALTER TABLE PLO
ADD CONSTRAINT FK_PLO_ParkingStatus
FOREIGN KEY (parkingStatusID) 
REFERENCES ParkingStatus(parkingStatusID);

-- ParkingMethod
ALTER TABLE ParkingMethod
ADD CONSTRAINT FK_ParkingMethod_PLO
FOREIGN KEY (ploID) 
REFERENCES PLO(ploID),
ADD CONSTRAINT FK_ParkingMethod_ReservationMethod
FOREIGN KEY (methodID) 
REFERENCES ReservationMethod(methodID);

-- Image
ALTER TABLE Image
ADD CONSTRAINT FK_Image_PLO
FOREIGN KEY (ploID) 
REFERENCES PLO(ploID);

-- PloTransaction
ALTER TABLE PloTransaction
ADD CONSTRAINT FK_PloTransaction_PLO
FOREIGN KEY (ploID) 
REFERENCES PLO(ploID);


-- INSERT INTO PLO (ploID, balance, phoneNumber, fullName, password, email, status, identify, parkingName, description, address, latitude, longtitude, parkingStatusID, slot, role, length, width, waitingTime, cancelBookingTime, contractLink)
-- VALUES
-- ('PLO000000001', 500.0, '1234567891', 'Nguyễn Thị B', 'hashed_password_1', 'nguyen.thi.b@example.com', 1, 'some_identity_data_1', 'Parking B', 'Parking B description', '456 Park Street, City', 12.345679, 98.765433, 1, 40, 'plo', 5.0, 2.0, '09:00:00', '13:00:00', 'https://example.com/contract1'),

-- ('PLO000000002', 750.0, '1234567892', 'Trần Văn C', 'hashed_password_2', 'tran.van.c@example.com', 1, 'some_identity_data_2', 'Parking C', 'Parking C description', '789 Park Street, City', 12.345670, 98.765434, 1, 30, 'plo', 4.5, 2.2, '10:00:00', '14:00:00', 'https://example.com/contract2'),

-- ('PLO000000003', 1000.0, '1234567893', 'Lê Thị D', 'hashed_password_3', 'le.thi.d@example.com', 1, 'some_identity_data_3', 'Parking D', 'Parking D description', '101 Park Street, City', 12.345671, 98.765435, 1, 25, 'plo', 4.0, 1.8, '11:00:00', '15:00:00', 'https://example.com/contract3'),

-- ('PLO000000004', 600.0, '1234567894', 'Phạm Văn E', 'hashed_password_4', 'pham.van.e@example.com', 1, 'some_identity_data_4', 'Parking E', 'Parking E description', '202 Park Street, City', 12.345672, 98.765436, 1, 35, 'plo', 4.8, 2.1, '08:30:00', '12:30:00', 'https://example.com/contract4'),

-- ('PLO000000005', 900.0, '1234567895', 'Nguyễn Văn F', 'hashed_password_5', 'nguyen.van.f@example.com', 1, 'some_identity_data_5', 'Parking F', 'Parking F description', '303 Park Street, City', 12.345673, 98.765437, 1, 45, 'plo', 5.2, 2.4, '09:30:00', '13:30:00', 'https://example.com/contract5'),

-- ('PLO000000006', 800.0, '1234567896', 'Hoàng Thị G', 'hashed_password_6', 'hoang.thi.g@example.com', 1, 'some_identity_data_6', 'Parking G', 'Parking G description', '404 Park Street, City', 12.345674, 98.765438, 1, 28, 'plo', 4.3, 2.0, '10:30:00', '14:30:00', 'https://example.com/contract6'),

-- ('PLO000000007', 1200.0, '1234567897', 'Trần Văn H', 'hashed_password_7', 'tran.van.h@example.com', 1, 'some_identity_data_7', 'Parking H', 'Parking H description', '505 Park Street, City', 12.345675, 98.765439, 1, 60, 'plo', 5.5, 2.7, '11:30:00', '15:30:00', 'https://example.com/contract7'),

-- ('PLO000000008', 950.0, '1234567898', 'Lê Thị I', 'hashed_password_8', 'le.thi.i@example.com', 1, 'some_identity_data_8', 'Parking I', 'Parking I description', '606 Park Street, City', 12.345676, 98.765440, 1, 38, 'plo', 4.6, 2.3, '08:45:00', '12:45:00', 'https://example.com/contract8'),

-- ('PLO000000009', 1100.0, '1234567899', 'Phạm Văn J', 'hashed_password_9', 'pham.van.j@example.com', 1, 'some_identity_data_9', 'Parking J', 'Parking J description', '707 Park Street, City', 12.345677, 98.765441, 1, 55, 'plo', 5.0, 2.6, '09:45:00', '13:45:00', 'https://example.com/contract9'),

-- ('PLO000000010', 850.0, '1234567800', 'Nguyễn Thị K', 'hashed_password_10', 'nguyen.thi.k@example.com', 1, 'some_identity_data_10', 'Parking K', 'Parking K description', '808 Park Street, City', 12.345678, 98.765442, 1, 42, 'plo', 4.7, 2.2, '10:45:00', '14:45:00', 'https://example.com/contract10');
--  
-- INSERT INTO Customer (customerID, wallet_balance, identify, role, phoneNumber, fullName, password, email, registrationDate, registerDate, status)
-- VALUES
-- ('CUST000000001', 500.0, 'some_identity_data_1', 'customer', '1234567891', 'Nguyễn Thị B', 'hashed_password_1', 'nguyen.thi.b@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000002', 750.0, 'some_identity_data_2', 'customer', '1234567892', 'Trần Văn C', 'hashed_password_2', 'tran.van.c@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000003', 1000.0, 'some_identity_data_3', 'customer', '1234567893', 'Lê Thị D', 'hashed_password_3', 'le.thi.d@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000004', 600.0, 'some_identity_data_4', 'customer', '1234567894', 'Phạm Văn E', 'hashed_password_4', 'pham.van.e@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000005', 900.0, 'some_identity_data_5', 'customer', '1234567895', 'Nguyễn Văn F', 'hashed_password_5', 'nguyen.van.f@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000006', 800.0, 'some_identity_data_6', 'customer', '1234567896', 'Hoàng Thị G', 'hashed_password_6', 'hoang.thi.g@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000007', 1200.0, 'some_identity_data_7', 'customer', '1234567897', 'Trần Văn H', 'hashed_password_7', 'tran.van.h@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000008', 950.0, 'some_identity_data_8', 'customer', '1234567898', 'Lê Thị I', 'hashed_password_8', 'le.thi.i@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000009', 1100.0, 'some_identity_data_9', 'customer', '1234567899', 'Phạm Văn J', 'hashed_password_9', 'pham.van.j@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),

-- ('CUST000000010', 850.0, 'some_identity_data_10', 'customer', '1234567800', 'Nguyễn Thị K', 'hashed_password_10', 'nguyen.thi.k@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- INSERT INTO CustomerTransaction.xml (depositAmount, customerID, bankCode, status)
-- VALUES
-- (100.0, 'CUST000000001', 'XYZBank', 1),

-- (200.0, 'CUST000000002', 'ABCBank', 1),

-- (150.0, 'CUST000000003', 'XYZBank', 1),

-- (300.0, 'CUST000000004', 'XYZBank', 1),

-- (250.0, 'CUST000000005', 'ABCBank', 1),

-- (180.0, 'CUST000000006', 'XYZBank', 1),

-- (400.0, 'CUST000000007', 'XYZBank', 1),

-- (280.0, 'CUST000000008', 'ABCBank', 1),

-- (350.0, 'CUST000000009', 'XYZBank', 1),

-- (220.0, 'CUST000000010', 'ABCBank', 1);

-- INSERT INTO Reservation (customerID, ploID, statusID, licensePlateID, price)
-- VALUES
-- ('CUST000000001', 'PLO000000001', 1, 1, 10.0),

-- ('CUST000000002', 'PLO000000002', 1, 2, 15.0),

-- ('CUST000000003', 'PLO000000003', 1, 3, 12.0),

-- ('CUST000000004', 'PLO000000004', 1, 4, 18.0),

-- ('CUST000000005', 'PLO000000005', 1, 5, 20.0),

-- ('CUST000000006', 'PLO000000006', 1, 6, 14.0),

-- ('CUST000000007', 'PLO000000007', 1, 7, 25.0),

-- ('CUST000000008', 'PLO000000008', 1, 8, 22.0),

-- ('CUST000000009', 'PLO000000009', 1, 9, 28.0),

-- ('CUST000000010', 'PLO000000010', 1, 10, 30.0);

-- INSERT INTO ReservationStatus (statusName)
-- VALUES
-- ('Đã đặt'),

-- ('Đang sử dụng'),

-- ('Hoàn thành'),

-- ('Hủy bỏ');

-- INSERT INTO LicensePlate (customerID, licensePlate)
-- VALUES
-- ('CUST000000001', 'ABC123'),

-- ('CUST000000002', 'XYZ789'),

-- ('CUST000000003', 'DEF456'),

-- ('CUST000000004', 'GHI789'),

-- ('CUST000000005', 'JKL123'),

-- ('CUST000000006', 'MNO456'),

-- ('CUST000000007', 'PQR789'),

-- ('CUST000000008', 'STU123'),

-- ('CUST000000009', 'VWX456'),

-- ('CUST000000010', 'YZA789');

-- INSERT INTO Rating (star, content, customerID, ploID, reservationID)
-- VALUES
-- (4, 'Dịch vụ tốt', 'CUST000000001', 'PLO000000001', 1),

-- (5, 'Rất hài lòng', 'CUST000000002', 'PLO000000002', 2),

-- (3, 'Chưa thực sự ấn tượng', 'CUST000000003', 'PLO000000003', 3),

-- (5, 'Dịch vụ xuất sắc', 'CUST000000004', 'PLO000000004', 4),

-- (4, 'Gửi xe nhanh chóng', 'CUST000000005', 'PLO000000005', 5),

-- (5, 'Đỗ xe dễ dàng', 'CUST000000006', 'PLO000000006', 6),

-- (3, 'Phục vụ chưa được tốt', 'CUST000000007', 'PLO000000007', 7),

-- (4, 'Gửi xe nhanh', 'CUST000000008', 'PLO000000008', 8),

-- (5, 'Chỗ đỗ rộng rãi', 'CUST000000009', 'PLO000000009', 9),

-- (4, 'Dịch vụ tốt', 'CUST000000010', 'PLO000000010', 10);

-- INSERT INTO Notifications (recipient_type, recipient_id, sender_type, sender_id, conten)
-- VALUES
-- ('customer', 'CUST000000001', 'system', 'SYS000000001', 'Chào mừng bạn đến với hệ thống của chúng tôi!'),

-- ('customer', 'CUST000000002', 'system', 'SYS000000001', 'Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.'),

-- ('customer', 'CUST000000003', 'system', 'SYS000000001', 'Hãy liên hệ với chúng tôi nếu bạn có bất kỳ câu hỏi nào.'),

-- ('customer', 'CUST000000004', 'system', 'SYS000000001', 'Thông báo quan trọng: Lịch đặt chỗ của bạn đã được xác nhận.'),

-- ('customer', 'CUST000000005', 'system', 'SYS000000001', 'Chúc bạn một ngày tốt lành!'),

-- ('customer', 'CUST000000006', 'system', 'SYS000000001', 'Dịch vụ của chúng tôi luôn sẵn sàng phục vụ bạn.'),

-- ('customer', 'CUST000000007', 'system', 'SYS000000001', 'Xin lỗi vì sự bất tiện này, chúng tôi đang nâng cấp hệ thống.'),

-- ('customer', 'CUST000000008', 'system', 'SYS000000001', 'Hãy đánh giá dịch vụ của chúng tôi sau khi sử dụng.'),

-- ('customer', 'CUST000000009', 'system', 'SYS000000001', 'Cảm ơn bạn đã đặt chỗ với chúng tôi.'),

-- ('customer', 'CUST000000010', 'system', 'SYS000000001', 'Sản phẩm và dịch vụ của chúng tôi luôn luôn được cải thiện.');

-- INSERT INTO ReservationMethod (methodName, startTime, endTime)
-- VALUES
-- ('Phương thức A', '08:00:00', '18:00:00'),

-- ('Phương thức B', '09:00:00', '17:00:00'),

-- ('Phương thức C', '10:00:00', '16:00:00'),

-- ('Phương thức D', '07:00:00', '19:00:00'),

-- ('Phương thức E', '08:30:00', '17:30:00'),

-- ('Phương thức F', '09:30:00', '16:30:00'),

-- ('Phương thức G', '07:30:00', '18:30:00'),

-- ('Phương thức H', '08:45:00', '17:45:00'),

-- ('Phương thức I', '10:30:00', '16:30:00'),

-- ('Phương thức J', '09:45:00', '18:45:00');

-- INSERT INTO ParkingMethod (ploID, methodID, price)
-- VALUES
-- ('PLO000000001', 1, 10.0),

-- ('PLO000000002', 2, 12.0),

-- ('PLO000000003', 3, 11.0),

-- ('PLO000000004', 4, 14.0),

-- ('PLO000000005', 5, 15.0),

-- ('PLO000000006', 6, 13.0),

-- ('PLO000000007', 7, 16.0),

-- ('PLO000000008', 8, 17.0),

-- ('PLO000000009', 9, 18.0),

-- ('PLO000000010', 10, 19.0);

-- INSERT INTO Admin (adminID, role, phoneNumber, fullName, password, email, status)
-- VALUES
-- ('ADMIN00001', 'superadmin', '1234567890', 'Super Admin', 'hashed_password_1', 'superadmin@example.com', 1),

-- ('ADMIN00002', 'admin', '9876543210', 'Admin 1', 'hashed_password_2', 'admin1@example.com', 1),

-- ('ADMIN00003', 'admin', '4567890123', 'Admin 2', 'hashed_password_3', 'admin2@example.com', 1),

-- ('ADMIN00004', 'admin', '3216549870', 'Admin 3', 'hashed_password_4', 'admin3@example.com', 1),

-- ('ADMIN00005', 'admin', '7890123456', 'Admin 4', 'hashed_password_5', 'admin4@example.com', 1),

-- ('ADMIN00006', 'admin', '2345678901', 'Admin 5', 'hashed_password_6', 'admin5@example.com', 1),

-- ('ADMIN00007', 'admin', '8901234567', 'Admin 6', 'hashed_password_7', 'admin6@example.com', 1),

-- ('ADMIN00008', 'admin', '5678901234', 'Admin 7', 'hashed_password_8', 'admin7@example.com', 1),

-- ('ADMIN00009', 'admin', '0123456789', 'Admin 8', 'hashed_password_9', 'admin8@example.com', 1),

-- ('ADMIN00010', 'admin', '4567123456', 'Admin 9', 'hashed_password_10', 'admin9@example.com', 1);

-- INSERT INTO Image (ploID, imageLink)
-- VALUES
-- ('PLO000000001', 'https://example.com/images/plo1.jpg'),

-- ('PLO000000002', 'https://example.com/images/plo2.jpg'),

-- ('PLO000000003', 'https://example.com/images/plo3.jpg'),

-- ('PLO000000004', 'https://example.com/images/plo4.jpg'),

-- ('PLO000000005', 'https://example.com/images/plo5.jpg'),

-- ('PLO000000006', 'https://example.com/images/plo6.jpg'),

-- ('PLO000000007', 'https://example.com/images/plo7.jpg'),

-- ('PLO000000008', 'https://example.com/images/plo8.jpg'),

-- ('PLO000000009', 'https://example.com/images/plo9.jpg'),

-- ('PLO000000010', 'https://example.com/images/plo10.jpg');

-- INSERT INTO PloTransaction (method, ploID, status, depositAmount)
-- VALUES
-- ('Nạp tiền từ ngân hàng', 'PLO000000001', 1, 1000.0),

-- ('Nạp tiền từ thẻ tín dụng', 'PLO000000002', 1, 1500.0),

-- ('Nạp tiền từ ví điện tử', 'PLO000000003', 1, 800.0),

-- ('Nạp tiền từ thẻ quà tặng', 'PLO000000004', 1, 1200.0),

-- ('Nạp tiền từ ví điện tử', 'PLO000000005', 1, 900.0),

-- ('Nạp tiền từ ngân hàng', 'PLO000000006', 1, 2000.0),

-- ('Nạp tiền từ thẻ tín dụng', 'PLO000000007', 1, 600.0),

-- ('Nạp tiền từ ngân hàng', 'PLO000000008', 1, 1300.0),

-- ('Nạp tiền từ thẻ quà tặng', 'PLO000000009', 1, 1100.0),

-- ('Nạp tiền từ thẻ tín dụng', 'PLO000000010', 1, 700.0);

-- INSERT INTO ParkingStatus (statusName)
-- VALUES
-- ('Trống'),

-- ('Đã đặt'),

-- ('Đang sử dụng'),

-- ('Bảo trì'),

-- ('Hết chỗ'),

-- ('Không hoạt động');


INSERT INTO PLO (ploID, balance, phoneNumber, fullName, password, email, status, identify, parkingName, description, address, latitude, longtitude, parkingStatusID, slot, role, length, width, waitingTime, cancelBookingTime, contractLink, registerContract, browseContract, contractDuration)
VALUES
('PLO1234567890', 500.00, '1234567890', 'PLO User 1', 'password1', 'user1@example.com', 1, 'PLO Identify 1', 'Parking 1', 'Description 1', 'Address 1', 12.345678, 98.765432, NULL, 100, 'PLO', 5.0, 2.5, '08:00:00', '17:00:00', 'http://contractlink1.com', NOW(), NOW(), NOW()),
('PLO0987654321', 300.00, '0987654321', 'PLO User 2', 'password2', 'user2@example.com', 1, 'PLO Identify 2', 'Parking 2', 'Description 2', 'Address 2', 11.111111, 99.999999, NULL, 50, 'PLO', 4.5, 3.0, '09:00:00', '18:00:00', 'http://contractlink2.com', NOW(), NOW(), NOW());
INSERT INTO Customer (customerID, wallet_balance, identify, role, phoneNumber, fullName, password, email, registrationDate, status)
VALUES
('CUS123456789', 200.00, 'Customer Identify 1', 'CUSTOMER', '0123456789', 'Customer 1', 'password1', 'customer1@example.com', NOW(), 1),
('CUS987654321', 150.00, 'Customer Identify 2', 'CUSTOMER', '0987654321', 'Customer 2', 'password2', 'customer2@example.com', NOW(), 1);
INSERT INTO Admin (adminID, role, phoneNumber, fullName, password, email, status)
VALUES
('ADMIN1', 'ADMIN', 'admin1', 'Admin 1', 'adminpassword1', 'admin1@example.com', 1),
('ADMIN2', 'ADMIN', 'admin2', 'Admin 2', 'adminpassword2', 'admin2@example.com', 1);


 