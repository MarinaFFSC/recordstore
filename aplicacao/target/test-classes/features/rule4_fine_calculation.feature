# language: pt
Funcionalidade: Validação de sócios antes da locação

Regra: Somente sócios ativos podem realizar empréstimos

Cenário: Sócio ativo → locação permitida
  Dado que existe um sócio "Maria" com status "ativo"
  E existe um exemplar "Vinil - The Wall" com status "disponível"
  Quando o administrador solicita a locação
  Então o sistema deve permitir a locação
  E o status do exemplar deve mudar para "emprestado"

Cenário: Sócio bloqueado → locação recusada
  Dado que existe um sócio "João" com status "bloqueado"
  E existe um exemplar "CD - Nevermind" com status "disponível"
  Quando o administrador solicita a locação
  Então o sistema deve recusar a locação
  E exibir a mensagem "Sócio não autorizado para locação"
  E a locação não deve ser criada
