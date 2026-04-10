-- =====================================
-- 1) CREAR USUARIO
-- =====================================
DROP USER IF EXISTS 'usuario_inventario'@'localhost';

CREATE USER 'usuario_inventario'@'localhost'
IDENTIFIED BY '123456';

-- =====================================
-- 2) CREAR BASE DE DATOS
-- =====================================
DROP DATABASE IF EXISTS inventario_emergencias;

CREATE DATABASE inventario_emergencias;

GRANT ALL PRIVILEGES ON inventario_emergencias.*
TO 'usuario_inventario'@'localhost';

FLUSH PRIVILEGES;

USE inventario_emergencias;

-- =====================================
-- 3) TABLA USUARIO
-- =====================================
CREATE TABLE usuario (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    correo   VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol      ENUM('ADMIN','COLABORADOR') NOT NULL,
    activo   BOOLEAN DEFAULT TRUE
);

-- =====================================
-- 4) TABLA CATEGORIA
-- =====================================
CREATE TABLE categoria (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- =====================================
-- 5) TABLA PROVEEDOR
-- =====================================
CREATE TABLE proveedor (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    correo   VARCHAR(100)
);

-- =====================================
-- 6) TABLA BODEGA
-- =====================================
CREATE TABLE bodega (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(255)
);

-- =====================================
-- 7) TABLA ITEM
-- =====================================
CREATE TABLE item (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     TEXT,
    categoria_id    INT NOT NULL,
    proveedor_id    INT,
    stock           INT DEFAULT 0,
    stock_minimo    INT DEFAULT 5,
    unidad_medida   VARCHAR(50) DEFAULT 'Unidad',
    tiene_caducidad BOOLEAN DEFAULT FALSE,
    fecha_caducidad DATE,
    fecha_creacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo          BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
);

-- =====================================
-- 8) TABLA MOVIMIENTO
-- =====================================
CREATE TABLE movimiento (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    item_id          INT NOT NULL,
    usuario_id       INT NOT NULL,
    tipo             ENUM('ENTRADA','SALIDA','TRANSFERENCIA') NOT NULL,
    cantidad         INT NOT NULL,
    fecha            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motivo           VARCHAR(255),
    observaciones    VARCHAR(255),
    bodega_origen_id INT,
    bodega_destino_id INT,
    FOREIGN KEY (item_id)           REFERENCES item(id),
    FOREIGN KEY (usuario_id)        REFERENCES usuario(id),
    FOREIGN KEY (bodega_origen_id)  REFERENCES bodega(id),
    FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id)
);

-- =====================================
-- 9) TABLA ALERTA
-- =====================================
CREATE TABLE alerta (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    item_id     INT NOT NULL,
    tipo        ENUM('VENCIMIENTO','STOCK_BAJO') NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enviada     BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (item_id) REFERENCES item(id)
);

-- =====================================
-- 10) TABLA RESPALDO_LOG
-- =====================================
CREATE TABLE respaldo_log (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    fecha         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado        ENUM('EXITOSO','FALLIDO') NOT NULL,
    observaciones VARCHAR(255)
);

-- =====================================
-- DATOS DE PRUEBA
-- =====================================

INSERT INTO categoria (nombre) VALUES
('Equipos de Emergencia'),
('Insumos Médicos'),
('Herramientas'),
('Materiales de Rescate');

INSERT INTO proveedor (nombre, telefono, correo) VALUES
('Proveedor Seguridad CR', '2222-1111', 'ventas@seguridadcr.com'),
('MedEquip Costa Rica',    '2255-8899', 'contacto@medequip.cr'),
('Herramientas Industriales', '2299-4455', 'info@herramientascr.com');

INSERT INTO bodega (nombre, ubicacion) VALUES
('Bodega Principal',  'Municipalidad de Escazú, Planta Baja'),
('Bodega Secundaria', 'Municipalidad de Escazú, Segundo Piso');

-- Nota: reemplazar los password con hashes BCrypt reales antes de la entrega
INSERT INTO usuario (nombre, correo, password, rol) VALUES
('Administrador Sistema', 'admin@inventario.com',  '$2a$10$reemplazarConHashBCrypt', 'ADMIN'),
('Carlos Perez',          'carlos@inventario.com', '$2a$10$reemplazarConHashBCrypt', 'COLABORADOR');

INSERT INTO item (nombre, descripcion, categoria_id, proveedor_id, stock, stock_minimo, unidad_medida, tiene_caducidad, fecha_caducidad) VALUES
('Botiquín de primeros auxilios', 'Kit completo de emergencias', 2, 2, 20,  5,  'Kit',    TRUE,  '2027-05-01'),
('Casco de seguridad',            'Casco para rescate',          1, 1, 15,  3,  'Unidad', FALSE, NULL),
('Sierra eléctrica',              'Herramienta para rescate',    3, 3,  8,  2,  'Unidad', FALSE, NULL),
('Guantes médicos',               'Guantes desechables',         2, 2, 100, 20, 'Caja',   TRUE,  '2026-08-10');

INSERT INTO movimiento (item_id, usuario_id, tipo, cantidad, motivo, bodega_destino_id) VALUES
(1, 2, 'ENTRADA',  10, 'Compra inicial',              1),
(2, 2, 'SALIDA',    2, 'Uso en simulacro',           NULL),
(4, 2, 'ENTRADA',  50, 'Reposición de inventario',    1);

CREATE TABLE ruta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ruta VARCHAR(255) NOT NULL,
    requiere_rol BOOLEAN NOT NULL,
    rol ENUM('ADMIN','COLABORADOR') NULL
);

-- públicas  
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES  
('/login', false, NULL),  
('/css/**', false, NULL),  
('/js/**', false, NULL),  
('/images/**', false, NULL),  
('/webjars/**', false, NULL);  

-- dashboard  
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES  
('/', true, NULL),  
('/index', true, NULL);  

-- ADMIN  
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES  
('/categoria/**', true, 'ADMIN'),  
('/usuario/**', true, 'ADMIN'),  
('/proveedor/**', true, 'ADMIN');  

-- COLABORADOR  
INSERT INTO ruta (ruta, requiere_rol, rol) VALUES  
('/item/**', true, 'COLABORADOR'),  
('/movimiento/**', true, 'COLABORADOR');
