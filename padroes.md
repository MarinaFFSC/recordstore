# Padrões de Projeto Adotados

Este documento descreve os padrões de projeto utilizados no sistema, bem como as classes que foram criadas e/ou alteradas em função da adoção de cada padrão.

---

## Iterator

**Motivação:**  
Evitar a exposição direta de coleções internas (List<T>), reduzindo acoplamento entre domínio e interface gráfica e garantindo iteração segura e padronizada.

**Descrição:**  
O padrão Iterator foi aplicado para encapsular a iteração sobre os exemplares emprestados. Para isso, foi criada uma abstração própria que implementa `Iterable`, permitindo o uso de `for-each` e `Stream` sem expor a estrutura interna da coleção.

**Classes criadas/alteradas:**
- `ExemplaresEmprestados`  
  - Implementa `Iterable`
  - Fornece os métodos `iterator()`, `stream()` e `asList()`
- `CatalogoView` (uso do iterator)
- `MeusEmprestimosView` (uso do iterator)
- `MinhasMultasView` (uso do iterator)
- `ExemplarServicoAplicacao` (método `pesquisarEmprestadosIterable()`)

---

## Proxy (Protection Proxy)

**Motivação:**  
Centralizar regras sensíveis de negócio, especialmente validações relacionadas a multas pendentes, evitando duplicação de lógica nas Views e garantindo segurança nas operações de empréstimo e devolução.

**Descrição:**  
Foi aplicado o padrão Proxy na forma de *Protection Proxy*. O proxy intercepta chamadas ao serviço real de empréstimos e valida se o sócio pode ou não realizar a operação antes de delegá-la ao serviço original.

**Classes criadas/alteradas:**
- `EmprestimoOperacoes` (interface)
- `EmprestimoServico` (serviço real)
- `EmprestimoServicoProxy` (proxy)
- `EmprestimoConfiguracao` (configuração Spring para registrar o proxy como bean)
- Views que passaram a depender da interface `EmprestimoOperacoes` em vez do serviço concreto

---

## Strategy

**Motivação:**  
Garantir consistência no cálculo de multas e atrasos e evitar que a mesma regra de negócio fosse implementada de maneiras diferentes em múltiplas telas.

**Descrição:**  
O padrão Strategy foi utilizado para encapsular a regra de cálculo de multas em uma classe dedicada, permitindo reutilização da lógica tanto na camada de apresentação quanto no Proxy.

**Classes criadas/alteradas:**
- `MultaCalculadoraServico` (estratégia concreta de cálculo)
- `MinhasMultasView` (uso da estratégia)
- `MeusEmprestimosView` (uso da estratégia)
- `EmprestimoServicoProxy` (uso da estratégia para validação)
- Demais Views que exibem ou validam multa

---

## Observer

**Motivação:**  
Manter a interface gráfica sempre sincronizada com o estado atual dos dados após operações de criação, edição ou exclusão, evitando inconsistências visuais.

**Descrição:**  
O padrão Observer é aplicado de forma implícita por meio do modelo de eventos do Vaadin. Componentes da interface reagem automaticamente a eventos disparados por ações do usuário, como cliques em botões, salvamento ou exclusão de entidades.

**Classes criadas/alteradas:**
- `MidiaAdminView`
  - Atualização automática de grids após edição ou exclusão
- `SocioView`
  - Atualização do grid após operações de edição ou remoção
- `MinhasMultasView`
  - Reação a eventos de solicitação de pagamento
- `CatalogoView`
- `Dialog`, `Button`, `Grid` e `MultiSelectComboBox`
  - Uso de listeners (`addClickListener`, `addValueChangeListener`)

---

