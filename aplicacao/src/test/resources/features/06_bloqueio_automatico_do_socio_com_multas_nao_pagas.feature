# 6) BLOQUEIO AUTOMÁTICO DO SÓCIO COM MULTAS NÃO PAGAS
# language: pt
Funcionalidade: BLOQUEIO AUTOMÁTICO DO SÓCIO COM MULTAS NÃO PAGAS
  Como administrador
  Quero bloquear automaticamente sócios com multas não pagas
  Para impedir novas locações ou renovações até que as pendências sejam regularizadas

  Cenário: Multa pendente atualiza status para BLOQUEADO
  Dado há um sócio ativo sem multas registradas
  E existe um empréstimo com devolução em atraso que gera multa pendente
  Quando o sistema processa a multa do empréstimo para avaliação de bloqueio
  Então o status do sócio deve ser ajustado para "BLOQUEADO"
  E novas locações e renovações devem ser impedidas

  Cenário: Sócio bloqueado tem locação recusada
  Dado que o cadastro do sócio "João" encontra-se "BLOQUEADO" por multa não paga
  E existe um exemplar "CD Beatles" no acervo com status "DISPONIVEL"
  Quando o administrador tenta iniciar uma locação para esse sócio
  Então a operação deve ser negada por bloqueio do sócio
  E deve ser exibida a mensagem "Operação não permitida para sócio bloqueado"
