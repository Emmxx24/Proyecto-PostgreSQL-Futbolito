/* 1. Creación de la Base de Datos */
CREATE DATABASE "Futbolito"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

/* 2. Creación de Esquemas */
CREATE SCHEMA Persona;
CREATE SCHEMA Juego;
CREATE SCHEMA Evento;
CREATE SCHEMA Club;

/* 3. Creación de tablas*/
CREATE TABLE Persona.Participante (
    IdParticipante BIGSERIAL NOT NULL,
    NombreParticipante VARCHAR(50) NOT NULL,
    Genero VARCHAR(10) NOT NULL,
    Telefono VARCHAR(10) NOT NULL,
    CorreoElectronico VARCHAR(50) NOT NULL,
    FechaNacimiento DATE NOT NULL,
    Edad INT,

    CONSTRAINT PK_PARTICIPANTE PRIMARY KEY (IdParticipante),
    CONSTRAINT UQ_PARTICIPANTE_TELEFONO UNIQUE(Telefono),
    CONSTRAINT UQ_PARTICIPANTE_CORREO UNIQUE(CorreoElectronico)
);

CREATE TABLE Juego.Lugar (
    IdLugar BIGSERIAL NOT NULL,
    Nombre VARCHAR(50) NOT NULL,
    Ubicacion VARCHAR(50) NOT NULL,
    Capacidad INT NOT NULL,
    
    CONSTRAINT PK_LUGAR PRIMARY KEY (IdLugar),
    CONSTRAINT UQ_LUGAR_UBICACION UNIQUE (Ubicacion),
    CONSTRAINT UQ_LUGAR_NOMBRE UNIQUE (Nombre),
    -- Regla de Capacidad (ex RL_CAPACIDAD)
    CONSTRAINT CHK_LUGAR_CAPACIDAD CHECK (Capacidad >= 1000)
);

CREATE TABLE Juego.Torneo (
    IdTorneo BIGSERIAL NOT NULL,
    NombreTorneo VARCHAR(50) NOT NULL,
    EdadMin INT NOT NULL,
    EdadMax INT NOT NULL,
    Genero VARCHAR(50) NOT NULL, 
    FechaInicio DATE NOT NULL,
    FechaFin DATE NOT NULL,
    CantEquipos INT,
    NumJornadas INT,

    CONSTRAINT PK_TORNEO PRIMARY KEY (IdTorneo),
    CONSTRAINT UQ_TORNEO_NOMBRE UNIQUE (NombreTorneo)
);

CREATE TABLE Club.Equipo (
    IdEquipo BIGSERIAL NOT NULL,
    NombreEquipo VARCHAR(50) NOT NULL,
    Logo VARCHAR(500) NOT NULL, 
    CantJugadores INT, 

    CONSTRAINT PK_EQUIPO PRIMARY KEY (IdEquipo),
    CONSTRAINT UQ_EQUIPO_NOMBRE UNIQUE (NombreEquipo)
);

CREATE TABLE Persona.Arbitro (
    IdArbitro BIGSERIAL NOT NULL,
    IdParticipante BIGINT NOT NULL,
    CedulaArbitro VARCHAR(15) NOT NULL,

    CONSTRAINT PK_ARBITRO PRIMARY KEY (IdArbitro),
    CONSTRAINT FK_ARBITRO_PARTICIPANTE FOREIGN KEY (IdParticipante) 
        REFERENCES Persona.Participante (IdParticipante),
    CONSTRAINT UQ_ARBITRO_CEDULA UNIQUE (CedulaArbitro),
    CONSTRAINT UQ_ARBITRO_PARTICIPANTE UNIQUE (IdParticipante)
);

CREATE TABLE Persona.Jugador (
    IdJugador BIGSERIAL NOT NULL,
    IdParticipante BIGINT NOT NULL,
    Posicion VARCHAR(50) NOT NULL, 
    Numero INT NOT NULL,
    TipoSangre VARCHAR(10) NOT NULL, 
    AcumuladorAmarillas INT DEFAULT 0,
    Estado VARCHAR(15) DEFAULT 'Activo',

    CONSTRAINT PK_JUGADOR PRIMARY KEY (IdJugador),
    CONSTRAINT FK_JUGADOR_PARTICIPANTE FOREIGN KEY (IdParticipante) 
        REFERENCES Persona.Participante (IdParticipante),
    CONSTRAINT UQ_JUGADOR_PARTICIPANTE UNIQUE (IdParticipante),
    -- Regla de Posición (ex RL_POSICION)
    CONSTRAINT CHK_JUGADOR_POSICION CHECK (Posicion IN ('Portero', 'Defensa', 'Medio', 'Delantero'))
);

