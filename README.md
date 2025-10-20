# Locadora de Mídias Físicas de Música

Sistema desenvolvido em Java para o gerenciamento de locação de mídias físicas (CDs e vinis).  
O projeto implementa princípios de DDD (Domain-Driven Design) e BDD (Behavior-Driven Development), aplicando regras de negócio reais e cenários de teste automatizados.

## Visão Geral do Domínio

O sistema tem como objetivo gerenciar acervo físico de mídias musicais, permitindo o controle completo do ciclo de locação:

- Clientes: podem pesquisar o catálogo, realizar locações e acompanhar o histórico de empréstimos.  
- Administradores: gerenciam o catálogo, usuários, reservas e devoluções.

### Subdomínios principais

- Gerenciamento de Catálogo: Cadastro, edição e controle de disponibilidade das mídias.  
- Controle de Empréstimos: Processos de locação, devolução, reservas e cálculo automático de multas.  
- Gestão de Usuários: Cadastro, aprovação e bloqueio de clientes.  

## Regras de Negócio

As principais regras implementadas no sistema são:

1. Disponibilidade obrigatória para locação 
   - Uma mídia só pode ser alugada se estiver com status disponível 
   - Mídias alugadas não podem ser emprestadas novamente até devolução 
   - Subdomínios: Catálogo + Controle de Empréstimos  

2. Atualização automática de status 
   - Ao confirmar um empréstimo, o status muda para alugada  
   - Ao registrar devolução, o status retorna para disponível ou indisponível (caso danificada).  
   - Subdomínios: Catálogo + Controle de Empréstimos

3. Cobrança de multa por devoluções atrasadas
   - O sistema calcula automaticamente o valor: multa = dias de atraso × valor diário fixo.  
   - A multa deve ser quitada antes de novas locações  
   - Subdomínio: Controle de Empréstimos

4. Validação de clientes antes da locação
   - Apenas clientes ativos podem realizar locações
   - Subdomínios: Gestão de Usuários + Controle de Empréstimos


APRESENTAÇÃO:
https://www.canva.com/design/DAG2Dbp-Xrw/KDwSES5gceaxJBfvD2pDIw/view?utm_content=DAG2Dbp-Xrw&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h8281444297

PROTÓTIPO (FIGMA):


