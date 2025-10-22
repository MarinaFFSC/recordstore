# Locadora de Mídias Físicas de Música

Sistema desenvolvido em Java para o gerenciamento de locação de mídias físicas (CDs e vinis).  
O projeto implementa princípios de DDD (Domain-Driven Design) e BDD (Behavior-Driven Development), aplicando regras de negócio reais e cenários de teste automatizados.

## Visão Geral do Domínio

O sistema tem como objetivo gerenciar acervo físico de mídias musicais, permitindo o controle completo do ciclo de locação:

- Sócios: podem pesquisar o catálogo, realizar locações e acompanhar o histórico de empréstimos.  
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

4. Validação de sócios antes de locação
   - Somente sócios ativos podem realizar locações.
   - Clientes bloqueados ou suspensos não conseguem iniciar novas locações.
   - A validação é feita no momento da confirmação do empréstimo.

5. Renovação condicionada à ausência de multas
   Um empréstimo só poderá ser renovado se:
   - O sócios não possuir multas pendentes;
   - Não houver atraso no empréstimo atual.
   - Caso alguma dessas condições não seja atendida, o sistema bloqueia a renovação e informa o motivo.

6. Bloqueio automático de clientes inadimplentes
   - Sócios com multas pendentes ficam automaticamente bloqueados para realizar novas locações ou renovações até regularizarem a situação.
   - Caso tentem iniciar uma operação, o sistema deve negar e exibir uma mensagem clara informando a pendência.

7. Limite de empréstimos simultâneos por cliente
   - Cada sócio possui um limite máximo de empréstimos ativos simultaneamente (por exemplo, 3 mídias).
   - Ao tentar realizar um novo empréstimo além desse limite, o sistema deve recusar a operação e informar o motivo.

8. Registro obrigatório de condição física na devolução
   - No momento de registrar uma devolução, o administrador deve informar obrigatoriamente a condição física do exemplar (bom, regular, danificado).
   - Caso seja informado “danificado”, o sistema atualiza o status do exemplar para “indisponível” e impede novos empréstimos até que haja manutenção.

APRESENTAÇÃO:
https://www.canva.com/design/DAG2Dbp-Xrw/KDwSES5gceaxJBfvD2pDIw/view?utm_content=DAG2Dbp-Xrw&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=h8281444297

PROTÓTIPO (FIGMA):
https://www.figma.com/design/TM3M17MpYg7xxGHuBtwaS5/Requisitos-e-Valida%C3%A7%C3%A3o-Software?node-id=0-1&p=f

