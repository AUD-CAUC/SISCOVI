/*    ==Parâmetros de Script==
    Versão do Servidor de Origem : SQL Server 2016 (13.0.4001)
    Edição do Mecanismo de Banco de Dados de Origem : Microsoft SQL Server Enterprise Edition
    Tipo do Mecanismo de Banco de Dados de Origem : SQL Server Autônomo
    Versão do Servidor de Destino : SQL Server 2016
    Edição de Mecanismo de Banco de Dados de Destino : Microsoft SQL Server Enterprise Edition
    Tipo de Mecanismo de Banco de Dados de Destino : SQL Server Autônomo
*/

USE [SISCOVI]
GO

/****** Object:  UserDefinedFunction [dbo].[F_RETURN_RANDOM_CPF]    Script Date: 07/09/2017 21:11:24 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-----------------------------------------------------------------------------------

create function [dbo].[F_RETURN_RANDOM_CPF](@pRandomNumber BIGINT) RETURNS VARCHAR(14)
WITH EXECUTE AS CALLER

AS

BEGIN

  DECLARE @vCpf VARCHAR(14);
  DECLARE @mTotal INT = 0;
  DECLARE @mDigit INT = 0;
  DECLARE @vCount INT = 1;

  SET @vCpf = CONVERT(VARCHAR,@pRandomNumber);

  --Multiplies each digit from 1 to 9 of the given CPF number according to its correspondent weight (10 to 2) and sums the results.
  
  WHILE @vCount <= 9

    BEGIN
  
      SET @mTotal = @mTotal + SUBSTRING (@vCpf, @vCount, 1) * (11 - @vCount);
    
	  SET @vCount = @vCount + 1;

    END;

  --Determines the first verification digit. If the digit is greater than 9 it becomes 0 else it stays the same.
    
  SET @mDigit = 11 - (@mTotal % 11);
  
  IF @mDigit > 9
 
   BEGIN

     SET @mDigit = 0;
 
   END;
  
  --Determines the 10th digit.
 
  SET @vCpf = SUBSTRING(@vCpf,1,9) + CONVERT(VARCHAR,@mDigit) + SUBSTRING(@vCpf,11,1);
 
  SET @mDigit = 0;
  SET @mTotal = 0;
  SET @vCount = 1;
  
  --Do the same process to check the second verification digit againt the 11th CPF number.
 
  WHILE @vCount <= 10

    BEGIN
 
      SET @mTotal = @mTotal + SUBSTRING(@vCpf, @vCount, 1) * (12 - @vCount);

	  SET @vCount = @vCount + 1;
 
    END;
 
  SET @mDigit = 11 - (@mTotal % 11);
  
  IF @mDigit > 9
 
    BEGIN

      SET @mDigit = 0;
 
    END;
 
  SET @vCpf = SUBSTRING(@vCpf,1,10) + CONVERT(VARCHAR,@mDigit);

  RETURN @vCpf;

END;

------------------------------------------------------------------------------------------------
GO