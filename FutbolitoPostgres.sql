/*Creacion de la Base de Datos  */
CREATE DATABASE "Futbolito"
WITH 
OWNER = postgres
ENCODING = 'UTF8'
LC_COLLATE = 'Spanish_Spain.1252'
LC_CTYPE = 'Spanish_Spain.1252'
LOCALE_PROVIDER = 'libc'
TABLESPACE = pg_default
CONNECTION LIMIT = -1
IS_TEMPLATE = False;


/*Creación de Esquemas */
CREATE SCHEMA Persona;
CREATE SCHEMA Juego;
CREATE SCHEMA Evento;
CREATE SCHEMA Club;

/*====================================================
 TABLAS DEL ESQUEMA PERSONA
====================================================*/

CREATE TABLE Persona.Participante (
    IdParticipante BIGSERIAL NOT NULL,
    NombreParticipante VARCHAR(50) NOT NULL,
    Genero VARCHAR(10) NOT NULL,
    Telefono VARCHAR(10) NOT NULL UNIQUE,
    CorreoElectronico VARCHAR(50) NOT NULL UNIQUE,
    FechaNacimiento DATE NOT NULL,
    Edad INT,
    
    CONSTRAINT PK_PARTICIPANTE PRIMARY KEY(IdParticipante)
	CHECK (Genero IN ('Masculino','Femenino'))
);

CREATE TABLE Persona.Arbitro (
    IdArbitro BIGSERIAL NOT NULL,
    IdParticipante BIGINT NOT NULL UNIQUE,
    CedulaArbitro VARCHAR(15) NOT NULL UNIQUE,

    CONSTRAINT PK_ARBITRO PRIMARY KEY(IdArbitro),
    CONSTRAINT FK_ARBITRO_PARTICIPANTE FOREIGN KEY (IdParticipante)
        REFERENCES Persona.Participante(IdParticipante)
);

CREATE TABLE Persona.Jugador (
    IdJugador BIGSERIAL NOT NULL,
    IdParticipante BIGINT NOT NULL UNIQUE,
    Posicion VARCHAR(50) NOT NULL,
    Numero INT NOT NULL,
    TipoSangre VARCHAR(10) NOT NULL,
    AcumuladorAmarillas INT DEFAULT 0,
    Estado VARCHAR(15) DEFAULT 'Activo',

    CONSTRAINT PK_JUGADOR PRIMARY KEY(IdJugador),
    CONSTRAINT FK_JUGADOR_PARTICIPANTE FOREIGN KEY (IdParticipante)
    	REFERENCES Persona.Participante(IdParticipante)
	
	CONSTRAINT CHK_JUGADOR_POSICION
        CHECK (Posicion IN ('Portero','Defensa','Medio','Delantero')),

    CONSTRAINT CHK_JUGADOR_NUMERO
        CHECK (Numero > 0 AND Numero <= 99),

    CONSTRAINT CHK_JUGADOR_AMARILLAS
        CHECK (AcumuladorAmarillas >= 0),

    CONSTRAINT CHK_JUGADOR_ESTADO
        CHECK (Estado IN ('Activo','Suspendido','Baja'))	
);


/*====================================================
 TABLAS DEL ESQUEMA JUEGO
====================================================*/

CREATE TABLE Juego.Lugar (
    IdLugar BIGSERIAL NOT NULL,
    Nombre VARCHAR(50) NOT NULL UNIQUE,
    Ubicacion VARCHAR(50) NOT NULL UNIQUE,
    Capacidad INT NOT NULL,

    CONSTRAINT PK_LUGAR PRIMARY KEY(IdLugar)
	CHECK (Capacidad > 1000)
);

CREATE TABLE Juego.Torneo (
    IdTorneo BIGSERIAL NOT NULL,
    NombreTorneo VARCHAR(50) NOT NULL UNIQUE,
    EdadMin INT NOT NULL,
    EdadMax INT NOT NULL,
    Genero VARCHAR(50) NOT NULL,
    FechaInicio DATE NOT NULL,
    FechaFin DATE NOT NULL,
    CantEquipos INT,
    NumJornadas INT,

    CONSTRAINT PK_TORNEO PRIMARY KEY(IdTorneo)
	 
	CONSTRAINT CHK_TORNEO_EDADES
        CHECK (EdadMin >= 0 AND EdadMax >= EdadMin),

    CONSTRAINT CHK_TORNEO_FECHAS
        CHECK (FechaFin >= FechaInicio)
);

