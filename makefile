%.html: %.md cib.csl helga_tauscher.bib
	pandoc -N -s --csl cib.csl --css style.css --bibliography helga_tauscher.bib -o $@ $<

