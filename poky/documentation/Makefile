# Minimal makefile for Sphinx documentation
#

# You can set these variables from the command line, and also
# from the environment for the first two.
SPHINXOPTS    ?= -W --keep-going -j auto
SPHINXBUILD   ?= sphinx-build
SOURCEDIR     = .
IMAGEDIRS     = */svg
BUILDDIR      = _build
DESTDIR       = final
SVG2PNG       = inkscape
SVG2PDF       = inkscape

ifeq ($(shell if which $(SPHINXBUILD) >/dev/null 2>&1; then echo 1; else echo 0; fi),0)
$(error "The '$(SPHINXBUILD)' command was not found. Make sure you have Sphinx installed")
endif

# Put it first so that "make" without argument is like "make help".
help:
	@$(SPHINXBUILD) -M help "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

.PHONY: all help Makefile clean publish epub latexpdf

publish: Makefile html singlehtml
	rm -rf $(BUILDDIR)/$(DESTDIR)/
	mkdir -p $(BUILDDIR)/$(DESTDIR)/
	cp -r $(BUILDDIR)/html/* $(BUILDDIR)/$(DESTDIR)/
	cp $(BUILDDIR)/singlehtml/index.html $(BUILDDIR)/$(DESTDIR)/singleindex.html
	sed -i -e 's@index.html#@singleindex.html#@g' $(BUILDDIR)/$(DESTDIR)/singleindex.html

# Build a list of SVG files to convert to PDFs
PDFs := $(foreach dir, $(IMAGEDIRS), $(patsubst %.svg,%.pdf,$(wildcard $(SOURCEDIR)/$(dir)/*.svg)))

# Build a list of SVG files to convert to PNGs
PNGs := $(foreach dir, $(IMAGEDIRS), $(patsubst %.svg,%.png,$(wildcard $(SOURCEDIR)/$(dir)/*.svg)))

# Pattern rule for converting SVG to PDF
%.pdf : %.svg
	$(SVG2PDF) --export-filename=$@ $<

# Pattern rule for converting SVG to PNG
%.png : %.svg
	$(SVG2PNG) --export-filename=$@ $<

clean:
	@rm -rf $(BUILDDIR) $(PNGs) $(PDFs) poky.yaml sphinx-static/switchers.js

epub: $(PNGs)
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

latexpdf: $(PDFs)
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)

all: html epub latexpdf

# Catch-all target: route all unknown targets to Sphinx using the new
# "make mode" option.  $(O) is meant as a shortcut for $(SPHINXOPTS).
%:
	$(SOURCEDIR)/set_versions.py
	@$(SPHINXBUILD) -M $@ "$(SOURCEDIR)" "$(BUILDDIR)" $(SPHINXOPTS) $(O)
