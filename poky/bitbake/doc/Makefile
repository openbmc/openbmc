# This is a single Makefile to handle all generated BitBake documents.
# The Makefile needs to live in the documentation directory and all figures used
# in any manuals must be .PNG files and live in the individual book's figures
# directory.
#
# The Makefile has these targets:
#
#    pdf:      generates a PDF version of a manual.
#    html:     generates an HTML version of a manual.
#    tarball:  creates a tarball for the doc files.
#    validate: validates
#    clean:    removes files
#
# The Makefile generates an HTML version of every document.  The
# variable DOC indicates the folder name for a given manual.
#
# To build a manual, you must invoke 'make' with the DOC argument.
#
# Examples:
#
#     make DOC=bitbake-user-manual
#     make pdf DOC=bitbake-user-manual
#
# The first example generates the HTML version of the User Manual.
# The second example generates the PDF version of the User Manual.
#

ifeq ($(DOC),bitbake-user-manual)
XSLTOPTS = --stringparam html.stylesheet bitbake-user-manual-style.css \
           --stringparam  chapter.autolabel 1 \
           --stringparam  section.autolabel 1 \
           --stringparam  section.label.includes.component.label 1 \
           --xinclude
ALLPREQ = html tarball
TARFILES = bitbake-user-manual-style.css bitbake-user-manual.html figures/bitbake-title.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

##
# These URI should be rewritten by your distribution's xml catalog to
# match your localy installed XSL stylesheets.
XSL_BASE_URI  = http://docbook.sourceforge.net/release/xsl/current
XSL_XHTML_URI = $(XSL_BASE_URI)/xhtml/docbook.xsl

all: $(ALLPREQ)

pdf:
ifeq ($(DOC),bitbake-user-manual)
	@echo " "
	@echo "********** Building."$(DOC)
	@echo " "
	cd $(DOC); ../tools/docbook-to-pdf $(DOC).xml ../template; cd ..
endif

html:
ifeq ($(DOC),bitbake-user-manual)
#       See http://www.sagehill.net/docbookxsl/HtmlOutput.html
	@echo " "
	@echo "******** Building "$(DOC)
	@echo " "
	cd $(DOC); xsltproc $(XSLTOPTS) -o $(DOC).html $(DOC)-customization.xsl $(DOC).xml; cd ..
endif

tarball: html
	@echo " "
	@echo "******** Creating Tarball of document files"
	@echo " "
	cd $(DOC); tar -cvzf $(DOC).tgz $(TARFILES); cd ..

validate:
	cd $(DOC); xmllint --postvalid --xinclude --noout $(DOC).xml; cd ..

publish:
	@if test -f $(DOC)/$(DOC).html; \
	  then \
            echo " "; \
            echo "******** Publishing "$(DOC)".html"; \
            echo " "; \
            scp -r $(MANUALS) $(STYLESHEET) docs.yp:/var/www/www.yoctoproject.org-docs/$(VER)/$(DOC); \
            cd $(DOC); scp -r $(FIGURES) docs.yp:/var/www/www.yoctoproject.org-docs/$(VER)/$(DOC); \
	else \
          echo " "; \
          echo $(DOC)".html missing.  Generate the file first then try again."; \
          echo " "; \
	fi

clean:
	rm -rf $(MANUALS); rm $(DOC)/$(DOC).tgz;
