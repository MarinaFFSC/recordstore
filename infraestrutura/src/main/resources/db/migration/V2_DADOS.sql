-- V2_DADOS.sql

-- ARTISTAS
insert into ARTISTA(NOME) values (
    'Pink Floyd'
);

insert into ARTISTA(NOME) values (
    'The Beatles'
);

insert into ARTISTA(NOME) values (
    'Miles Davis'
);

insert into ARTISTA(NOME) values (
    'David Bowie'
);

insert into ARTISTA(NOME) values (
    'Queen'
);

-- MIDIAS + RELAÇÃO COM ARTISTAS + EXEMPLARES

-- 1) Pink Floyd - The Dark Side of the Moon
insert into MIDIA(ID, TITULO) values (
    '7890001112223',
    'The Dark Side of the Moon'
);

insert into MIDIA_ARTISTA(MIDIA_ID, ARTISTA_ID, ARTISTA_ORDEM) values (
    '7890001112223',
    1,
    0
);

insert into EXEMPLAR (ID, MIDIA_ID) values (
    1,
    '7890001112223'
);

-- 2) The Beatles - Abbey Road
insert into MIDIA(ID, TITULO) values (
    '7890001112224',
    'Abbey Road'
);

insert into MIDIA_ARTISTA(MIDIA_ID, ARTISTA_ID, ARTISTA_ORDEM) values (
    '7890001112224',
    2,
    0
);

insert into EXEMPLAR (ID, MIDIA_ID) values (
    2,
    '7890001112224'
);

-- 3) Miles Davis - Kind of Blue
insert into MIDIA(ID, TITULO) values (
    '7890001112225',
    'Kind of Blue'
);

insert into MIDIA_ARTISTA(MIDIA_ID, ARTISTA_ID, ARTISTA_ORDEM) values (
    '7890001112225',
    3,
    0
);

insert into EXEMPLAR (ID, MIDIA_ID) values (
    3,
    '7890001112225'
);

-- 4) David Bowie - The Rise and Fall of Ziggy Stardust
insert into MIDIA(ID, TITULO, SUBTITULO) values (
    '7890001112226',
    'The Rise and Fall of Ziggy Stardust',
    'and the Spiders from Mars'
);

insert into MIDIA_ARTISTA(MIDIA_ID, ARTISTA_ID, ARTISTA_ORDEM) values (
    '7890001112226',
    4,
    0
);

insert into EXEMPLAR (ID, MIDIA_ID) values (
    4,
    '7890001112226'
);

-- 5) Queen - A Night at the Opera
insert into MIDIA(ID, TITULO) values (
    '7890001112227',
    'A Night at the Opera'
);

insert into MIDIA_ARTISTA(MIDIA_ID, ARTISTA_ID, ARTISTA_ORDEM) values (
    '7890001112227',
    5,
    0
);

insert into EXEMPLAR (ID, MIDIA_ID) values (
    5,
    '7890001112227'
);

-- SÓCIO DE TESTE
insert into SOCIO(NOME, EMAIL) values (
    'Cliente de Teste da RecordStore',
    'cliente@recordstore.dev'
);
