# language: pt
Funcionalidade: Cálculo de multas por atraso

Regra: multa = dias de atraso × valor diário

Cenário: Devolução dentro do prazo → sem multa
  Dado que a data prevista de devolução é "10/10/2025"
  E a data real de devolução é "10/10/2025"
  Quando o administrador registra a devolução
  Então o sistema não deve calcular multa
  E o valor da multa deve ser "0"

Cenário: Devolução com atraso de 3 dias → multa aplicada
  Dado que a data prevista de devolução é "10/10/2025"
  E a data real de devolução é "13/10/2025"
  E o valor da multa diária é "R$ 2,00"
  Quando o administrador registra a devolução
  Então o sistema deve calcular multa de "R$ 6,00"
  E registrar esse valor no empréstimo
