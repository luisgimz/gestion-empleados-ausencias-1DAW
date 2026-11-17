drop database if exists BD_grupo_beta;
create database BD_grupo_beta;
use BD_grupo_beta;

-- BORRAR TABLAS
DROP TABLE IF EXISTS registro_ausencia;
DROP TABLE IF EXISTS solicitud_ausencia;
DROP TABLE IF EXISTS tipo_ausencia;

DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS empleado;
DROP TABLE IF EXISTS puesto_trabajo;
DROP TABLE IF EXISTS categoria_laboral;
DROP TABLE IF EXISTS departamento;
DROP TABLE IF EXISTS sede;
DROP TABLE IF EXISTS empresa;

-- CREAR TABLAS PRINCIPALES

CREATE TABLE empresa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(255) NOT NULL,
    nombre_comercial VARCHAR(255),
    forma_juridica VARCHAR(100),
    cif VARCHAR(20) UNIQUE NOT NULL,
    direccion_fiscal VARCHAR(255),
    telefono VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE sede (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    tipo VARCHAR(50),
    calle VARCHAR(255),
    numero VARCHAR(20),
    cp VARCHAR(10),
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    email_contacto VARCHAR(100),
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE departamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    codigo VARCHAR(20),
    nombre VARCHAR(100),
    descripcion TEXT,
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE categoria_laboral (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    convenio VARCHAR(100),
    grupo_profesional VARCHAR(100),
    nivel VARCHAR(50),
    descripcion TEXT,
    FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE puesto_trabajo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sede_id BIGINT NOT NULL,
    departamento_id BIGINT NOT NULL,
    categoria_referencia BIGINT NOT NULL,
    nombre VARCHAR(100),
    descripcion_funciones TEXT,
    jornada VARCHAR(50),
    modalidad VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (sede_id) REFERENCES sede(id),
    FOREIGN KEY (departamento_id) REFERENCES departamento(id),
    FOREIGN KEY (categoria_referencia) REFERENCES categoria_laboral(id)
);

CREATE TABLE empleado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    puesto_actual BIGINT,
    categoria_real BIGINT,
    dni VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150),
    email_corporativo VARCHAR(100) UNIQUE,
    email_personal VARCHAR(100),
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    numero_ss VARCHAR(20),
    fecha_nacimiento DATE,
    fecha_alta DATE,
    fecha_baja DATE,
    estado_laboral VARCHAR(50),
    dias_vacaciones_disponibles INT NOT NULL DEFAULT 22,
    foto LONGBLOB NULL,
    FOREIGN KEY (puesto_actual) REFERENCES puesto_trabajo(id),
    FOREIGN KEY (categoria_real) REFERENCES categoria_laboral(id)
);

CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id BIGINT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (empleado_id) REFERENCES empleado(id) ON DELETE CASCADE
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES rol(id)
);

-- CREAR TABLAS EXPANSION

