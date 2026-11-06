INSERT INTO producto_suspel (id, nombre, imo, bodega, cantidad) VALUES
(1, 'Propano', 'Clase 2.1 - Gas Inflamable', 'Bodega A-01', 1200),
(2, 'Acetileno', 'Clase 2.1 - Gas Inflamable', 'Bodega A-02', 900),
(3, 'Nitrógeno Comprimido', 'Clase 2.2 - Gas No Inflamable No Tóxico', 'Bodega B-01', 600),
(4, 'Dióxido de Carbono', 'Clase 2.2 - Gas No Inflamable No Tóxico', 'Bodega B-02', 800),
(5, 'Gasolina 95', 'Clase 3 - Líquido Inflamable', 'Bodega C-01', 3000),
(6, 'Acetona', 'Clase 3 - Líquido Inflamable', 'Bodega C-02', 800),
(7, 'Azufre', 'Clase 4.1 - Sólido Inflamable', 'Bodega D-01', 2500),
(8, 'Fósforo Rojo', 'Clase 4.1 - Sólido Inflamable', 'Bodega D-02', 850),
(9, 'Carbón Activado', 'Clase 4.2 - Combustión Espontánea', 'Bodega E-01', 1200),
(10, 'Harina de Pescado', 'Clase 4.2 - Combustión Espontánea', 'Bodega E-02', 3500),
(11, 'Sodio Metálico', 'Clase 4.3 - Peligroso en Contacto con Agua', 'Bodega F-01', 700),
(12, 'Carburo de Calcio', 'Clase 4.3 - Peligroso en Contacto con Agua', 'Bodega F-02', 1500),
(13, 'Nitrato de Amonio', 'Clase 5.1 - Comburente', 'Bodega G-01', 5000),
(14, 'Hipoclorito de Calcio', 'Clase 5.1 - Comburente', 'Bodega G-02', 800),
(15, 'Peróxido de Benzoilo', 'Clase 5.2 - Peróxido Orgánico', 'Bodega H-01', 650),
(16, 'Cianuro de Sodio', 'Clase 6.1 - Tóxico', 'Bodega I-01', 800),
(17, 'Fenol', 'Clase 6.1 - Tóxico', 'Bodega I-02', 750),
(18, 'Ácido Sulfúrico 98%', 'Clase 8 - Corrosivo', 'Bodega J-01', 1500),
(19, 'Hidróxido de Sodio', 'Clase 8 - Corrosivo', 'Bodega J-02', 2000),
(20, 'Baterías de Ion Litio', 'Clase 9 - Misceláneo', 'Bodega K-01', 3000);

ALTER TABLE producto_suspel ALTER COLUMN id RESTART WITH 21;