CREATE TABLE Juego.Jornada (
    IdJornada BIGSERIAL NOT NULL,
    IdTorneo BIGINT NOT NULL,
    NumeroJornada INT NOT NULL,

    CONSTRAINT PK_JORNADA PRIMARY KEY (IdJornada),
    CONSTRAINT FK_JORNADA_TORNEO FOREIGN KEY (IdTorneo)
        REFERENCES Juego.Torneo (IdTorneo)
);

CREATE TABLE Club.DetalleEquipo (
    IdEquipo BIGINT NOT NULL,
    IdJugador BIGINT NOT NULL,

    CONSTRAINT FK_DETALLEEQUIPO_EQUIPO FOREIGN KEY (IdEquipo) 
        REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_DETALLEEQUIPO_JUGADOR FOREIGN KEY (IdJugador) 
        REFERENCES Persona.Jugador (IdJugador),
	CONSTRAINT UQ_EQUIPO_JUGADOR UNIQUE (IdEquipo, IdJugador)
);

CREATE TABLE Juego.DetalleTorneo (
    IdTorneo BIGINT NOT NULL,
    IdEquipo BIGINT NOT NULL,

    CONSTRAINT FK_DETALLETORNEO_EQUIPO FOREIGN KEY (IdEquipo)
        REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_DETALLETORNEO_TORNEO FOREIGN KEY (IdTorneo)
        REFERENCES Juego.Torneo (IdTorneo),
	CONSTRAINT UQ_TORNEO_EQUIPO UNIQUE (IdTorneo, IdEquipo)
);

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

    CONSTRAINT PK_PARTIDO PRIMARY KEY (IdPartido),
    CONSTRAINT FK_PARTIDO_ARBITRO FOREIGN KEY (IdArbitro)
        REFERENCES Persona.Arbitro (IdArbitro),
    CONSTRAINT FK_PARTIDO_JORNADA FOREIGN KEY (IdJornada)
        REFERENCES Juego.Jornada (IdJornada),
    CONSTRAINT FK_PARTIDO_LUGAR FOREIGN KEY (IdLugar)
        REFERENCES Juego.Lugar (IdLugar),
    CONSTRAINT FK_PARTIDO_EQLOCAL FOREIGN KEY (IdLocal)
        REFERENCES Club.Equipo (IdEquipo),
    CONSTRAINT FK_PARTIDO_EQVISIT FOREIGN KEY (IdVisitante)
        REFERENCES Club.Equipo (IdEquipo)
);

CREATE TABLE Evento.ResultadoPartido (
    IdResultado BIGSERIAL NOT NULL,
    IdPartido BIGINT NOT NULL,
    GolesLocal SMALLINT NOT NULL,
    GolesVisitante SMALLINT NOT NULL,
    HoraFin TIME NOT NULL,

    CONSTRAINT PK_RESULTADO PRIMARY KEY (IdResultado),
    CONSTRAINT FK_RESULTADO_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido (IdPartido),
    CONSTRAINT UQ_RESULTADO_PARTIDO UNIQUE (IdPartido)
);

CREATE TABLE Evento.Gol (
    IdGol BIGSERIAL NOT NULL,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    Minuto SMALLINT NOT NULL,

    CONSTRAINT PK_GOL PRIMARY KEY (IdGol),
    CONSTRAINT FK_GOL_JUGADOR FOREIGN KEY (IdJugador)
        REFERENCES Persona.Jugador (IdJugador),
    CONSTRAINT FK_GOL_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido (IdPartido)
);

CREATE TABLE Evento.Tarjeta (
    IdTarjeta BIGSERIAL NOT NULL,
    IdJugador BIGINT NOT NULL,
    IdPartido BIGINT NOT NULL,
    TipoTarjeta VARCHAR(10) NOT NULL,
    Minuto SMALLINT NOT NULL,

    CONSTRAINT PK_TARJETA PRIMARY KEY (IdTarjeta),
    CONSTRAINT FK_TARJETA_JUGADOR FOREIGN KEY (IdJugador)
        REFERENCES Persona.Jugador (IdJugador),
    CONSTRAINT FK_TARJETA_PARTIDO FOREIGN KEY (IdPartido)
        REFERENCES Evento.Partido (IdPartido)
);


