# 3) CÁLCULO DE MULTAS POR ATRASO
# language: pt
Funcionalidade: CÁLCULO DE MULTAS POR ATRASO
  Como administrador
  Quero calcular e registrar multas quando houver devoluções em atraso
  Para garantir a cobrança correta e o controle de atrasos

  Cenário: Devolução em atraso calcula multa
  Dado que a data prevista de devolução é "2025-10-01"
  E a data real de devolução é "2025-10-05"
  E o valor da multa diária é "2.50"
  Quando o administrador confirma o registro da devolução
  Então o total de multa calculado deve ser "10.00"
  E o valor da multa no empréstimo deve ser "10.00"
  E o sistema deve vincular a multa ao empréstimo

  Cenário: Devolução dentro do prazo não gera multa
  Dado que a data prevista de devolução é "2025-10-05"
  E a data real de devolução é "2025-10-05"
  E o valor da multa diária é "2.50"
  Quando o administrador confirma o registro da devolução
  Então não deve haver multa calculada
