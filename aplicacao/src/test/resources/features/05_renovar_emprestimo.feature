# 5) RENOVAR EMPRÉSTIMO
# language: pt
Funcionalidade: RENOVAR EMPRÉSTIMO
  Como administrador
  Quero renovar empréstimos de exemplares dentro do prazo e sem multas pendentes
  Para permitir a extensão do prazo de forma controlada e justa

  Cenário: Renovação permitida quando dentro do prazo e sem multas
  Dado que o empréstimo do exemplar "DVD Matrix" encontra-se dentro do prazo
  E o sócio "João" não possui multas pendentes
  Quando o administrador solicita a prorrogação do empréstimo
  Então o sistema deve conceder a renovação
  E a nova data prevista de devolução deve ser posterior à atual

  Cenário: Renovação negada quando há atraso ou multa
  Dado que o empréstimo do exemplar "DVD Matrix" está em atraso
  E o sócio "João" possui multa pendente
  Quando o administrador solicita a prorrogação do empréstimo
  Então a renovação deve ser recusada
  E a mensagem de impedimento exibida deve ser "Renovação não permitida"