/*Disparadores*/

-- 1. Calcular Edad del Participante
CREATE OR REPLACE FUNCTION Persona.fn_tr_participante_calcular_edad()
RETURNS TRIGGER AS $$
BEGIN
    NEW.Edad := EXTRACT(YEAR FROM AGE(CURRENT_DATE, NEW.FechaNacimiento));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_PARTICIPANTE_CALCULAR_EDAD ON Persona.Participante;
CREATE TRIGGER TR_PARTICIPANTE_CALCULAR_EDAD
BEFORE INSERT OR UPDATE ON Persona.Participante
FOR EACH ROW EXECUTE FUNCTION Persona.fn_tr_participante_calcular_edad();


-- 2. Actualizar Torneo (CantEquipos y Jornadas)
CREATE OR REPLACE FUNCTION Juego.fn_tr_actualizar_torneo()
RETURNS TRIGGER AS $$
DECLARE
    v_idTorneo BIGINT;
    v_total INT;
    v_jornadas INT;
    v_jornadasActuales INT;
    v_i INT;
BEGIN
    -- Determinar el ID del torneo afectado
    IF (TG_OP = 'DELETE') THEN
        v_idTorneo := OLD.IdTorneo;
    ELSE
        v_idTorneo := NEW.IdTorneo;
    END IF;

    -- Contar equipos actuales
    SELECT COUNT(*) INTO v_total FROM Juego.DetalleTorneo WHERE IdTorneo = v_idTorneo;

    -- Calcular jornadas (AQUÍ ESTÁ EL PARCHE DEL BUG)
    IF v_total = 0 THEN 
        v_jornadas := 0;
    ELSIF v_total % 2 = 0 THEN 
        v_jornadas := v_total - 1;
    ELSE 
        v_jornadas := v_total;
    END IF;

    -- Actualizar Torneo
    UPDATE Juego.Torneo SET CantEquipos = v_total, NumJornadas = v_jornadas WHERE IdTorneo = v_idTorneo;

    -- Manejo de filas en Juego.Jornada
    SELECT COUNT(*) INTO v_jornadasActuales FROM Juego.Jornada WHERE IdTorneo = v_idTorneo;

    IF v_jornadas > v_jornadasActuales THEN
        v_i := v_jornadasActuales + 1;
        WHILE v_i <= v_jornadas LOOP
            INSERT INTO Juego.Jornada(IdTorneo, NumeroJornada) VALUES (v_idTorneo, v_i);
            v_i := v_i + 1;
        END LOOP;
    ELSIF v_jornadas < v_jornadasActuales THEN
        DELETE FROM Juego.Jornada WHERE IdTorneo = v_idTorneo AND NumeroJornada > v_jornadas;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_ActualizarTorneo ON Juego.DetalleTorneo;
CREATE TRIGGER TR_ActualizarTorneo
AFTER INSERT OR UPDATE OR DELETE ON Juego.DetalleTorneo
FOR EACH ROW EXECUTE FUNCTION Juego.fn_tr_actualizar_torneo();


-- 3. Actualizar número de jugadores en un equipo
CREATE OR REPLACE FUNCTION Club.fn_tr_detalleequipo_cantidad()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        UPDATE Club.Equipo 
        SET CantJugadores = (SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo = NEW.IdEquipo)
        WHERE IdEquipo = NEW.IdEquipo;
        
    ELSIF (TG_OP = 'UPDATE') THEN
        -- Si modificas al jugador y lo cambias de equipo, actualizamos ambos equipos
        IF OLD.IdEquipo IS DISTINCT FROM NEW.IdEquipo THEN
            -- Recalcular el equipo del que salió
            UPDATE Club.Equipo 
            SET CantJugadores = (SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo = OLD.IdEquipo)
            WHERE IdEquipo = OLD.IdEquipo;
            
            -- Recalcular el equipo al que entró
            UPDATE Club.Equipo 
            SET CantJugadores = (SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo = NEW.IdEquipo)
            WHERE IdEquipo = NEW.IdEquipo;
        END IF;

    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE Club.Equipo 
        SET CantJugadores = (SELECT COUNT(*) FROM Club.DetalleEquipo WHERE IdEquipo = OLD.IdEquipo)
        WHERE IdEquipo = OLD.IdEquipo;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_DETALLEEQUIPO_CANTIDAD ON Club.DetalleEquipo;
