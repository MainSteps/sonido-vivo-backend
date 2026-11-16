-- Solo desarrollo: administrador de prueba (contraseña documentada en README)
INSERT INTO usuario (nombre, email, password_hash, rol, creado_en)
VALUES (
    'Administrador',
    'admin@sonidovivo.cl',
    '$2a$10$01CKX0QvYNgf1Cwp9qFP6.Sjj2mYD5nq9NUnRT76RO29Ao75kSu5.',
    'ADMIN',
    CURRENT_TIMESTAMP
);