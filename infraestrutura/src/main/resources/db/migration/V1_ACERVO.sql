-- V1_ACERVO.sql

create table ARTISTA (
    ID int generated always as identity not null,
    NOME varchar not null,
    primary key (ID)
);

create table MIDIA (
    ID varchar not null,
    TITULO varchar not null,
    SUBTITULO varchar,
    DESCRICAO varchar,
    primary key (ID)
);

create table MIDIA_ARTISTA (
    MIDIA_ID varchar not null,
    ARTISTA_ID integer not null,
    ARTISTA_ORDEM integer not null,
    primary key (MIDIA_ID, ARTISTA_ID),
    foreign key (ARTISTA_ID) references ARTISTA(ID),
    foreign key (MIDIA_ID) references MIDIA(ID)
);

create table SOCIO (
    ID int generated always as identity not null,
    NOME varchar not null,
    EMAIL varchar not null,
    primary key (ID)
);

create table EXEMPLAR (
    ID integer not null,
    MIDIA_ID varchar not null,
    EMPRESTIMO_PERIODO_INICIO date null,
    EMPRESTIMO_PERIODO_FIM date null,
    EMPRESTIMO_TOMADOR_ID int null,	
    primary key (ID),
    foreign key (MIDIA_ID) references MIDIA(ID),
    foreign key (EMPRESTIMO_TOMADOR_ID) references SOCIO(ID)
);

create table EMPRESTIMO_REGISTRO (
    ID int generated always as identity not null,
    EXEMPLAR_ID integer not null,
    EMPRESTIMO_PERIODO_INICIO date not null,
    EMPRESTIMO_PERIODO_FIM date not null,    
    EMPRESTIMO_TOMADOR_ID integer not null,
    DEVOLUCAO date null,
    primary key (ID),
    foreign key (EXEMPLAR_ID) references EXEMPLAR(ID),
    foreign key (EMPRESTIMO_TOMADOR_ID) references SOCIO(ID)
);