CREATE TRIGGER TR_DETALLEEQUIPO_CANTIDAD
AFTER INSERT OR UPDATE OR DELETE ON Club.DetalleEquipo
FOR EACH ROW EXECUTE FUNCTION Club.fn_tr_detalleequipo_cantidad();


-- 4. Actualizamos el estado de partido y jugadores despues de insertar un resultado partido
CREATE OR REPLACE FUNCTION Evento.fn_tr_resultado_actualizar_estados()
RETURNS TRIGGER AS $$
BEGIN
    -- Si estamos insertando un resultado
    IF (TG_OP = 'INSERT') THEN
        -- A) El partido pasa a 'Jugado'
        UPDATE Evento.Partido 
        SET Estado = 'Jugado' 
        WHERE IdPartido = NEW.IdPartido;

        -- B) Los jugadores suspendidos de esos equipos pasan a 'Activo'
        UPDATE Persona.Jugador
        SET Estado = 'Activo'
        FROM Club.DetalleEquipo de
        INNER JOIN Evento.Partido p ON (de.IdEquipo = p.IdLocal OR de.IdEquipo = p.IdVisitante)
        WHERE Persona.Jugador.IdJugador = de.IdJugador
          AND p.IdPartido = NEW.IdPartido
          AND Persona.Jugador.Estado = 'Suspendido';
        
        RETURN NEW;
        
    -- Si estamos eliminando un resultado, el partido regresa a 'Pendiente' 
    ELSIF (TG_OP = 'DELETE') THEN
        UPDATE Evento.Partido 
        SET Estado = 'Pendiente' 
        WHERE IdPartido = OLD.IdPartido;
        
        RETURN OLD;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_RESULTADO_ACTUALIZAR_ESTADOS ON Evento.ResultadoPartido;
CREATE TRIGGER TR_RESULTADO_ACTUALIZAR_ESTADOS
AFTER INSERT OR DELETE ON Evento.ResultadoPartido
FOR EACH ROW 
EXECUTE FUNCTION Evento.fn_tr_resultado_actualizar_estados

-- 5. Evitamos que se eliminen resultados si ya hay goles o tarjetas registradas
CREATE OR REPLACE FUNCTION Evento.fn_tr_prevenir_borrar_resultado()
RETURNS TRIGGER AS $$
DECLARE
    v_goles INT;
    v_tarjetas INT;
BEGIN
    SELECT COUNT(*) INTO v_goles FROM Evento.Gol WHERE IdPartido = OLD.IdPartido;
    SELECT COUNT(*) INTO v_tarjetas FROM Evento.Tarjeta WHERE IdPartido = OLD.IdPartido;

    IF (v_goles > 0 OR v_tarjetas > 0) THEN
        RAISE EXCEPTION 'No puedes eliminar este resultado. Existen % goles y % tarjetas registrados.', v_goles, v_tarjetas;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_PREVENIR_BORRAR_RESULTADO ON Evento.ResultadoPartido;
CREATE TRIGGER TR_PREVENIR_BORRAR_RESULTADO
BEFORE DELETE ON Evento.ResultadoPartido
FOR EACH ROW EXECUTE FUNCTION Evento.fn_tr_prevenir_borrar_resultado();

-- 6. Actualizamos el marcador de un resultado de partido en cada insercion/eliminacion
CREATE OR REPLACE FUNCTION Evento.fn_tr_gol_actualizar_marcador()
RETURNS TRIGGER AS $$
DECLARE
    v_idPartido BIGINT;
    v_golesLocal INT;
    v_golesVisitante INT;
    v_idLocal BIGINT;
    v_idVisitante BIGINT;
