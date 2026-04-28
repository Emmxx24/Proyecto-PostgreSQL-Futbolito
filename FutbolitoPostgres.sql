/*Creacion de la Base de Datos  */
CREATE DATABASE "Futbolito"
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

/*Creación de Esquemas */
CREATE SCHEMA Persona;
CREATE SCHEMA Juego;
CREATE SCHEMA Evento;
CREATE SCHEMA Club;

/*Tablas del Esquema Persona Participante/Jugador/Arbitro */

CREATE TABLE Persona.Participante (
    IdParticipante BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NombreParticipante VARCHAR(50) NOT NULL,
    Genero VARCHAR(10) NOT NULL,
    Telefono VARCHAR(10) NOT NULL UNIQUE,
    CorreoElectronico VARCHAR(50) NOT NULL UNIQUE,
    FechaNacimiento DATE NOT NULL,
    Edad INT
);

CREATE TABLE Persona.Arbitro (
    IdArbitro BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdParticipante BIGINT NOT NULL UNIQUE,
    CedulaArbitro VARCHAR(15) NOT NULL UNIQUE,
    CONSTRAINT FK_ARBITRO_PARTICIPANTE FOREIGN KEY (IdParticipante) 
        REFERENCES Persona.Participante (IdParticipante)
);

CREATE TABLE Persona.Jugador (
    IdJugador BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdParticipante BIGINT NOT NULL UNIQUE,
    Posicion VARCHAR(50) NOT NULL,
    Numero INT NOT NULL,
    TipoSangre VARCHAR(10) NOT NULL,
    AcumuladorAmarillas INT DEFAULT 0,
    Estado VARCHAR(15) DEFAULT 'Activo',
    CONSTRAINT FK_JUGADOR_PARTICIPANTE FOREIGN KEY (IdParticipante) 
        REFERENCES Persona.Participante (IdParticipante)
);

/*Tablas del Esquema Juego Lugar/Torneo/Jornada */
CREATE TABLE Juego.Lugar (
    IdLugar BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL UNIQUE,
    Ubicacion VARCHAR(50) NOT NULL UNIQUE,
    Capacidad INT NOT NULL
);

CREATE TABLE Juego.Torneo (
    IdTorneo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NombreTorneo VARCHAR(50) NOT NULL UNIQUE,
    EdadMin INT NOT NULL,
    EdadMax INT NOT NULL,
    Genero VARCHAR(50) NOT NULL,
    FechaInicio DATE NOT NULL,
    FechaFin DATE NOT NULL,
    CantEquipos INT,
    NumJornadas INT
);

CREATE TABLE Juego.DetalleTorneo (
    IdTorneo BIGINT NOT NULL,
    IdEquipo BIGINT NOT NULL,
    CONSTRAINT FK_DETALLETORNEO_EQUIPO FOREIGN KEY (IdEquipo) 
        REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_DETALLETORNEO_TORNEO FOREIGN KEY (IdTorneo) 
        REFERENCES Juego.Torneo (IdTorneo)
);

CREATE TABLE Juego.Jornada (
    IdJornada BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdTorneo BIGINT NOT NULL,
    NumeroJornada INT NOT NULL,
    CONSTRAINT FK_JORNADA_TORNEO FOREIGN KEY (IdTorneo) 
        REFERENCES Juego.Torneo (IdTorneo)
);

/*Tablas del Esquema Club Equipo/DetalleEquipo */

CREATE TABLE Club.Equipo (
    IdEquipo BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NombreEquipo VARCHAR(50) NOT NULL UNIQUE,
    Logo VARCHAR(500) NOT NULL,
    CantJugadores INT
);

CREATE TABLE Club.DetalleEquipo (
    IdEquipo BIGINT NOT NULL,
    IdJugador BIGINT NOT NULL,
    CONSTRAINT FK_DETALLEEQUIPO_EQUIPO FOREIGN KEY (IdEquipo) 
        REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_DETALLEEQUIPO_JUGADOR FOREIGN KEY (IdJugador) 
        REFERENCES Persona.Jugador (IdJugador)
);

/* 5. Tablas de Evento Partido/ResultadoPartido/Gol/Tarjeta */

CREATE TABLE Evento.Partido (
    IdPartido BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdArbitro BIGINT NOT NULL,
    IdJornada BIGINT NOT NULL,
    IdLugar BIGINT NOT NULL,
    IdLocal BIGINT NOT NULL,
    IdVisitante BIGINT NOT NULL,
    Fecha DATE NOT NULL,
    HoraInicio TIME NOT NULL,
    Estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT FK_PARTIDO_ARBITRO FOREIGN KEY (IdArbitro) REFERENCES Persona.Arbitro (IdArbitro),
    CONSTRAINT FK_PARTIDO_JORNADA FOREIGN KEY (IdJornada) REFERENCES Juego.Jornada (IdJornada),
    CONSTRAINT FK_PARTIDO_LUGAR FOREIGN KEY (IdLugar) REFERENCES Juego.Lugar (IdLugar),
    CONSTRAINT FK_PARTIDO_EQLOCAL FOREIGN KEY (IdLocal) REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_PARTIDO_EQVISIT FOREIGN KEY (IdVisitante) REFERENCES Club.Equipo (IdEquipo)
);

CREATE TABLE Evento.ResultadoPartido (
    IdResultado BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdPartido BIGINT NOT NULL,
    GolesLocal SMALLINT NOT NULL,
    GolesVisitante SMALLINT NOT NULL,
    HoraFin TIME NOT NULL,
    CONSTRAINT FK_RESULTADO_PARTIDO FOREIGN KEY (IdPartido) REFERENCES Evento.Partido (IdPartido)
);

CREATE TABLE Evento.Gol (
    IdGol BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    Minuto SMALLINT NOT NULL,
    CONSTRAINT FK_GOL_JUGADOR FOREIGN KEY (IdJugador) REFERENCES Persona.Jugador (IdJugador),
    CONSTRAINT FK_GOLD_PARTIDO FOREIGN KEY (IdPartido) REFERENCES Evento.Partido (IdPartido)
);

CREATE TABLE Evento.Tarjeta (
    IdTarjeta BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    TipoTarjeta VARCHAR(10) NOT NULL,
    Minuto SMALLINT NOT NULL,
    CONSTRAINT FK_TARJETA_JUGADOR FOREIGN KEY (IdJugador) REFERENCES Persona.Jugador (IdJugador),
    CONSTRAINT FK_TARJETA_PARTIDO FOREIGN KEY (IdPartido) REFERENCES Evento.Partido (IdPartido)
);