CREATE TABLE tipo_ausencia (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE solicitud_ausencia (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id        BIGINT NOT NULL,
    tipo_ausencia_id   BIGINT NOT NULL,
    fecha_inicio       DATE NOT NULL,
    fecha_fin          DATE NOT NULL,
    dias_solicitados   INT,
    motivo             TEXT,
    estado             VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    comentario_admin   TEXT,
    fecha_solicitud    DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (empleado_id)      REFERENCES empleado(id),
    FOREIGN KEY (tipo_ausencia_id) REFERENCES tipo_ausencia(id)
);

CREATE TABLE registro_ausencia (
    id                      BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    id_empleado_solicitud   BIGINT NOT NULL,
    id_admin                BIGINT NULL,
    id_solicitud            BIGINT NOT NULL,
    tipo_solicitud          BIGINT NOT NULL,
    fecha_registro          DATETIME NOT NULL,
    comentario_admin        TEXT NULL,

    FOREIGN KEY (id_empleado_solicitud) REFERENCES empleado(id),
    FOREIGN KEY (id_admin) REFERENCES usuario(id) ON DELETE SET NULL,
    FOREIGN KEY (id_solicitud) REFERENCES solicitud_ausencia(id),
    FOREIGN KEY (tipo_solicitud) REFERENCES tipo_ausencia(id)
);

-- TRIGGER PARA CALCULAR CAMPO DIAS SOLICITADOS
DROP TRIGGER IF EXISTS calcular_dias_solicitados;
DELIMITER $$
CREATE TRIGGER calcular_dias_solicitados
BEFORE INSERT ON solicitud_ausencia
FOR EACH ROW
BEGIN
    SET NEW.dias_solicitados = DATEDIFF(NEW.fecha_fin, NEW.fecha_inicio) + 1;
END$$
DELIMITER ;

-- EMPRESA
INSERT INTO empresa (razon_social, nombre_comercial, forma_juridica, cif, direccion_fiscal, telefono, email)
VALUES ('Soluciones Digitales Mediterráneo S.L.', 'MedTech Labs', 'S.L.', 'B98765432', 'Av. del Puerto 45, Valencia', '960001122', 'info@medtechlabs-ejemplo.com');

-- SEDE
INSERT INTO sede (empresa_id, tipo, calle, numero, cp, ciudad, provincia, email_contacto)
VALUES (1, 'Central', 'Av. del Puerto', '45', '46021', 'Valencia', 'Valencia', 'sede.central@medtechlabs-ejemplo.com');

-- DEPARTAMENTOS
INSERT INTO departamento (empresa_id, codigo, nombre, descripcion) VALUES
(1, 'DIR', 'Dirección',         'Dirección ejecutiva y estrategia corporativa'),
(1, 'DEV', 'Desarrollo',        'Backend, frontend y fullstack'),
(1, 'QA',  'Calidad',           'Testing, automatización y control de calidad'),
(1, 'IT',  'Sistemas',          'Infraestructura, redes y soporte técnico'),
(1, 'HR',  'Recursos Humanos',  'Gestión de personas, selección y cultura'),
(1, 'PM',  'Proyectos',         'Product owners, scrum masters y gestión de proyectos');

-- CATEGORÍAS LABORALES
INSERT INTO categoria_laboral (empresa_id, convenio, grupo_profesional, nivel, descripcion) VALUES
(1, 'Convenio TIC', 'Programador Junior',  'I',   'Perfil de entrada, formación continua'),
(1, 'Convenio TIC', 'Programador Senior',  'II',  'Perfil avanzado con autonomía técnica'),
(1, 'Convenio TIC', 'Arquitectura',        'III', 'Alta especialización y toma de decisiones técnicas'),
(1, 'Convenio TIC', 'Manager',             'IV',  'Dirección de equipos y responsabilidad de área'),
(1, 'Convenio TIC', 'Alta Dirección',      'V',   'Dirección ejecutiva y representación corporativa'),
(1, 'Convenio TIC', 'Alta Dirección',      'VI',  'Dirección técnica y liderazgo de arquitectura global');

-- PUESTOS DE TRABAJO
INSERT INTO puesto_trabajo (sede_id, departamento_id, categoria_referencia, nombre, descripcion_funciones, jornada, modalidad, activo) VALUES
(1, 1, 5, 'CEO - Director General',              'Estrategia corporativa, representación legal y dirección global de la empresa.',         'Completa', 'Presencial', TRUE),
(1, 1, 6, 'CTO - Director Técnico',              'Liderazgo técnico, definición de arquitectura y supervisión del equipo de desarrollo.',   'Completa', 'Híbrido',    TRUE),
(1, 6, 4, 'Head of Product / Product Owner',     'Gestión del roadmap del producto, priorización y comunicación con stakeholders.',        'Completa', 'Híbrido',    TRUE),
(1, 5, 4, 'Director de RRHH',                    'Gestión de personas, selección de talento, cultura organizacional y bienestar.',         'Completa', 'Presencial', TRUE),
(1, 2, 1, 'Programador Junior',                  'Desarrollo de funcionalidades bajo supervisión, resolución de bugs y documentación.',    'Completa', 'Híbrido',    TRUE),
(1, 2, 2, 'Programador Senior',                  'Desarrollo avanzado, revisión de código, mentoría a juniors y diseño técnico.',          'Completa', 'Remoto',     TRUE),
(1, 2, 3, 'Arquitecto Software',                 'Diseño de arquitectura de sistemas, evaluación tecnológica y estándares de desarrollo.', 'Completa', 'Presencial', TRUE),
(1, 3, 2, 'QA Tester',                           'Diseño y ejecución de planes de prueba, automatización y reporte de incidencias.',       'Completa', 'Híbrido',    TRUE),
(1, 4, 2, 'Técnico de Sistemas',                 'Administración de servidores, redes, backups y soporte técnico interno.',                'Completa', 'Presencial', TRUE),
(1, 6, 3, 'Scrum Master',                        'Facilitación de ceremonias ágiles, eliminación de impedimentos y coaching de equipos.',  'Completa', 'Híbrido',    TRUE);

-- EMPLEADOS (DATOS FICTICIOS)
INSERT INTO empleado (puesto_actual, categoria_real, dni, nombre, apellidos, email_corporativo, email_personal, telefono, direccion, numero_ss, fecha_nacimiento, fecha_alta, fecha_baja, estado_laboral, dias_vacaciones_disponibles) VALUES
(1,  5, '00000001A', 'Alejandro',      'Vega Morales',       'alejandro.vega@medtechlabs-ejemplo.com',     'avega.personal@ejemplo.com',     '600000001', 'C/ Ficticia 10, Valencia',      '460000000001', '1981-04-12', '2020-03-01', NULL, 'Activo', 22),
(2,  6, '00000002B', 'Carlos',         'Mendoza Silva',      'carlos.mendoza@medtechlabs-ejemplo.com',     'cmendoza.personal@ejemplo.com',  '600000002', 'Av. Imaginaria 24, Valencia',   '460000000002', '1983-06-22', '2021-05-02', NULL, 'Activo', 22),
(3,  4, '00000003C', 'Sofía',          'Navarro Ruiz',       'sofia.navarro@medtechlabs-ejemplo.com',      'snavarro.personal@ejemplo.com',  '600000003', 'C/ del Bosque 8, Valencia',     '460000000003', '1991-11-05', '2022-06-15', NULL, 'Activo', 22),
(4,  4, '00000004D', 'Javier',         'Ortega Castillo',    'javier.ortega@medtechlabs-ejemplo.com',      'jortega.personal@ejemplo.com',   '600000004', 'Plaza Norte 3, Valencia',       '460000000004', '1986-02-18', '2021-09-01', NULL, 'Activo', 22),
(5,  1, '00000005E', 'Lucía',          'Ferrer Romero',      'lucia.ferrer@medtechlabs-ejemplo.com',       'lferrer.personal@ejemplo.com',   '600000005', 'C/ Primavera 19, Valencia',     '460000000005', '1998-08-30', '2023-04-01', NULL, 'Activo', 22),
(6,  2, '00000006F', 'Elena',          'Sánchez Pardo',      'elena.sanchez@medtechlabs-ejemplo.com',      'esanchez.personal@ejemplo.com',  '600000006', 'C/ Mirador 12, Valencia',       '460000000006', '1995-05-14', '2022-01-10', NULL, 'Activo', 22),
(7,  3, '00000007G', 'Miguel Ángel',   'Torres Gimeno',      'miguelangel.torres@medtechlabs-ejemplo.com', 'matorres.personal@ejemplo.com',  '600000007', 'C/ Horizonte 41, Valencia',     '460000000007', '1987-12-09', '2021-02-01', NULL, 'Activo', 22),
(8,  2, '00000008H', 'David',          'Cano Beltrán',       'david.cano@medtechlabs-ejemplo.com',         'dcano.personal@ejemplo.com',     '600000008', 'Av. Central 66, Valencia',      '460000000008', '1993-01-25', '2022-09-01', NULL, 'Activo', 22),
(9,  2, '00000009J', 'Paula',          'Ibáñez Lozano',      'paula.ibanez@medtechlabs-ejemplo.com',       'pibanez.personal@ejemplo.com',   '600000009', 'C/ Jardines 2, Valencia',       '460000000009', '1990-07-19', '2020-09-01', NULL, 'Activo', 22);

-- ROLES
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('MODERADOR'), ('USER');

-- USUARIOS (ACTUALIZADOS CON LOS NUEVOS NOMBRES)
INSERT INTO usuario (empleado_id, username, password_hash, activo) VALUES
(1, 'alejandro.vega',     '123456', TRUE),
(2, 'carlos.mendoza',     '123456', TRUE),
(3, 'sofia.navarro',      '123456', TRUE),
(4, 'javier.ortega',      '123456', TRUE),
(5, 'lucia.ferrer',       '123456', TRUE),
(6, 'elena.sanchez',      '123456', TRUE),
(7, 'miguelangel.torres', '123456', TRUE),
(8, 'david.cano',         '123456', TRUE),
(9, 'paula.ibanez',       '123456', TRUE);

-- USUARIO_ROL
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 2),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(9, 3);

