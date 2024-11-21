This repository was used for the maintenance of the Unicode Standard Annex #42, Unicode Character Database in XML, as well as the code to generate the related data files. It is no longer used. See https://www.unicode.org/Public/UCD/latest/ucdxml/ for the latest data files, and https://www.unicode.org/reports/tr42/ for the latest documentation.

-----

On mac:
 brew install icu4c
 brew install rnv


(cd uax42; ant all)

ant compile

ant download-15.1.0

ant ucd15.1

ant download-16.0.0

ant ucd16

ant validate16

ant diff16


RNV (validateur Relax NG): http://www.davidashen.net/rnv.html
