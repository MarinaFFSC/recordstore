# language: pt
Funcionalidade: Bloqueio automático de sócio com multas não pagas

Regra: Sócios com multas pendentes ficam automaticamente bloqueados até regularizarem a situação

Cenário: Bloquear após registrar multa por atraso
  Dado que o sócio "Carla" está com status "ativo" e sem multas
  E ocorre uma devolução com atraso gerando multa pendente
  Quando o sistema registrar a multa no empréstimo
  Então o sistema deve atualizar o status do sócio para "bloqueado"
  E impedir novas locações e renovações
  E registrar o evento "SocioBloqueadoPorInadimplência"

Cenário: Operação negada a inadimplente com mensagem clara
  Dado que o sócio "Diego" está "bloqueado" por multa não paga
  E existe um exemplar "Vinil - Kind of Blue" com status "disponível"
  Quando o administrador solicita o aluguel do exemplar
  Então o sistema deve recusar a operação
  E exibir a mensagem "Operação negada: pendência de multa"
  E a locação não deve ser criada
