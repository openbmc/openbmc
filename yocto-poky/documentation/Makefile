# This is a single Makefile to handle all generated Yocto Project documents,
# which includes the BitBake User Manual and the Toaster User Manual.
# The Makefile needs to live in the documents directory and all figures used
# in any manuals must be .PNG files and live in the individual book's figures
# directory as well as in the figures directory for the mega-manual.
#
# Some manuals are available as linked help through the Eclipse development
# system.  These manuals also include an "eclipse" sub-directory as part of
# the make process.
#
# Note that the figures for the Yocto Project Development Manual
# differ depending on the BRANCH being built.
#
# The Makefile has these targets:
#    all:       If you leave off the target then "all" is implied.
#               You will generate HTML, eclipse help (if applicable),
#               and a tarball of files.
#
#    pdf:	generates a PDF version of a manual.  Not valid for the
#		Quick Start or the mega-manual (single, large HTML file
#		comprised of all Yocto Project manuals).
#    html:	generates an HTML version of a manual.
#    eclipse:	generates an HTML version of a manual that can be used as
#		eclipse help (including necessary metadata files).
#    tarball:	creates a tarball for the doc files.
#    validate:	validates
#    publish:	pushes generated files to the Yocto Project website
#    clean:	removes files
#
# The Makefile can generate an HTML and PDF version of every document except the
# Yocto Project Quick Start and the single, HTML mega-manual, which is comprised
# of all the individual Yocto Project manuals.  You can generate these two manuals
# in HTML form only.  The variable DOC indicates the folder name for a given manual.
# The variable VER represents the distro version of the Yocto Release for which the
# manuals are being generated.  The variable BRANCH is used to indicate the
# branch (edison or denzil) and is used only when DOC=dev-manual or
# DOC=mega-manual.  If you do not specify a BRANCH, the default branch used
# will be for the latest Yocto Project release.  If you build for either
# edison or denzil, you must use BRANCH. You do not need to use BRANCH for
# any release beyond denzil.
#
# To build a manual, you must invoke Makefile with the DOC argument.  If you
# are going to publish the manual, then you must invoke Makefile with both the
# DOC and the VER argument.  Furthermore, if you are building or publishing
# the edison or denzil versions of the Yocto Project Development Manual or
# the mega-manual, you must also use the BRANCH argument.
#
# Examples:
#
#     make DOC=bsp-guide
#     make html DOC=yocto-project-qs
#     make pdf DOC=ref-manual
#     make DOC=dev-manual BRANCH=edison
#     make DOC=mega-manual BRANCH=denzil
#
# The first example generates the HTML and Eclipse help versions of the BSP Guide.
# The second example generates the HTML version only of the Quick Start.  Note
# that the Quick Start only has an HTML version available.  So, the
# 'make DOC=yocto-project-qs' command would be equivalent. The third example
# generates just the PDF version of the Yocto Project Reference Manual.
# The fourth example generates the HTML 'edison' version and (if available)
# the Eclipse help version of the YP Development Manual.  The last example
# generates the HTML version of the mega-manual and uses the 'denzil'
# branch when choosing figures for the tarball of figures.  Any example that does
# not use the BRANCH argument builds the current version of the manual set.
#
# The publish target pushes the generated manuals to the Yocto Project
# website.  Unless you are a developer on the YP team, you will not succeed in
# pushing manuals to this server.  All files needed for the manual's HTML form are
# pushed as well as applicable Eclipse versions.
#
# Examples:
#
#    make publish DOC=bsp-guide VER=1.7
#    make publish DOC=adt-manual VER=1.6
#    make publish DOC=dev-manual VER=1.1.1 BRANCH=edison
#    make publish DOC=dev-manual VER=1.2 BRANCH=denzil
#
# The first example publishes the 1.7 version of both the PDF and HTML versions of
# the BSP Guide.  The second example publishes the 1.6 version of both the PDF and
# HTML versions of the ADT Manual. The third example publishes the 1.1.1 version of
# the PDF and HTML YP Development Manual for the 'edison' branch.  The fourth example
# publishes the 1.2 version of the PDF and HTML YP Development Manual for the
# 'denzil' branch.
#

ifeq ($(DOC),bsp-guide)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = bsp-style.css bsp-guide.html figures/bsp-title.png \
           eclipse
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),dev-manual)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
#
# Note that the tarfile might produce the "Cannot stat: No such file or
# directory" error message for .PNG files that are not present when building
# a particular branch.  The list of files is all-inclusive for all branches.
# Note, if you don't provide a BRANCH option, it defaults to the latest stuff.
# This would be appropriate for "master" branch.
#

	ifeq ($(BRANCH),edison)
