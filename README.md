# Savoria Restaurant Management System

A desktop restaurant management application built with Java Swing and SQLite.

## Features

- Multi-screen Swing user interface for customer and admin workflows
- Order placement and order-status tracking
- Table booking and booking-status management
- Branch-wise menu management
- SQLite-backed persistence with automatic schema initialization on first run

## Tech Stack

- Java (Swing, JDBC)
- SQLite

## Project Structure

- SavoriaApp.java: Main source file (UI, models, and database helper)
- savoria_rms.db: SQLite database file used at runtime
- sqlite-jdbc-3.47.1.0.jar: SQLite JDBC driver
- mysql-connector-j.jar: MySQL driver (optional / legacy)
- SavoriaRMS/: Packaged desktop app output (exe/runtime)

## Prerequisites

- JDK 17 or later
- Windows PowerShell (for Windows commands below)

## Run From Source (Windows)

1. Compile

   javac SavoriaApp.java

2. Run

   java -cp .;sqlite-jdbc-3.47.1.0.jar;mysql-connector-j.jar SavoriaApp

## Run Packaged App

- Launch SavoriaRMS/SavoriaRMS.exe

## Database Notes

- The app uses SQLite with file name savoria_rms.db.
- If the database does not exist, tables and seed data are created on first run.

## GitHub Setup

1. Initialize git

   git init

2. Add files

   git add .

3. First commit

   git commit -m "Initial commit"

4. Add remote

   git remote add origin https://github.com/<your-username>/<your-repo>.git

5. Push

   git branch -M main
   git push -u origin main

