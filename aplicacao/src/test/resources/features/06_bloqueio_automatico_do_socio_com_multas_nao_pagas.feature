# language: pt
Funcionalidade: BLOQUEIO AUTOMÁTICO DO SÓCIO COM MULTAS NÃO PAGAS
  Como administrador
  Quero bloquear automaticamente sócios com multas não pagas
  Para impedir novas locações ou renovações até que as pendências sejam regularizadas

  Cenário: Multa pendente atualiza status para BLOQUEADO
  Dado ocorre uma devolução com atraso gerando multa pendente
  Quando o administrador registra a devolução
  Então o sistema deve atualizar o status do sócio para "BLOQUEADO"
  E impedir novas locações e renovações

  Cenário: Sócio bloqueado tem locação recusada
  Dado que o sócio "João" está "BLOQUEADO" por multa não paga
  E existe um exemplar "CD Beatles" com status "DISPONIVEL"
  Quando o administrador solicita a locação
  Então o sistema deve recusar a operação
  E exibir a mensagem "Operação não permitida para sócio bloqueado" 