TARFILES = dev-style.css dev-manual.html \
           figures/app-dev-flow.png figures/bsp-dev-flow.png \
           figures/dev-title.png figures/git-workflow.png \
           figures/index-downloads.png figures/kernel-dev-flow.png \
           figures/kernel-example-repos-edison.png \
           figures/kernel-overview-1.png figures/kernel-overview-2.png \
           figures/kernel-overview-3-edison.png \
           figures/source-repos.png figures/yp-download.png \
           figures/wip.png
	else ifeq ($(BRANCH),denzil)
TARFILES = dev-style.css dev-manual.html \
           figures/app-dev-flow.png figures/bsp-dev-flow.png \
           figures/dev-title.png figures/git-workflow.png \
           figures/index-downloads.png figures/kernel-dev-flow.png \
           figures/kernel-example-repos-denzil.png \
           figures/kernel-overview-1.png figures/kernel-overview-2.png \
           figures/kernel-overview-3-denzil.png \
           figures/source-repos.png figures/yp-download.png \
           figures/wip.png
        else
TARFILES = dev-style.css dev-manual.html \
           figures/bsp-dev-flow.png \
           figures/dev-title.png figures/git-workflow.png \
           figures/index-downloads.png figures/kernel-dev-flow.png \
           figures/kernel-overview-1.png figures/kernel-overview-2-generic.png \
           figures/source-repos.png figures/yp-download.png \
           figures/recipe-workflow.png figures/build-workspace-directory.png \
           figures/devtool-add-flow.png figures/devtool-modify-flow.png \
           figures/devtool-upgrade-flow.png \
           eclipse
	endif

MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),yocto-project-qs)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = yocto-project-qs.html qs-style.css figures/yocto-environment.png \
           figures/yocto-project-transp.png \
           eclipse
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),mega-manual)
XSLTOPTS = --stringparam html.stylesheet mega-style.css \
           --stringparam  chapter.autolabel 1 \
           --stringparam  section.autolabel 1 \
           --stringparam  section.label.includes.component.label 1 \
           --xinclude
ALLPREQ = html tarball

	ifeq ($(BRANCH),edison)
TARFILES = mega-manual.html mega-style.css figures/yocto-environment.png \
        figures/building-an-image.png  \
	figures/using-a-pre-built-image.png \
	figures/poky-title.png \
	figures/adt-title.png figures/bsp-title.png \
	figures/kernel-title.png figures/kernel-architecture-overview.png \
	figures/app-dev-flow.png figures/bsp-dev-flow.png \
        figures/dev-title.png figures/git-workflow.png \
        figures/index-downloads.png figures/kernel-dev-flow.png \
	figures/kernel-example-repos-edison.png \
	figures/kernel-overview-1.png figures/kernel-overview-2.png \
	figures/kernel-overview-3-edison.png \
	figures/source-repos.png figures/yp-download.png \
	figures/wip.png
	else ifeq ($(BRANCH),denzil)
TARFILES = mega-manual.html mega-style.css figures/yocto-environment.png \
        figures/building-an-image.png  \
	figures/using-a-pre-built-image.png \
	figures/poky-title.png \
	figures/adt-title.png figures/bsp-title.png \
	figures/kernel-title.png figures/kernel-architecture-overview.png \
	figures/app-dev-flow.png figures/bsp-dev-flow.png \
        figures/dev-title.png figures/git-workflow.png \
        figures/index-downloads.png figures/kernel-dev-flow.png \
	figures/kernel-example-repos-denzil.png \
	figures/kernel-overview-1.png figures/kernel-overview-2.png \
	figures/kernel-overview-3-denzil.png \
	figures/source-repos.png figures/yp-download.png \
	figures/wip.png
        else
