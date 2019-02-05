create or replace function "F_RETURN_RANDOM_CPF" RETURN VARCHAR2
IS

vCpf VARCHAR2(11);
mTotal NUMBER := 0;
mDigit NUMBER := 0;

BEGIN

  SELECT TO_CHAR(TRUNC(dbms_random.VALUE(10000000000, 99999999999))) AS CPF
    INTO vCpf
    FROM DUAL;

  --Multiplies each digit from 1 to 9 of the given CPF number according to its correspondent weight (10 to 2) and sums the results.
  
  FOR i IN 1 .. 9 LOOP
  
    mTotal := mTotal + SUBSTR (vCpf, i, 1) * (11 - i);
 
  END LOOP;
  
  --Determines the first verification digit. If the digit is greater than 9 it becomes 0 else it stays the same.
    
  mDigit := 11 - MOD (mTotal, 11);
  
  IF mDigit > 9 THEN
 
    mDigit := 0;
 
  END IF;
  
  --Determines the 10th digit.
 
  vCpf := substr(vCpf,1,9) || mDigit || substr(vCpf,11,1);
 
  mDigit := 0;
  mTotal := 0;
  
  --Do the same process to check the second verification digit againt the 11th CPF number.
 
  FOR i IN 1 .. 10 LOOP
 
    mTotal := mTotal + SUBSTR (vCpf, i, 1) * (12 - i);
 
  END LOOP;
 
    mDigit := 11 - MOD (mTotal, 11);
  
  IF mDigit > 9 THEN
 
    mDigit := 0;
 
  END IF;
 
  vCpf := substr(vCpf,1,10) || mDigit;
 
  RETURN vCpf;
  
END "F_RETURN_RANDOM_CPF";