CREATE TABLE Juego.DetalleTorneo (
    IdTorneo BIGINT NOT NULL,
    IdEquipo BIGINT NOT NULL,

    CONSTRAINT PK_DETALLETORNEO PRIMARY KEY(IdTorneo, IdEquipo),
    CONSTRAINT FK_DETALLETORNEO_EQUIPO FOREIGN KEY (IdEquipo)
        REFERENCES Club.Equipo(IdEquipo),
    CONSTRAINT FK_DETALLETORNEO_TORNEO FOREIGN KEY (IdTorneo)
        REFERENCES Juego.Torneo(IdTorneo)
);

CREATE TABLE Juego.Jornada (
    IdJornada BIGSERIAL NOT NULL,
    IdTorneo BIGINT NOT NULL,
    NumeroJornada INT NOT NULL,

    CONSTRAINT PK_JORNADA PRIMARY KEY(IdJornada),
    CONSTRAINT FK_JORNADA_TORNEO FOREIGN KEY (IdTorneo)
        REFERENCES Juego.Torneo(IdTorneo)
);


/*====================================================
 TABLAS DEL ESQUEMA CLUB
====================================================*/

CREATE TABLE Club.Equipo (
    IdEquipo BIGSERIAL NOT NULL,
    NombreEquipo VARCHAR(50) NOT NULL UNIQUE,
    Logo VARCHAR(500) NOT NULL,
    CantJugadores INT,

    CONSTRAINT PK_EQUIPO PRIMARY KEY(IdEquipo)
);

CREATE TABLE Club.DetalleEquipo (
    IdEquipo BIGINT NOT NULL,
    IdJugador BIGINT NOT NULL,

    CONSTRAINT PK_DETALLEEQUIPO PRIMARY KEY(IdEquipo, IdJugador),
    CONSTRAINT FK_DETALLEEQUIPO_EQUIPO FOREIGN KEY (IdEquipo)
        REFERENCES Club.Equipo(IdEquipo),
    CONSTRAINT FK_DETALLEEQUIPO_JUGADOR FOREIGN KEY (IdJugador)
        REFERENCES Persona.Jugador(IdJugador)
);


/*====================================================
 TABLAS DEL ESQUEMA EVENTO
====================================================*/

CREATE TABLE Evento.Partido (
    IdPartido BIGSERIAL NOT NULL,
    IdArbitro BIGINT NOT NULL,
    IdJornada BIGINT NOT NULL,
    IdLugar BIGINT NOT NULL,
    IdLocal BIGINT NOT NULL,
    IdVisitante BIGINT NOT NULL,
    Fecha DATE NOT NULL,
    HoraInicio TIME NOT NULL,
    Estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',

    CONSTRAINT PK_PARTIDO PRIMARY KEY(IdPartido),
    CONSTRAINT FK_PARTIDO_ARBITRO FOREIGN KEY (IdArbitro)
        REFERENCES Persona.Arbitro(IdArbitro),
    CONSTRAINT FK_PARTIDO_JORNADA FOREIGN KEY (IdJornada)
        REFERENCES Juego.Jornada(IdJornada),
    CONSTRAINT FK_PARTIDO_LUGAR FOREIGN KEY (IdLugar)
        REFERENCES Juego.Lugar(IdLugar),
    CONSTRAINT FK_PARTIDO_EQLOCAL FOREIGN KEY (IdLocal)
        REFERENCES Club.Equipo(IdEquipo),
    CONSTRAINT FK_PARTIDO_EQVISIT FOREIGN KEY (IdVisitante)
        REFERENCES Club.Equipo(IdEquipo)

	CONSTRAINT CHK_PARTIDO_EQUIPOS
        CHECK (IdLocal <> IdVisitante),

    CONSTRAINT CHK_PARTIDO_ESTADO
        CHECK (Estado IN ('Pendiente','En Juego','Finalizado','Suspendido'))
);

CREATE TABLE Evento.ResultadoPartido (
    IdResultado BIGSERIAL NOT NULL,
    IdPartido BIGINT NOT NULL,
    GolesLocal SMALLINT NOT NULL,
    GolesVisitante SMALLINT NOT NULL,
    HoraFin TIME NOT NULL,

    CONSTRAINT PK_RESULTADO PRIMARY KEY(IdResultado),
    CONSTRAINT FK_RESULTADO_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido(IdPartido)

	CONSTRAINT CHK_RESULTADO_GOLES
        CHECK (GolesLocal >= 0 AND GolesVisitante >= 0)
);

CREATE TABLE Evento.Gol (
    IdGol BIGSERIAL NOT NULL,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    Minuto SMALLINT NOT NULL,

    CONSTRAINT PK_GOL PRIMARY KEY(IdGol),
    CONSTRAINT FK_GOL_JUGADOR FOREIGN KEY (IdJugador)
        REFERENCES Persona.Jugador(IdJugador),
    CONSTRAINT FK_GOL_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido(IdPartido)

	CONSTRAINT CHK_GOL_MINUTO
        CHECK (Minuto >= 0 AND Minuto <= 130)
);