BEGIN
    -- Saber de qué partido estamos hablando
    IF (TG_OP = 'DELETE') THEN
        v_idPartido := OLD.IdPartido;
    ELSE
        v_idPartido := NEW.IdPartido;
    END IF;

    -- Sacar quién es el Local y el Visitante
    SELECT IdLocal, IdVisitante INTO v_idLocal, v_idVisitante
    FROM Evento.Partido WHERE IdPartido = v_idPartido;

    -- Contar goles del Local (cruzando Gol con DetalleEquipo para saber el equipo del anotador)
    SELECT COUNT(*) INTO v_golesLocal
    FROM Evento.Gol g
    INNER JOIN Club.DetalleEquipo de ON g.IdJugador = de.IdJugador
    WHERE g.IdPartido = v_idPartido AND de.IdEquipo = v_idLocal;

    -- Contar goles del Visitante
    SELECT COUNT(*) INTO v_golesVisitante
    FROM Evento.Gol g
    INNER JOIN Club.DetalleEquipo de ON g.IdJugador = de.IdJugador
    WHERE g.IdPartido = v_idPartido AND de.IdEquipo = v_idVisitante;

    -- Actualizar el marcador oficial
    UPDATE Evento.ResultadoPartido
    SET GolesLocal = v_golesLocal, GolesVisitante = v_golesVisitante
    WHERE IdPartido = v_idPartido;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_GOL_ACTUALIZAR_MARCADOR ON Evento.Gol;
CREATE TRIGGER TR_GOL_ACTUALIZAR_MARCADOR
AFTER INSERT OR UPDATE OR DELETE ON Evento.Gol
FOR EACH ROW EXECUTE FUNCTION Evento.fn_tr_gol_actualizar_marcador();

-- 7. Disparador para actualizar el estado de los jugadores 
-- despues de las inserciones de tarjetas, asi como para reiniciar 
-- su acumulador de tarjetas amarillas
CREATE OR REPLACE FUNCTION Evento.fn_tr_tarjeta_estado_jugador()
RETURNS TRIGGER AS $$
BEGIN
    -- 1. CASO INSERT
    IF (TG_OP = 'INSERT') THEN
        
        -- A) Si es amarilla, sumamos al acumulador
        IF (NEW.TipoTarjeta = 'Amarilla') THEN
            UPDATE Persona.Jugador 
            SET AcumuladorAmarillas = AcumuladorAmarillas + 1 
            WHERE IdJugador = NEW.IdJugador;
        END IF;

        -- B) Verificamos si se debe suspender (Por roja o por juntar 2 amarillas)
        UPDATE Persona.Jugador
        SET Estado = 'Suspendido',
            AcumuladorAmarillas = 0 
        WHERE IdJugador = NEW.IdJugador
          AND (NEW.TipoTarjeta = 'Roja' OR AcumuladorAmarillas >= 2);

        RETURN NEW;

	-- 2. CASO DELETE
    ELSIF (TG_OP = 'DELETE') THEN
        -- A) Si le borramos una amarilla y estaba activo, le restamos 1 al acumulador
        IF (OLD.TipoTarjeta = 'Amarilla') THEN
            UPDATE Persona.Jugador 
            SET AcumuladorAmarillas = GREATEST(AcumuladorAmarillas - 1, 0)
            WHERE IdJugador = OLD.IdJugador AND Estado = 'Activo';
        END IF;

        -- B) Si estaba suspendido, lo regresamos a Activo (Cuidando que no tenga otra roja en ese mismo partido)
        UPDATE Persona.Jugador j
        SET Estado = 'Activo',
            AcumuladorAmarillas = CASE WHEN OLD.TipoTarjeta = 'Amarilla' THEN 1 ELSE 0 END
        WHERE j.IdJugador = OLD.IdJugador 
          AND j.Estado = 'Suspendido'
          AND NOT EXISTS (
              SELECT 1 FROM Evento.Tarjeta t 
              WHERE t.IdJugador = j.IdJugador 
                AND t.IdPartido = OLD.IdPartido 
                AND t.TipoTarjeta = 'Roja' 
                AND t.IdTarjeta != OLD.IdTarjeta
          );

        RETURN OLD;

    -- 3. CASO UPDATE
    ELSIF (TG_OP = 'UPDATE') THEN
        -- Solo ejecutamos si cambiaron el Tipo de Tarjeta o el Jugador
        IF (OLD.TipoTarjeta IS DISTINCT FROM NEW.TipoTarjeta OR OLD.IdJugador IS DISTINCT FROM NEW.IdJugador) THEN
            -- A) Revertimos el efecto de la tarjeta vieja (Como en el DELETE)
            UPDATE Persona.Jugador 
            SET Estado = 'Activo',
                AcumuladorAmarillas = CASE 
                    WHEN OLD.TipoTarjeta = 'Amarilla' AND Estado = 'Activo' AND AcumuladorAmarillas > 0 
                    THEN AcumuladorAmarillas - 1 
                    ELSE AcumuladorAmarillas 
                END
            WHERE IdJugador = OLD.IdJugador;

            -- B) Aplicamos el efecto de la tarjeta nueva (Como en el INSERT)
            IF (NEW.TipoTarjeta = 'Amarilla') THEN
                UPDATE Persona.Jugador 
                SET AcumuladorAmarillas = AcumuladorAmarillas + 1 
                WHERE IdJugador = NEW.IdJugador;
            END IF;

            UPDATE Persona.Jugador
            SET Estado = 'Suspendido',
                AcumuladorAmarillas = 0
            WHERE IdJugador = NEW.IdJugador
              AND (NEW.TipoTarjeta = 'Roja' OR AcumuladorAmarillas >= 2);
              
        END IF;

        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS TR_TARJETA_ESTADO_JUGADOR ON Evento.Tarjeta;
