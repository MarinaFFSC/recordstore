# 2) ATUALIZAÇÃO AUTOMÁTICA DE STATUS DE EXEMPLAR
# language: pt
Funcionalidade: ATUALIZAÇÃO AUTOMÁTICA DE STATUS DE EXEMPLAR
  Como administrador
  Quero atualizar automaticamente o status do exemplar após devoluções
  Para manter o catálogo sempre coerente com o estado físico dos itens

  Cenário: Devolução com condição informada
  Dado há um empréstimo ativo associado a este exemplar
  Quando o administrador efetua o registro da devolução informando a condição "DANIFICADO"
  Então o sistema deve marcar o exemplar como "INDISPONIVEL"
  E registrar o evento "CONDICAO_INFORMADA"

  Cenário: Devolução sem informar condição física é recusada
  Dado há um empréstimo ativo associado a este exemplar
  Quando o administrador tenta registrar a devolução sem informar a condição física
  Então o sistema deve rejeitar o registro de devolução
  E a mensagem apresentada deve ser "Condição física obrigatória"
