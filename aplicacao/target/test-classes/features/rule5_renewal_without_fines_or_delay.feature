# language: pt
Funcionalidade: Renovar empréstimo

Regra: Só renova se não houver multa pendente nem atraso

Cenário: Renovação permitida (sem multa e sem atraso)
  Dado que o sócio "Ana" não possui multas pendentes
  E o empréstimo do exemplar "CD - Parachutes" está dentro do prazo
  Quando o administrador solicitar a renovação do empréstimo
  Então o sistema deve permitir a renovação
  E recalcular a nova data prevista de devolução
  E registrar o evento "RenovaçãoConcedida"

Cenário: Renovação negada por multa pendente ou atraso
  Dado que o sócio "Bruno" possui multa pendente
  E o empréstimo do exemplar "Vinil - A Love Supreme" está em atraso
  Quando o administrador solicitar a renovação do empréstimo
  Então o sistema deve negar a renovação
  E exibir a mensagem "Renovação não permitida: multa pendente ou empréstimo em atraso"
  E registrar o evento "RenovaçãoNegada"
