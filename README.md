# Sistema de Locadora de Mídias — Gestão Completa de Acervo, Sócios, Empréstimos e Multas

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Vaadin](https://img.shields.io/badge/Vaadin-1572B6?style=for-the-badge&logo=vaadin&logoColor=white)
![BDD](https://img.shields.io/badge/BDD-Cucumber-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

## Descrição do Projeto
Sistema desenvolvido em **Java + Spring Boot + Vaadin + MySQL**, com testes comportamentais baseados em **BDD (Cucumber)**.
O objetivo é realizar o **gerenciamento completo da locação de mídias físicas** (CDs e vinis), aplicando:

- **DDD (Domain-Driven Design)**
- **BDD (Behavior-Driven Development)**
- **Padrões de Projeto (Iterator, Proxy, Strategy, Observer)**

## Visão Geral do Domínio
### Sócios
- Consultam o catálogo  
- Realizam empréstimos  
- Devolvem mídias  
- Visualizam multas e histórico  
### Administradores
- Gerenciam catálogo  
- Cadastram e editam sócios  
- Controlam multas  
- Acompanham empréstimos  

## Subdomínios Principais
- **Gerenciamento de Catálogo** (artistas, mídias, exemplares)  
- **Controle de Empréstimos** (empréstimo, devolução, cálculo automático de multas)  
- **Gestão de Usuários** (cadastro, edição e validação de pendências)  

## Protótipos e Documentação
- Documentação por entrega
    - Entrega 1: https://drive.google.com/drive/folders/1EHRZCaNonCRC9wP_TEXDCAS-yGs_yjBt?usp=drive_link
    - Entrega 2: https://drive.google.com/drive/folders/1Kl6AK6LYR0uapOnbydTWwRVs9KUbvOV1?usp=drive_link
- Figma: https://www.figma.com/design/TM3M17MpYg7xxGHuBtwaS5/Requisitos-e-Validação-Software?node-id=0-1&p=f  


## Testes BDD (Cucumber)
Todos os fluxos críticos foram descritos em cenários Given/When/Then.
Cenários incluem:
- Empréstimo válido  
- Bloqueio por multas pendentes  
- Devolução atrasada gerando multa  
- Editar e excluir itens do catálogo  
- Gestão de usuários
  
# Arquitetura da Aplicação
### Frontend (Vaadin)
- Interface declarativa e reativa  
- Atualização automática via eventos de UI  
### Backend
- **Domínio**: Regras de negócio, entidades, serviços  
- **Aplicação**: Orquestra processos  
- **Infraestrutura**: JPA/MySQL  
- **BDD**: Testes funcionais com Cucumber
  
## Padrões de Projeto Utilizados
| Padrão | Implementação | Benefício |
|-------|---------------|-----------|
| **Iterator** | Iteração segura sobre exemplares | Evita acoplamento |
| **Proxy** | EmprestimoServicoProxy | Intercepta violações de regras (multas, suspensão) |
| **Strategy** | Cálculo de multas | Centraliza e facilita manutenção |
| **Observer** | Events no Vaadin | Interface sempre sincronizada |

## Guia de Execução do Sistema ( Passo a passo usando Eclipse)
### 1. Pré-requisitos
Instale:
- Java **JDK 21**  
- MySQL 8.0 (ou Docker)  
- **Eclipse IDE** com suporte a Maven  
- Plugin **Spring Tools Suite (STS)** e **Cucumber 3.0**

### Importando o Projeto no Eclipse
1. Abra o **Eclipse**  
2. Vá em **File -> Import**  
3. Selecione **Existing Maven Project**  
4. Escolha a pasta raiz do projeto  
5. Aguarde o Maven baixar todas as dependências  

### Como Rodar a Aplicação
1. Localize a classe principal do Spring Boot ( `VaadinApresencacao.java` )  
2. Clique com o botão direito → **Run As -> Java Application**

### Como Rodar os Testes
1. Localize a classe runner ( `RunCucumberTest.java` )  
2. Clique com o botão direito → **Run As -> JUnit**

### Como Simular as multas:
1.Procurar no (Catalogo, MinhasMultas, MultasAdmin, MeusEmprestimos) uma função que possui a linha (fimPrevisto, LocalDate.now())
2.Trocar o LocalDate.now() por uma função chamada hojeParaTestar(simula 10 dias com o exemplar, ou seja 3 dias de multa)
