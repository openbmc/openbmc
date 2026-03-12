# Minimal makefile for Sphinx documentation
#

# You can set these variables from the command line, and also
# from the environment for the first two.
SPHINXOPTS     ?= -W --keep-going -j auto
SPHINXBUILD    ?= python3 -m sphinx
# Release notes are excluded because they contain contributor names and commit messages which can't be modified
VALEOPTS       ?= --no-wrap --glob '!migration-guides/release-notes-*.rst'
SOURCEDIR      = .
VALEDOCS       ?= $(SOURCEDIR)
SPHINXLINTDOCS ?= $(SOURCEDIR)
BUILDDIR       = _build
DESTDIR        = final

ifeq ($(shell if $(SPHINXBUILD) --version >/dev/null 2>&1; then echo 1; else echo 0; fi),0)
$(error "Sphinx was not found. Make sure you have it installed")
endif

# Put it first so that "make" without argument is like "make help".
help:
	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

.PHONY: all checks help Makefile clean stylecheck publish latexpdf

publish: Makefile checks epub latexpdf html singlehtml
	rm -rf $(BUILDDIR)/$(DESTDIR)/
	mkdir -p $(BUILDDIR)/$(DESTDIR)/
	cp -r $(BUILDDIR)/html/* $(BUILDDIR)/$(DESTDIR)/
	mkdir -p $(BUILDDIR)/$(DESTDIR)/_static
	cp $(BUILDDIR)/epub/TheYoctoProject.epub $(BUILDDIR)/latex/theyoctoproject.pdf $(BUILDDIR)/$(DESTDIR)/_static/
	cp $(BUILDDIR)/singlehtml/index.html $(BUILDDIR)/$(DESTDIR)/singleindex.html
	sed -i -e 's@index.html#@singleindex.html#@g' $(BUILDDIR)/$(DESTDIR)/singleindex.html

clean:
	@rm -rf $(BUILDDIR) poky.yaml sphinx-static/switchers.js releases.rst

checks:
	$(SOURCEDIR)/tools/check-glossaries --docs-dir $(SOURCEDIR)

stylecheck:
	vale sync
	vale $(VALEOPTS) $(VALEDOCS)

sphinx-lint:
	sphinx-lint $(SPHINXLINTDOCS)

# Note: we need to pass buf_size here (which is also configurable from
# texmf.cnf), to avoid following error:
#   Unable to read an entire line---bufsize=200000. Please increase buf_size in texmf.cnf.
latexpdf:
	$(SOURCEDIR)/set_versions.py
	buf_size=10000000 LATEXMKOPTS="-silent" $(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

all: html epub latexpdf

# Catch-all target: route all unknown targets to Sphinx using the new
# "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
%:
	$(SOURCEDIR)/set_versions.py
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
