-- ***************************************************************
-- Descrição: Função para validar o CPF quanto a forma e o tipo.
--
-- Parâmetros de entrada: pCpf VARCHAR2
--
-- Parâmetros de saída: BOOLEAN
--
-- Condições de erro: CPF incompatível com a lei de formação.
--
-- Autor:  Vinícius de Sousa Santana
--
-- Histórico de revisão
-- Data                Autor           Razão para alteração
-- --------------------------------------------------------------
-- 31/01/2018         VSSOUSA             Criação
-- 
-- **************************************************************

create or replace function "F_VALIDAR_CPF" (pCpf IN VARCHAR2) RETURN BOOLEAN
IS

vTotal NUMBER := 0;
vDigito NUMBER := 0;
vCPF NUMBER := 0;

BEGIN

  --Verifica se o tamando da string passada é com patível com o do cpf.

  IF(LENGTH(pCpf) <> 11)THEN

    RETURN FALSE;

  END IF;

  --A conversão para número testa quanto a string ser numérica.

  vCPF := TO_NUMBER(pCpf);

  --Descarta os números usualmente usados.
  
  IF (pCpf IN ('00000000000',
               '11111111111',
               '22222222222',
               '33333333333',
               '44444444444',
               '55555555555',
               '66666666666',
               '77777777777',
               '88888888888',
               '99999999999')) THEN
			    
    RETURN FALSE;
	
  END IF;
  
  --Multiplica cada dígito de 1 a 9 do CPF passado de acordo com seu peso correspondente (10 a 2) e soma os resultados.
  
  FOR i IN 1 .. 9 LOOP
  
    vTotal := vTotal + SUBSTR (pCpf, i, 1) * (11 - i);
 
  END LOOP;
  
  --Determina o primeiro dígito de verificação. Se o dígito é maior que 9 então torna-se 0, caso contrário permanece o mesmo.
    
  vDigito := 11 - MOD (vTotal, 11);
  
  IF vDigito > 9 THEN
 
    vDigito := 0;
 
  END IF;
  
  --Avalia se o primeiro dígito verificador corresponde ao décimo dígito do CPF.
 
  IF vDigito != SUBSTR (pCpf, 10, 1) THEN
 
    RETURN FALSE;
 
  END IF;
 
  vDigito := 0;
  vTotal := 0;
  
  --Faz o mesmo processo para conferir se o segundo dígito verificador corresponde ao décimo primeiro dígito do CPF.
 
  FOR i IN 1 .. 10 LOOP
 
    vTotal := vTotal + SUBSTR (pCpf, i, 1) * (12 - i);
 
  END LOOP;
 
    vDigito := 11 - MOD (vTotal, 11);
  
  IF vDigito > 9 THEN
 
    vDigito := 0;
 
  END IF;
 
  IF vDigito != SUBSTR (pCpf, 11, 1) THEN
 
    RETURN FALSE;
 
  END IF;
 
  RETURN TRUE;
  
  EXCEPTION WHEN OTHERS THEN
  
    RETURN FALSE;

END "F_VALIDAR_CPF";