CREATE TABLE Evento.Tarjeta (
    IdTarjeta BIGSERIAL NOT NULL,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    TipoTarjeta VARCHAR(10) NOT NULL,
    Minuto SMALLINT NOT NULL,

    CONSTRAINT PK_TARJETA PRIMARY KEY(IdTarjeta),
    CONSTRAINT FK_TARJETA_JUGADOR FOREIGN KEY (IdJugador)
        REFERENCES Persona.Jugador(IdJugador),
    CONSTRAINT FK_TARJETA_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido(IdPartido)

	CONSTRAINT CHK_TARJETA_TIPO
        CHECK (TipoTarjeta IN ('Amarilla','Roja')),

    CONSTRAINT CHK_TARJETA_MINUTO
        CHECK (Minuto >= 0 AND Minuto <= 130)
);

/*====================================================
 Disparadores
====================================================*/

/* Calcula Edad de Participante */
CREATE OR REPLACE FUNCTION Persona.fn_participante_calcular_edad()
RETURNS TRIGGER
AS $$
BEGIN
    NEW.Edad :=
        EXTRACT(YEAR FROM AGE(CURRENT_DATE, NEW.FechaNacimiento));

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_participante_calcular_edad
BEFORE INSERT OR UPDATE
ON Persona.Participante
FOR EACH ROW
EXECUTE FUNCTION Persona.fn_participante_calcular_edad();

/*actualizar CantEquipos y NumJornadas en la tabla Torneo al inscribir un equipo */
CREATE OR REPLACE FUNCTION Juego.fn_actualizar_torneo()
RETURNS TRIGGER
AS $$
DECLARE
    v_idtorneo BIGINT;
    v_total INT;
    v_jornadas INT;
    v_jornadas_actuales INT;
    i INT;
BEGIN
    /* Detectar IdTorneo según operación */
    IF TG_OP = 'DELETE' THEN
        v_idtorneo := OLD.IdTorneo;
    ELSE
        v_idtorneo := NEW.IdTorneo;
    END IF;

    /* Contar equipos actuales */
    SELECT COUNT(*)
    INTO v_total
    FROM Juego.DetalleTorneo
    WHERE IdTorneo = v_idtorneo;

    /* Calcular jornadas */
    IF MOD(v_total, 2) = 0 THEN
        v_jornadas := v_total - 1;
    ELSE
        v_jornadas := v_total;
    END IF;

    /* Actualizar tabla torneo */
    UPDATE Juego.Torneo
    SET CantEquipos = v_total,
        NumJornadas = v_jornadas
    WHERE IdTorneo = v_idtorneo;

    /* Contar jornadas existentes */
    SELECT COUNT(*)
    INTO v_jornadas_actuales
    FROM Juego.Jornada
    WHERE IdTorneo = v_idtorneo;

    /* Insertar jornadas faltantes */
    IF v_jornadas > v_jornadas_actuales THEN
        i := v_jornadas_actuales + 1;

        WHILE i <= v_jornadas LOOP
            INSERT INTO Juego.Jornada(IdTorneo, NumeroJornada)
            VALUES (v_idtorneo, i);

            i := i + 1;
        END LOOP;
    END IF;

    /* Eliminar jornadas sobrantes */
    IF v_jornadas < v_jornadas_actuales THEN
        DELETE FROM Juego.Jornada
        WHERE IdTorneo = v_idtorneo
          AND NumeroJornada > v_jornadas;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_actualizar_torneo
AFTER INSERT OR UPDATE OR DELETE
ON Juego.DetalleTorneo
FOR EACH ROW
EXECUTE FUNCTION Juego.fn_actualizar_torneo();

/*actualizar el numero de jugadores de algun equipo */
CREATE OR REPLACE FUNCTION Club.fn_detalleequipo_cantidad()
RETURNS TRIGGER
AS $$
DECLARE
    v_idequipo BIGINT;
BEGIN
    /* Detectar equipo según operación */
    IF TG_OP = 'DELETE' THEN
        v_idequipo := OLD.IdEquipo;
    ELSE
        v_idequipo := NEW.IdEquipo;
    END IF;

    /* Actualizar cantidad de jugadores */
    UPDATE Club.Equipo
    SET CantJugadores = (
        SELECT COUNT(*)
        FROM Club.DetalleEquipo
        WHERE IdEquipo = v_idequipo
    )
    WHERE IdEquipo = v_idequipo;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_detalleequipo_cantidad
AFTER INSERT OR DELETE
ON Club.DetalleEquipo
FOR EACH ROW
EXECUTE FUNCTION Club.fn_detalleequipo_cantidad();