CREATE TRIGGER TR_TARJETA_ESTADO_JUGADOR
AFTER INSERT OR UPDATE OR DELETE ON Evento.Tarjeta
FOR EACH ROW
EXECUTE FUNCTION Evento.fn_tr_tarjeta_estado_jugador();

-- 1. CREAR LOS USUARIOS (Con sus contraseñas)
CREATE USER admin_bd WITH PASSWORD 'admin123' SUPERUSER; -- El que le mueve a todo
CREATE USER arbitro_user WITH PASSWORD 'arbitro123';
CREATE USER capturista_user WITH PASSWORD 'capturista123';

-- ==============================================================
-- 2. PERMISOS DEL ÁRBITRO
-- ==============================================================
-- Dar acceso a los esquemas
GRANT USAGE ON SCHEMA Evento, Persona, Juego, Club TO arbitro_user;

-- Permisos completos (CRUD) para Resultados, Tarjetas y Goles
GRANT ALL PRIVILEGES ON Evento.ResultadoPartido, Evento.Tarjeta, Evento.Gol TO arbitro_user;
-- IMPORTANTE: Si usas campos SERIAL o IDENTITY, dales permiso a las secuencias
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA Evento TO arbitro_user;

-- Permisos de SOLO LECTURA (SELECT)
GRANT SELECT ON Persona.Participante, Persona.Jugador, Persona.Arbitro, Juego.Torneo, 
Juego.Jornada, Juego.Lugar, Club.Equipo, Club.DetalleEquipo, 
Juego.DetalleTorneo, Evento.Partido TO arbitro_user;

-- ==============================================================
-- 3. PERMISOS DEL CAPTURISTA
-- ==============================================================
-- Dar acceso a los esquemas
GRANT USAGE ON SCHEMA Persona, Juego, Club TO capturista_user;

-- Permisos completos (CRUD) para Participante, Jugador, DetalleTorneo y DetalleEquipo
GRANT ALL PRIVILEGES ON Juego.DetalleTorneo, Club.DetalleEquipo TO capturista_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA Persona TO capturista_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA Juego TO capturista_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA Club TO capturista_user;

-- Permisos de SOLO LECTURA (SELECT)
GRANT SELECT ON Persona.Participante, Persona.Jugador, Juego.Torneo, 
Juego.Jornada, Club.Equipo TO capturista_user;

-- Hacemos que los triggers se ejecuten con permisos de administrador
ALTER FUNCTION Persona.fn_tr_participante_calcular_edad() SECURITY DEFINER;
ALTER FUNCTION Juego.fn_tr_actualizar_torneo() SECURITY DEFINER;
ALTER FUNCTION Club.fn_tr_detalleequipo_cantidad() SECURITY DEFINER;
ALTER FUNCTION Evento.fn_tr_resultado_actualizar_estados() SECURITY DEFINER;
ALTER FUNCTION Evento.fn_tr_prevenir_borrar_resultado() SECURITY DEFINER;
ALTER FUNCTION Evento.fn_tr_gol_actualizar_marcador() SECURITY DEFINER;
ALTER FUNCTION Evento.fn_tr_tarjeta_estado_jugador() SECURITY DEFINER;

