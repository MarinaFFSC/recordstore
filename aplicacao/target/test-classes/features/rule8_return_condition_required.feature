# language: pt
Funcionalidade: Registrar devolução / Atualizar disponibilidade do exemplar

Regra: É obrigatório informar a condição física (bom, regular, danificado)

Cenário: Condição física não informada → devolução recusada
  Dado que há um empréstimo ativo do exemplar "CD - The Dark Side of the Moon"
  Quando o administrador tentar registrar a devolução sem informar a condição física
  Então o sistema deve recusar o registro
  E exibir a mensagem "Condição física é obrigatória na devolução"

Cenário: Condição "danificado" → exemplar indisponível
  Dado que há um empréstimo ativo do exemplar "Vinil - Led Zeppelin IV"
  Quando o administrador registrar a devolução informando a condição "danificado"
  Então o sistema deve atualizar o status do exemplar para "indisponível"
  E impedir novas locações desse exemplar
  E registrar "ExemplarDanificado"
