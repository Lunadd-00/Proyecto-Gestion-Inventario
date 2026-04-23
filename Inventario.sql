-- 1) CREAR USUARIO
DROP USER IF EXISTS 'usuario_inventario'@'localhost';

CREATE USER 'usuario_inventario'@'localhost'
IDENTIFIED BY '123456';

-- 2) CREAR BASE DE DATOS
DROP DATABASE IF EXISTS inventario_emergencias;

CREATE DATABASE inventario_emergencias;

GRANT ALL PRIVILEGES ON inventario_emergencias.*
TO 'usuario_inventario'@'localhost';

FLUSH PRIVILEGES;

USE inventario_emergencias;

-- 3) TABLA USUARIO
CREATE TABLE usuario (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    correo   VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol      ENUM('ADMIN','COLABORADOR') NOT NULL,
    activo   BOOLEAN DEFAULT TRUE
);

-- 4) TABLA CATEGORIA
CREATE TABLE categoria (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- 5) TABLA PROVEEDOR
CREATE TABLE proveedor (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo   VARCHAR(100)
);

-- 6) TABLA BODEGA
CREATE TABLE bodega (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(255)
);

-- 7) TABLA ITEM
CREATE TABLE item (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    categoria_id    INT NOT NULL,
    proveedor_id    INT,
    bodega_id       INT,
    stock           INT DEFAULT 0,
    stock_minimo    INT DEFAULT 5,
    unidad_medida   VARCHAR(50) DEFAULT 'Unidad',
    tiene_caducidad BOOLEAN DEFAULT FALSE,
    fecha_caducidad DATE,
    fecha_creacion  DATE NOT NULL,
    activo          BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    FOREIGN KEY (bodega_id)    REFERENCES bodega(id)
);

-- 8) TABLA LOTE
CREATE TABLE lote (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    numero_lote     VARCHAR(100) UNIQUE,
    cantidad        INT NOT NULL,
    fecha_ingreso   DATE NOT NULL,
    fecha_caducidad DATE,
    activo          BOOLEAN DEFAULT TRUE,
    item_id         INT NOT NULL,
    bodega_id       INT NOT NULL,
    FOREIGN KEY (item_id)   REFERENCES item(id),
    FOREIGN KEY (bodega_id) REFERENCES bodega(id)
);

-- 9) TABLA MOVIMIENTO
CREATE TABLE movimiento (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    item_id           INT,
    usuario_id        INT NOT NULL,
    tipo              ENUM('ENTRADA','SALIDA','TRANSFERENCIA') NOT NULL,
    cantidad          INT NOT NULL,
    fecha             DATETIME NOT NULL,
    motivo            VARCHAR(255),
    observaciones     VARCHAR(255),
    bodega_origen_id  INT,
    bodega_destino_id INT,
    FOREIGN KEY (usuario_id)         REFERENCES usuario(id),
    FOREIGN KEY (bodega_origen_id)   REFERENCES bodega(id),
    FOREIGN KEY (bodega_destino_id)  REFERENCES bodega(id)
);

-- 10) TABLA RUTA
CREATE TABLE ruta (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    ruta         VARCHAR(255) NOT NULL,
    requiere_rol BOOLEAN NOT NULL,
    rol          ENUM('ADMIN','COLABORADOR') NULL
);

-- 11) TABLA CONFIGURACION ALERTA
CREATE TABLE configuracion_alerta (
    id                 BIGINT PRIMARY KEY,
    hora_vencimiento   INT NOT NULL DEFAULT 8,
    minuto_vencimiento INT NOT NULL DEFAULT 0,
    hora_stock         INT NOT NULL DEFAULT 8,
    minuto_stock       INT NOT NULL DEFAULT 5
);

-- DATOS DE PRUEBA

INSERT INTO categoria (nombre) VALUES
('Equipos de Emergencia'),
('Insumos Médicos'),
('Herramientas'),
('Materiales de Rescate');

