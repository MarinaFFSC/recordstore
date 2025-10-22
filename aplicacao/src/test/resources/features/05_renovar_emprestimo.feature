# language: pt
Funcionalidade: RENOVAR EMPRÉSTIMO
  Como administrador
  Quero renovar empréstimos de exemplares dentro do prazo e sem multas pendentes
  Para permitir a extensão do prazo de forma controlada e justa

  Cenário: Renovação permitida quando dentro do prazo e sem multas
  Dado o empréstimo do exemplar "DVD Matrix" está dentro do prazo
  E que o sócio "João" não possui multas pendentes
  Quando o administrador solicitar a renovação do empréstimo
  Então o sistema deve permitir a renovação
  E recalcular a nova data prevista de devolução

  Cenário: Renovação negada quando há atraso ou multa
  Dado o empréstimo do exemplar "DVD Matrix" está em atraso
  E que o sócio "João" possui multa pendente
  Quando o administrador solicitar a renovação do empréstimo
  Então o sistema deve negar a renovação
  E exibir a mensagem "Renovação não permitida" 
