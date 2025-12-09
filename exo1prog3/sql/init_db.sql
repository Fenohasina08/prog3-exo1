-- À exécuter avec un superutilisateur PostgreSQL

CREATE DATABASE product_management_db;

CREATE USER rafano WITH PASSWORD '123456';

GRANT CONNECT ON DATABASE product_management_db TO rafano;
