DELETE FROM reservation;
DELETE FROM hoteluser;
DELETE FROM room;

insert into hoteluser
    (id,
     city,
     email,
     firstName,
     lastName,
     password,
     phoneNumber,
     role,
     state,
     streetAddress,
     username,
     zipCode)
values (
        99,
        'city',
        'email',
        'firstName',
        'lastName',
        'password',
        'phone',
        'HOTEL_GUEST',
        'state',
        'street',
        'userName',
        'zipCo'
       );
insert into room
    (id,
     baseRate,
     bedNumber,
     bedType,
     quality,
     roomNumber,
     smokingStatus)
values (
        99,
        1,
        1,
        'bed',
        'quality',
        99,
        'smoking'
       );
insert into reservation
    (
     id,
     dailyRate,
     endDate,
     isConfirmed,
     isNotStarted,
     isPromoApplied,
     numDays,
     startDate,
     totalRate,
     guest_id,
     room_id
     )
values (
        99,
        100,
        '2000-01-01',
        0,
        0,
        0,
        10,
        '2000-01-01',
        1000,
        99,
        99
       );