TARFILES = mega-manual.html mega-style.css figures/yocto-environment.png \
        figures/building-an-image.png  \
	figures/using-a-pre-built-image.png \
	figures/poky-title.png figures/buildhistory.png \
        figures/buildhistory-web.png \
	figures/sdk-title.png figures/bsp-title.png \
	figures/kernel-dev-title.png figures/kernel-architecture-overview.png \
	figures/bsp-dev-flow.png \
        figures/dev-title.png \
	figures/git-workflow.png figures/index-downloads.png \
        figures/kernel-dev-flow.png \
	figures/kernel-overview-1.png figures/kernel-overview-2-generic.png \
	figures/source-repos.png figures/yp-download.png \
        figures/profile-title.png figures/kernelshark-all.png \
        figures/kernelshark-choose-events.png \
        figures/kernelshark-i915-display.png \
        figures/kernelshark-output-display.png figures/lttngmain0.png \
        figures/oprofileui-busybox.png figures/oprofileui-copy-to-user.png \
        figures/oprofileui-downloading.png figures/oprofileui-processes.png \
        figures/perf-probe-do_fork-profile.png \
        figures/perf-report-cycles-u.png \
        figures/perf-systemwide.png figures/perf-systemwide-libc.png \
        figures/perf-wget-busybox-annotate-menu.png \
        figures/perf-wget-busybox-annotate-udhcpc.png \
        figures/perf-wget-busybox-debuginfo.png \
        figures/perf-wget-busybox-dso-zoom.png \
        figures/perf-wget-busybox-dso-zoom-menu.png \
        figures/perf-wget-busybox-expanded-stripped.png \
        figures/perf-wget-flat-stripped.png \
        figures/perf-wget-g-copy-from-user-expanded-stripped.png \
        figures/perf-wget-g-copy-to-user-expanded-debuginfo.png \
        figures/perf-wget-g-copy-to-user-expanded-stripped.png \
        figures/perf-wget-g-copy-to-user-expanded-stripped-unresolved-hidden.png \
        figures/pybootchartgui-linux-yocto.png \
        figures/pychart-linux-yocto-rpm.png \
        figures/pychart-linux-yocto-rpm-nostrip.png \
        figures/sched-wakeup-profile.png figures/sysprof-callers.png \
        figures/sysprof-copy-from-user.png figures/sysprof-copy-to-user.png \
        figures/cross-development-toolchains.png \
	figures/yocto-environment-ref.png figures/user-configuration.png \
        figures/source-input.png figures/package-feeds.png \
        figures/layer-input.png figures/images.png figures/sdk.png \
	figures/source-fetching.png figures/patching.png \
        figures/configuration-compile-autoreconf.png \
	figures/analysis-for-package-splitting.png \
        figures/image-generation.png \
	figures/sdk-generation.png figures/recipe-workflow.png \
	figures/build-workspace-directory.png figures/mega-title.png \
	figures/toaster-title.png figures/hosted-service.png \
	figures/simple-configuration.png figures/devtool-add-flow.png \
	figures/devtool-modify-flow.png figures/devtool-upgrade-flow.png \
	figures/compatible-layers.png figures/import-layer.png figures/new-project.png \
	figures/sdk-environment.png figures/sdk-installed-standard-sdk-directory.png \
	figures/sdk-devtool-add-flow.png figures/sdk-installed-extensible-sdk-directory.png \
	figures/sdk-devtool-modify-flow.png figures/sdk-eclipse-dev-flow.png
	endif

MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),ref-manual)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = ref-manual.html ref-style.css figures/poky-title.png \
	figures/buildhistory.png figures/buildhistory-web.png eclipse \
        figures/cross-development-toolchains.png figures/layer-input.png \
	figures/package-feeds.png figures/source-input.png \
	figures/user-configuration.png figures/yocto-environment-ref.png \
	figures/images.png figures/sdk.png figures/source-fetching.png \
	figures/patching.png figures/configuration-compile-autoreconf.png \
	figures/analysis-for-package-splitting.png figures/image-generation.png \
	figures/sdk-generation.png figures/building-an-image.png
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),sdk-manual)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = sdk-manual.html sdk-style.css figures/sdk-title.png \
           figures/sdk-environment.png figures/sdk-installed-standard-sdk-directory.png \
	   figures/sdk-installed-extensible-sdk-directory.png figures/sdk-devtool-add-flow.png \
	   figures/sdk-devtool-modify-flow.png figures/sdk-eclipse-dev-flow.png \
           eclipse
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),profile-manual)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = profile-manual.html profile-manual-style.css \
           figures/profile-title.png figures/kernelshark-all.png \
           figures/kernelshark-choose-events.png \
           figures/kernelshark-i915-display.png \
           figures/kernelshark-output-display.png figures/lttngmain0.png \
           figures/oprofileui-busybox.png figures/oprofileui-copy-to-user.png \
           figures/oprofileui-downloading.png figures/oprofileui-processes.png \
           figures/perf-probe-do_fork-profile.png \
           figures/perf-report-cycles-u.png \
           figures/perf-systemwide.png figures/perf-systemwide-libc.png \
           figures/perf-wget-busybox-annotate-menu.png \
           figures/perf-wget-busybox-annotate-udhcpc.png \
           figures/perf-wget-busybox-debuginfo.png \
           figures/perf-wget-busybox-dso-zoom.png \
           figures/perf-wget-busybox-dso-zoom-menu.png \
           figures/perf-wget-busybox-expanded-stripped.png \
           figures/perf-wget-flat-stripped.png \
           figures/perf-wget-g-copy-from-user-expanded-stripped.png \
           figures/perf-wget-g-copy-to-user-expanded-debuginfo.png \
           figures/perf-wget-g-copy-to-user-expanded-stripped.png \
           figures/perf-wget-g-copy-to-user-expanded-stripped-unresolved-hidden.png \
           figures/pybootchartgui-linux-yocto.png \
           figures/pychart-linux-yocto-rpm.png \
           figures/pychart-linux-yocto-rpm-nostrip.png \
           figures/sched-wakeup-profile.png figures/sysprof-callers.png \
           figures/sysprof-copy-from-user.png figures/sysprof-copy-to-user.png \
           eclipse
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),kernel-dev)
XSLTOPTS = --xinclude
ALLPREQ = html eclipse tarball
TARFILES = kernel-dev.html kernel-dev-style.css \
           figures/kernel-dev-title.png \
           figures/kernel-architecture-overview.png \
           eclipse