INSERT INTO proveedor (nombre, telefono, correo) VALUES
('Proveedor Seguridad CR',    '2222-1111', 'ventas@seguridadcr.com'),
('MedEquip Costa Rica',       '2255-8899', 'contacto@medequip.cr'),
('Herramientas Industriales', '2299-4455', 'info@herramientascr.com');

INSERT INTO bodega (nombre, ubicacion) VALUES
('Bodega Principal',  'Municipalidad de Escazú, Planta Baja'),
('Bodega Secundaria', 'Municipalidad de Escazú, Segundo Piso');

-- Contraseña para ambos usuarios: 123456
INSERT INTO usuario (nombre, correo, password, rol) VALUES
('Administrador Sistema', 'admin@inventario.com',  '$2a$10$CCvSwJAKfi2FM9cKyZ5ybeW9jU.NDPX/Ht7Vk5S2AtP5UngsdOtKK', 'ADMIN'),
('Carlos Perez',          'carlos@inventario.com', '$2a$10$CCvSwJAKfi2FM9cKyZ5ybeW9jU.NDPX/Ht7Vk5S2AtP5UngsdOtKK', 'COLABORADOR');

INSERT INTO item (nombre, descripcion, categoria_id, proveedor_id, stock, stock_minimo, unidad_medida, fecha_creacion) VALUES
('Botiquín de primeros auxilios', 'Kit completo de emergencias', 2, 2, 20,  5,  'Kit',    CURDATE()),
('Casco de seguridad',            'Casco para rescate',          1, 1, 15,  3,  'Unidad', CURDATE()),
('Sierra eléctrica',              'Herramienta para rescate',    3, 3,  8,  2,  'Unidad', CURDATE()),
('Guantes médicos',               'Guantes desechables',         2, 2, 100, 20, 'Caja',   CURDATE());

INSERT INTO lote (numero_lote, cantidad, fecha_ingreso, fecha_caducidad, activo, item_id, bodega_id) VALUES
('LOT-2026-001',  20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 180 DAY), TRUE, 1, 1),
('LOT-2026-002',  15, CURDATE(), NULL,                                   TRUE, 2, 1),
('LOT-2026-003',   8, CURDATE(), NULL,                                   TRUE, 3, 2),
('LOT-2026-004', 100, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 120 DAY), TRUE, 4, 2);

INSERT INTO movimiento (item_id, usuario_id, tipo, cantidad, fecha, motivo, bodega_destino_id) VALUES
(1, 2, 'ENTRADA', 20,  NOW(), 'Stock inicial', 1),
(2, 2, 'ENTRADA', 15,  NOW(), 'Stock inicial', 1),
(3, 2, 'ENTRADA',  8,  NOW(), 'Stock inicial', 2),
(4, 2, 'ENTRADA', 100, NOW(), 'Stock inicial', 2);

INSERT INTO configuracion_alerta (id, hora_vencimiento, minuto_vencimiento, hora_stock, minuto_stock)
VALUES (1, 8, 0, 8, 5);

-- RUTAS DE SEGURIDAD

-- Públicas (sin autenticación)
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES
('/login',      FALSE, NULL),
('/css/**',     FALSE, NULL),
('/js/**',      FALSE, NULL),
('/img/**',     FALSE, NULL),
('/webjars/**', FALSE, NULL);

-- Dashboard (cualquier usuario autenticado)
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES
('/',             FALSE, NULL),
('/index',        FALSE, NULL),
('/dashboard/**', FALSE, NULL);

-- Solo ADMIN vía tabla ruta
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES
('/categoria/**', TRUE, 'ADMIN'),
('/usuario/**',   TRUE, 'ADMIN'),
('/proveedor/**', TRUE, 'ADMIN');

-- COLABORADOR y ADMIN
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES
('/item/**',       TRUE, 'COLABORADOR'),
('/lote/**',       TRUE, 'COLABORADOR'),
('/movimiento/**', TRUE, 'COLABORADOR'),
('/reporte/**',    TRUE, 'COLABORADOR');
