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
    * [Configuring Environment variables](#configuring-environment-variables)
  * [Running the application](#running-the-application)
    * [Initial Setup (from fresh installation)](#initial-setup--from-fresh-installation-)
    * [Basic Functions](#basic-functions)
    * [Tests](#tests)
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
- lorem ipsum
- ipsum lorem
- lorem ipsum

As a hotel Clerk, I should be able to...
- lorem ipsum
- ipsum lorem
- lorem ipsum

As a Hotel Administrator, I should be able to...
- lorem ipsum
- ipsum lorem
- lorem ipsum

## System Setup

### Java/Maven Build system

  - Dependencies
  - App properties

### Local Database Needs

  - Installing mariaDB (include version)

### Configuring Environment variables

  - Set up environment variables to run

## Running the application

### Initial Setup (from fresh installation)

  - Make default admin
  - Make your first clerk
  - Register as a guest

### Basic Functions

### Tests

## Feature Highlights

The following sections will highlight some features of the applications and will
cover how to get the most out of each feature.

### Reserve a room

  - Exclude rooms not available in the searched window
  - See details of reservation and payment before confirming
  - Ability to modify length of stay up to start date of reservation

### Check in / Check out

  - Clerk can check in a guest
    - any time up to last day of reservation
    - if check in after start date, does not alter payment details
  - Clerk can check out a guest
    - When checking out, payment details are finalized