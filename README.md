# HotelReservation

---

## TOC

<!-- TOC -->
* [HotelReservation](#hotelreservation)
  * [TOC](#toc)
  * [Project Description](#project-description)
  * [System Setup](#system-setup)
    * [Java/Maven Build system](#javamaven-build-system)
    * [Local Database Needs](#local-database-needs)
  * [Running the application](#running-the-application)
    * [Initial Setup (from fresh installation)](#initial-setup--from-fresh-installation-)
    * [Basic Functions](#basic-functions)
    * [Test Classes](#test-classes)
  * [Feature Highlights](#feature-highlights)
    * [Reserve a room](#reserve-a-room)
    * [Check in / Check out](#check-in--check-out)
<!-- TOC -->

---
## Project Description

HotelReservation is a project based on the idea of standing up and introducing a
simple web application for the management of a basic hotel and its room and 
reservation.  The application should be able to handle the following requirements:

As a Hotel guest, I should be able to...
- Create a guest account, and log in/log out.
- Modify their own guest account.
- Search available rooms for reservation and browse the information of a selected room.
- Make a new reservation and change/cancel an existing reservation.

As a hotel Clerk, I should be able to...
- Log in to the system using a username and a password.
- Modify their own profile information including password.
- View the status of all the rooms in the hotel.
- Modify a reservation.
- Process check-in/check-out of a guest.
- Generate billing information for any guest.
- Make a reservation requested by a guest.
- Cancel any reservation prior to the reservation start date.

As a Hotel Administrator, I should be able to...
- Log in to the system using a username and a password.
- Create a hotel clerk account which contains a username and a default password.
- Reset the user account password.
- Enter and modify the information of all rooms.

## System Setup

### Java/Maven Build system

- Java 17 JDK
- Maven Version 4.0
- Dependencies
```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
  </dependency>
  <dependency>
      <groupId>org.thymeleaf.extras</groupId>
      <artifactId>thymeleaf-extras-springsecurity6</artifactId>
      <!-- Temporary explicit version to fix Thymeleaf bug -->
      <version>3.1.1.RELEASE</version>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
      <groupId>org.mariadb.jdbc</groupId>
      <artifactId>mariadb-java-client</artifactId>
      <scope>runtime</scope>
  </dependency>
  <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
  <!--Used for in memory database done during testing-->
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <version>2.1.214</version>
  </dependency>
  <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <version>5.9.2</version>
      <scope>test</scope>
  </dependency>
  <dependency>
  <!--Used for controller testing-->
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>4.6.1</version>
      <scope>test</scope>
  </dependency>
```
- App properties
```
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MariaDBDialect
spring.datasource.url=jdbc:mariadb://${DB_ADDRESS}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.mvc.hiddenmethod.filter.enabled=true
```
- Required Environment Variables
```xml
DB_ADDRESS=<address to wherever DB is hosted for project>
DB_PORT=<port to connect to DB on>
DB_NAME=<name of database to be used on hosted machine>
DB_USERNAME=<username of user that can modify the connected DB>
DB_PASSWORD=<password for the above username>
```

### Local Database Needs

- Current project is built on MariaDB solution
  - Version 10.6.12
- Other Database may be used, but would require replacing the JDBC dependency
  - No testing has been done on code transfer with different DB Connector

## Running the application

### Initial Setup (from fresh installation)

- Make default admin
  - navigate to `<baseurl>/setup/admin` to generate an admin user
  - Admin user login details will be displayed on the screen for teh created user
  - This step only needs to be completed once
- Make your first clerk
  - Clerk accounts can only be made by the admin user
- Register as a guest
  - Navigate to the login page and click the option to register a new user
  - Registrations must be unique on `username` and `email address`

### Basic Functions

- A guest can reserve a room for themselves
- A clerk can reserve a room for a guest
  - When reserving a room as a clerk, the selection is via drop down to prevent errors
  - Would recommend changing to a fuzzy search or autocomplete option for larger user databases
- Admin can add and modify rooms
- Clerks can process check in and check out of users
- Payment for reservations are generated
  - Payment occurs in 3 steps
    - First, a "PENDING" step is generated when a user confirms
    - Second, the payment is updated when the user checks in with "deposit" information
    - Third, the payment is processed and finalized when the user checks out

### Test Classes

- MainControllerTest Class
- RoomControllerTest Class
- HotelUserControllerTest Class
- HotelUserServiceTest Class
- PaymentServiceTest Class
- ReservationControllerTest Class
- ReservationServiceTest Class

## Feature Highlights

The following sections will highlight some features of the applications and will
cover how to get the most out of each feature.

### Reserve a room

- Exclude rooms not available in the searched window
- See details of reservation and payment before confirming
- Ability to modify length of stay up to start date of reservation
- Ability to add a promotional code to improve the daily rate of the reservation
  - Currently only a hard coded promotional code is available
  - Able to be extended to a database backed list of codes
- Ability to cancel non-started or non-confirmed reservations
  - Can also remove reservation records that have "completed" without ever being confirmed or checked in

### Check in / Check out

- Clerk can check in a guest
  - any time up to last day of reservation
  - if check in after start date, does not alter payment details
- Clerk can check out a guest
  - When checking out, payment details are finalized

## Screenshots

//TODO - add screenshots of various steps here