-- Borrar los datos que hayan registrados para volver a ingresar datos sin borrar la bd
TRUNCATE TABLE 
    Persona.Participante,
    Persona.Arbitro,
    Persona.Jugador,
    Juego.Lugar,
    Juego.Torneo,
    Juego.Jornada,
    Juego.DetalleTorneo,
    Club.Equipo,
    Club.DetalleEquipo,
    Evento.Partido,
    Evento.ResultadoPartido,
    Evento.Gol,
    Evento.Tarjeta
RESTART IDENTITY CASCADE;

-- DATOS DE PRUEBA
-- =========================================================
-- 1. LUGARES (Requerido para poder crear partidos)
-- =========================================================
INSERT INTO Juego.Lugar (Nombre, Ubicacion, Capacidad) VALUES 
('Estadio Universitario', 'Zona Universitaria', 1500),
('Cancha Principal FI', 'Facultad de Ingeniería', 1200);

-- =========================================================
-- 2. PARTICIPANTES (2 Árbitros + 16 Jugadores - 1 Mujer por equipo)
-- =========================================================
-- Árbitros (IDs 1 y 2)
INSERT INTO Persona.Participante (NombreParticipante, Genero, Telefono, CorreoElectronico, FechaNacimiento) VALUES 
('Arturo Brizio', 'Masculino', '4441112233', 'arturo.b@arbitros.com', '1985-05-10'),
('Marco Rodríguez', 'Masculino', '4442223344', 'marco.r@arbitros.com', '1988-08-15'),

-- Jugadores Equipo 1 (IDs 3 al 6) -> 1 Mujer (ID 6)
('Carlos Martínez', 'Masculino', '4443334455', 'carlos.m@mail.com', '2001-02-20'),
('David López', 'Masculino', '4444445566', 'david.l@mail.com', '2002-03-25'),
('Eduardo Gómez', 'Masculino', '4445556677', 'eduardo.g@mail.com', '2000-11-05'),
('Fernanda Ruiz', 'Femenino', '4446667788', 'fernanda.r@mail.com', '2003-01-15'),

-- Jugadores Equipo 2 (IDs 7 al 10) -> 1 Mujer (ID 10)
('Gerardo Silva', 'Masculino', '4447778899', 'gerardo.s@mail.com', '2001-07-10'),
('Hugo Castro', 'Masculino', '4448889900', 'hugo.c@mail.com', '2000-09-30'),
('Ignacio Vega', 'Masculino', '4449990011', 'ignacio.v@mail.com', '2002-12-12'),
('Jessica Ortiz', 'Femenino', '4440001122', 'jessica.o@mail.com', '2001-04-18'),

-- Jugadores Equipo 3 (IDs 11 al 14) -> 1 Mujer (ID 14)
('Kevin Nava', 'Masculino', '4441234567', 'kevin.n@mail.com', '2003-05-22'),
('Luis Peña', 'Masculino', '4442345678', 'luis.p@mail.com', '2000-08-08'),
('Mario Luna', 'Masculino', '4443456789', 'mario.l@mail.com', '2001-10-14'),
('Natalia Ríos', 'Femenino', '4444567890', 'natalia.r@mail.com', '2002-06-28'),

-- Jugadores Equipo 4 (IDs 15 al 18) -> 1 Mujer (ID 18)
('Omar Cruz', 'Masculino', '4445678901', 'omar.c@mail.com', '2001-01-09'),
('Pablo Díaz', 'Masculino', '4446789012', 'pablo.d@mail.com', '2000-04-03'),
('Quintín Mora', 'Masculino', '4447890123', 'quintin.m@mail.com', '2003-11-19'),
('Raquel Salas', 'Femenino', '4448901234', 'raquel.s@mail.com', '2002-09-07');

-- =========================================================
-- 3. ASIGNACIÓN DE ROLES (Árbitros y Jugadores)
-- =========================================================
INSERT INTO Persona.Arbitro (IdParticipante, CedulaArbitro) VALUES 
(1, 'ARB-001-MX'),
(2, 'ARB-002-MX');

-- Cuidando la restricción CHK_JUGADOR_POSICION
INSERT INTO Persona.Jugador (IdParticipante, Posicion, Numero, TipoSangre) VALUES 
-- Eq 1
(3, 'Portero', 1, 'O+'), (4, 'Defensa', 4, 'A+'), (5, 'Medio', 8, 'B+'), (6, 'Delantero', 9, 'O-'),
-- Eq 2
(7, 'Portero', 1, 'A-'), (8, 'Defensa', 3, 'O+'), (9, 'Medio', 10, 'O+'), (10, 'Delantero', 11, 'AB+'),
-- Eq 3
(11, 'Portero', 1, 'B-'), (12, 'Defensa', 2, 'O+'), (13, 'Medio', 6, 'A+'), (14, 'Delantero', 7, 'O+'),
-- Eq 4
(15, 'Portero', 12, 'O+'), (16, 'Defensa', 5, 'AB-'), (17, 'Medio', 14, 'O+'), (18, 'Delantero', 19, 'A+');