MANUALS = $(DOC)/$(DOC).html $(DOC)/eclipse
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),toaster-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = toaster-manual.html toaster-manual-style.css \
	   figures/toaster-title.png figures/simple-configuration.png \
	   figures/hosted-service.png \
	   figures/compatible-layers.png figures/import-layer.png figures/new-project.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif


##
# These URI should be rewritten by your distribution's xml catalog to
# match your locally installed XSL stylesheets.
XSL_BASE_URI  = http://docbook.sourceforge.net/release/xsl/1.76.1
XSL_XHTML_URI = $(XSL_BASE_URI)/xhtml/docbook.xsl

all: $(ALLPREQ)

pdf:
ifeq ($(DOC),yocto-project-qs)
	@echo " "
	@echo "ERROR: You cannot generate a yocto-project-qs PDF file."
	@echo " "

else ifeq ($(DOC),mega-manual)
	@echo " "
	@echo "ERROR: You cannot generate a mega-manual PDF file."
	@echo " "

else

	cd $(DOC); ../tools/poky-docbook-to-pdf $(DOC).xml ../template; cd ..
endif

html:
ifeq ($(DOC),mega-manual)
#       See http://www.sagehill.net/docbookxsl/HtmlOutput.html
	@echo " "
	@echo "******** Building "$(DOC)
	@echo " "
	cd $(DOC); xsltproc $(XSLTOPTS) -o $(DOC).html $(DOC)-customization.xsl $(DOC).xml; cd ..
	@echo " "
	@echo "******** Using mega-manual.sed to process external links"
	@echo " "
	cd $(DOC); sed -f ../tools/mega-manual.sed < mega-manual.html > mega-output.html; cd ..
	@echo " "
	@echo "******** Cleaning up transient file mega-output.html"
	@echo " "
	cd $(DOC); rm mega-manual.html; mv mega-output.html mega-manual.html; cd ..
else
#       See http://www.sagehill.net/docbookxsl/HtmlOutput.html
	@echo " "
	@echo "******** Building "$(DOC)
	@echo " "
	cd $(DOC); xsltproc $(XSLTOPTS) -o $(DOC).html $(DOC)-customization.xsl $(DOC).xml; cd ..
endif


eclipse: BASE_DIR = html/$(DOC)/

eclipse: eclipse-generate eclipse-resolve-links

.PHONY : eclipse-generate eclipse-resolve-links

eclipse-generate:
ifeq ($(filter $(DOC), sdk-manual bsp-guide dev-manual kernel-dev profile-manual ref-manual yocto-project-qs),)
	@echo " "
	@echo "ERROR: You can only create eclipse documentation"
	@echo "       of the following documentation parts:"
	@echo "       - sdk-manual"
	@echo "       - bsp-guide"
	@echo "       - dev-manual"
	@echo "       - kernel-dev"
	@echo "       - profile-manual"
	@echo "       - ref-manual"
	@echo "       - yocto-project-qs"
	@echo " "
else
	@echo " "
	@echo "******** Building eclipse help of "$(DOC)
	@echo " "
	cd $(DOC) && \
		xsltproc $(XSLTOPTS) \
				--stringparam base.dir '$(BASE_DIR)' \
				-o eclipse/$(DOC).html \
				$(DOC)-eclipse-customization.xsl $(DOC).xml && \
		mv eclipse/toc.xml eclipse/$(DOC)-toc.xml && \
		cp -rf $(FIGURES) eclipse/$(BASE_DIR) && \
		cd ..;

	$(call modify-eclipse)
endif

eclipse-resolve-links:
	@echo " "
	@echo "******** Using eclipse-help.sed to process external links"
	@echo " "
	$(foreach FILE, \
		$(wildcard $(DOC)/eclipse/html/$(DOC)/*.html), \
		$(shell sed -i -f tools/eclipse-help.sed $(FILE)))

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
