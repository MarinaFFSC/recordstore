# 4) VALIDAÇÃO DE SÓCIO ANTES DA LOCAÇÃO
# language: pt
Funcionalidade: VALIDAÇÃO DE SÓCIO ANTES DA LOCAÇÃO
  Como administrador
  Quero validar o status e pendências do sócio antes de autorizar locações
  Para evitar locações indevidas e manter o controle sobre o cadastro de sócios

  Cenário: Sócio ativo e sem multas pode alugar
  Dado que o cadastro do sócio "João" está "ATIVO" e sem multas registradas
  E há no acervo um exemplar "CD Beatles" marcado como "DISPONIVEL"
  Quando o administrador envia a solicitação de locação
  Então a solicitação de locação deve ser aprovada

  Cenário: Sócio bloqueado por multa não pode alugar
  Dado que o cadastro do sócio "João" está "BLOQUEADO" por multa não paga
  E há no acervo um exemplar "CD Beatles" marcado como "DISPONIVEL"
  Quando o administrador envia a solicitação de locação
  Então a solicitação de locação deve ser impedida
  E a mensagem apresentada ao administrador deve ser "Sócio bloqueado por multa"