-- =========================================================
-- 4. TORNEO Y EQUIPOS
-- =========================================================
INSERT INTO Juego.Torneo (NombreTorneo, EdadMin, EdadMax, Genero, FechaInicio, FechaFin) VALUES 
('Torneo Futbolito Universitario', 18, 50, 'Masculino', '2026-06-01', '2026-07-30');

INSERT INTO Club.Equipo (NombreEquipo, Logo) VALUES 
('Ingeniería FC', 'a'),
('Sistemas United', 'a'),
('Deportivo Centro', 'a'),
('Los del Fondo', 'a');

-- =========================================================
-- 5. ASIGNAR JUGADORES A EQUIPOS (Dispara TR_DETALLEEQUIPO_CANTIDAD)
-- =========================================================
INSERT INTO Club.DetalleEquipo (IdEquipo, IdJugador) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4),        -- Ingeniería FC
(2, 5), (2, 6), (2, 7), (2, 8),        -- Sistemas United
(3, 9), (3, 10), (3, 11), (3, 12),     -- Deportivo Centro
(4, 13), (4, 14), (4, 15), (4, 16);    -- Los del Fondo

-- =========================================================
-- 6. INSCRIBIR EQUIPOS AL TORNEO (Dispara TR_ActualizarTorneo)
-- =========================================================
INSERT INTO Juego.DetalleTorneo (IdTorneo, IdEquipo) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4);

-- =========================================================
-- 7. PARTIDOS (Round Robin - Todos contra todos)
-- =========================================================
-- Jornada 1 (IdJornada = 1)
INSERT INTO Evento.Partido (IdArbitro, IdJornada, IdLugar, IdLocal, IdVisitante, Fecha, HoraInicio) VALUES 
(1, 1, 1, 1, 4, '2026-06-05', '16:00:00'), -- Ingeniería FC vs Los del Fondo
(2, 1, 2, 2, 3, '2026-06-05', '18:00:00'); -- Sistemas United vs Deportivo Centro

-- Jornada 2 (IdJornada = 2)
INSERT INTO Evento.Partido (IdArbitro, IdJornada, IdLugar, IdLocal, IdVisitante, Fecha, HoraInicio) VALUES 
(2, 2, 1, 1, 3, '2026-06-12', '16:00:00'), -- Ingeniería FC vs Deportivo Centro
(1, 2, 2, 4, 2, '2026-06-12', '18:00:00'); -- Los del Fondo vs Sistemas United

-- Jornada 3 (IdJornada = 3)
INSERT INTO Evento.Partido (IdArbitro, IdJornada, IdLugar, IdLocal, IdVisitante, Fecha, HoraInicio) VALUES 
(1, 3, 2, 1, 2, '2026-06-19', '16:00:00'), -- Ingeniería FC vs Sistemas United
(2, 3, 1, 3, 4, '2026-06-19', '18:00:00'); -- Deportivo Centro vs Los del Fondo



-- Consulta de Reporte 1
/*SELECT j.IdJugador, par.NombreParticipante AS "Nombre de jugador", e.NombreEquipo AS "Nombre del equipo", 
COUNT(g.IdGol) AS "Cantidad de goles"
FROM Persona.Jugador j
INNER JOIN Persona.Participante par
ON par.IdParticipante = j.IdParticipante
INNER JOIN Evento.Gol g
ON g.IdJugador = j.IdJugador
INNER JOIN Evento.Partido p
ON p.IdPartido = g.IdPartido
INNER JOIN Club.Equipo e
ON e.IdEquipo = p.IdLocal OR e.IdEquipo = p.IdVisitante
INNER JOIN Club.DetalleEquipo de
ON de.IdJugador = j.IdJugador AND de.IdEquipo = e.IdEquipo
WHERE e.IdEquipo = 9 --ese id cambia
GROUP BY j.IdJugador, par.NombreParticipante, e.NombreEquipo;*/