-- TIPO AUSENCIA
INSERT INTO tipo_ausencia (nombre) VALUES
('Vacaciones'),
('Enfermedad'),
('Asuntos propios'),
('Cambio de domicilio'),
('Enfermedad de familiar'),
('Matrimonio'),
('Maternidad / Paternidad');

-- SOLICITUDES DE AUSENCIA
INSERT INTO solicitud_ausencia (empleado_id, tipo_ausencia_id, fecha_inicio, fecha_fin, motivo, estado, comentario_admin, fecha_solicitud) VALUES
(1, 1, '2026-06-03', '2026-06-09', 'Vacaciones inicio de verano',          'PENDIENTE', NULL, '2026-05-28'),
(1, 2, '2026-06-16', '2026-06-17', 'Baja médica por gripe',                'PENDIENTE', NULL, '2026-05-29'),
(1, 1, '2026-06-23', '2026-06-28', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-30'),
(1, 3, '2026-09-14', '2026-09-14', 'Gestión con la Seguridad Social',      'PENDIENTE', NULL, '2026-05-30'),
(2, 1, '2026-06-02', '2026-06-04', 'Vacaciones planificadas junio',        'PENDIENTE', NULL, '2026-05-27'),
(2, 3, '2026-06-10', '2026-06-10', 'Trámite notarial',                     'PENDIENTE', NULL, '2026-05-28'),
(2, 1, '2026-06-22', '2026-06-29', 'Vacaciones final de junio',            'PENDIENTE', NULL, '2026-05-29'),
(2, 5, '2026-10-05', '2026-10-06', 'Cuidado de familiar enfermo',          'PENDIENTE', NULL, '2026-05-30'),
(3, 3, '2026-06-04', '2026-06-04', 'Cita médica especialista',             'PENDIENTE', NULL, '2026-05-26'),
(3, 1, '2026-06-09', '2026-06-13', 'Vacaciones verano',                    'PENDIENTE', NULL, '2026-05-27'),
(3, 1, '2026-06-23', '2026-06-27', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-28'),
(3, 2, '2026-08-25', '2026-08-26', 'Baja médica por lumbalgia',            'PENDIENTE', NULL, '2026-05-29'),
(4, 1, '2026-06-01', '2026-06-03', 'Vacaciones inicio de junio',           'PENDIENTE', NULL, '2026-05-26'),
(4, 5, '2026-06-09', '2026-06-10', 'Familiar con intervención quirúrgica', 'PENDIENTE', NULL, '2026-05-27'),
(4, 1, '2026-06-16', '2026-06-29', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-28'),
(4, 4, '2026-09-07', '2026-09-08', 'Mudanza a nuevo domicilio',            'PENDIENTE', NULL, '2026-05-29'),
(5, 1, '2026-06-03', '2026-06-05', 'Vacaciones de verano',                 'PENDIENTE', NULL, '2026-05-27'),
(5, 3, '2026-06-15', '2026-06-15', 'Renovación documentación',             'PENDIENTE', NULL, '2026-05-28'),
(5, 1, '2026-06-22', '2026-06-27', 'Vacaciones segunda quincena',          'PENDIENTE', NULL, '2026-05-29'),
(5, 2, '2026-11-03', '2026-11-04', 'Baja médica por fiebre',               'PENDIENTE', NULL, '2026-05-30'),
(6, 2, '2026-06-02', '2026-06-03', 'Baja médica por migraña',              'PENDIENTE', NULL, '2026-05-29'),
(6, 1, '2026-06-08', '2026-06-10', 'Vacaciones junio',                     'PENDIENTE', NULL, '2026-05-27'),
(6, 1, '2026-06-22', '2026-06-29', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-28'),
(6, 3, '2026-10-19', '2026-10-19', 'Gestión bancaria',                     'PENDIENTE', NULL, '2026-05-30'),
(7, 1, '2026-06-01', '2026-06-02', 'Vacaciones inicio junio',              'PENDIENTE', NULL, '2026-05-26'),
(7, 5, '2026-06-09', '2026-06-10', 'Cuidado de hijo enfermo',              'PENDIENTE', NULL, '2026-05-27'),
(7, 1, '2026-06-16', '2026-06-29', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-28'),
(7, 3, '2026-09-28', '2026-09-28', 'Trámite con hacienda',                 'PENDIENTE', NULL, '2026-05-29'),
(8, 1, '2026-06-03', '2026-06-05', 'Vacaciones de verano',                 'PENDIENTE', NULL, '2026-05-27'),
(8, 3, '2026-06-15', '2026-06-15', 'Cita médica especialista',             'PENDIENTE', NULL, '2026-05-28'),
(8, 1, '2026-06-22', '2026-06-26', 'Vacaciones segunda quincena junio',    'PENDIENTE', NULL, '2026-05-29'),
(8, 2, '2026-12-01', '2026-12-02', 'Baja médica por lumbalgia',            'PENDIENTE', NULL, '2026-05-30'),
(9, 1, '2026-06-02', '2026-06-08', 'Vacaciones inicio junio',              'PENDIENTE', NULL, '2026-05-26'),
(9, 2, '2026-06-16', '2026-06-17', 'Baja médica por gripe',                'PENDIENTE', NULL, '2026-05-27'),
(9, 1, '2026-06-22', '2026-06-24', 'Vacaciones fin de junio',              'PENDIENTE', NULL, '2026-05-28'),
(9, 4, '2026-10-12', '2026-10-13', 'Mudanza a nuevo piso',                 'PENDIENTE', NULL, '2026-05-29');