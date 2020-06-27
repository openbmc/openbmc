#
# SPDX-License-Identifier: CC-BY-2.0-UK
#
# This is a single Makefile to handle all generated Yocto Project documents,
# which includes the BitBake User Manual and the Toaster User Manual.
# The Makefile needs to live in the documents directory and all figures used
# in any manuals must be .PNG files and live in the individual book's figures
# directory as well as in the figures directory for the mega-manual.
#
# Note that the figures for the Yocto Project Development Tasks Manual
# differ depending on the BRANCH being built.
#
# The Makefile has these targets:
#    all:       If you leave off the target then "all" is implied.
#               You will generate HTML and a tarball of files.
#
#    pdf:	generates a PDF version of a manual.  Not valid for the
#		Quick Start or the mega-manual (single, large HTML file
#		comprised of all Yocto Project manuals).
#    html:	generates an HTML version of a manual.
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
# the edison or denzil versions of the Yocto Project Development Tasks Manual or
# the mega-manual, you must also use the BRANCH argument.
#
# Examples:
#
#     make DOC=bsp-guide
#     make html DOC=brief-yoctoprojectqs
#     make pdf DOC=ref-manual
#     make DOC=dev-manual BRANCH=edison
#     make DOC=mega-manual BRANCH=denzil
#
# The first example generates the HTML version of the BSP Guide.
# The second example generates the HTML version only of the Quick Start.  Note
# that the Quick Start only has an HTML version available.  So, the
# 'make DOC=brief-yoctoprojectqs' command would be equivalent. The third example
# generates just the PDF version of the Yocto Project Reference Manual.
# The fourth example generates the HTML 'edison' version of the YP Development
# Tasks Manual.  The last example
# generates the HTML version of the mega-manual and uses the 'denzil'
# branch when choosing figures for the tarball of figures.  Any example that does
# not use the BRANCH argument builds the current version of the manual set.
#
# The publish target pushes the generated manuals to the Yocto Project
# website.  Unless you are a developer on the YP team, you will not succeed in
# pushing manuals to this server.  All files needed for the manual's HTML form are
# pushed.
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
# the PDF and HTML YP Development Tasks Manual for the 'edison' branch.  The fourth
# example publishes the 1.2 version of the PDF and HTML YP Development Tasks Manual
# for the 'denzil' branch.
#
# IN MEMORIAM: This comment is to remember Scott Rifenbark (scottrif), whom we lost
# in January, 2020. Scott was the primary technical writer for the Yocto Project for
# over 9 years. In that time, he contributed many thousands of patches, built this
# documentation tree, and enabled tens of thousands of developers to succeed with
# embedded Linux. He ran this Makefile many thousands of times. Godspeed, Dude.

ifeq ($(DOC),brief-yoctoprojectqs)
XSLTOPTS = --stringparam html.stylesheet brief-yoctoprojectqs-style.css \
           --stringparam  chapter.autolabel 0 \
           --stringparam  section.autolabel 0 \
           --stringparam  section.label.includes.component.label 0 \
           --xinclude
ALLPREQ = html tarball
TARFILES = brief-yoctoprojectqs-style.css brief-yoctoprojectqs.html figures/bypqs-title.png \
           figures/yocto-project-transp.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),overview-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = overview-manual-style.css overview-manual.html figures/overview-manual-title.png \
           figures/git-workflow.png figures/source-repos.png figures/index-downloads.png \
           figures/yp-download.png figures/YP-flow-diagram.png figures/key-dev-elements.png \
           figures/poky-reference-distribution.png figures/cross-development-toolchains.png \
           figures/user-configuration.png figures/layer-input.png figures/source-input.png \
           figures/package-feeds.png figures/patching.png figures/source-fetching.png \
           figures/configuration-compile-autoreconf.png figures/analysis-for-package-splitting.png \
           figures/image-generation.png figures/sdk-generation.png figures/images.png \
           figures/sdk.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),bsp-guide)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = bsp-style.css bsp-guide.html figures/bsp-title.png \
           figures/bsp-dev-flow.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),dev-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
#
# Note that the tarfile might produce the "Cannot stat: No such file or
# directory" error message for .PNG files that are not present when building
# a particular branch.  The list of files is all-inclusive for all branches.
# Note, if you don't provide a BRANCH option, it defaults to the latest stuff.
# This would be appropriate for "master" branch.
#

TARFILES = dev-style.css dev-manual.html figures/buildhistory-web.png \
           figures/dev-title.png figures/buildhistory.png \
           figures/recipe-workflow.png figures/bitbake-build-flow.png \
           figures/multiconfig_files.png figures/cute-files-npm-example.png

MANUALS = $(DOC)/$(DOC).html
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

TARFILES = mega-manual.html mega-style.css \
        figures/YP-flow-diagram.png \
	figures/using-a-pre-built-image.png \
	figures/poky-title.png figures/buildhistory.png \
        figures/buildhistory-web.png \
	figures/sdk-title.png figures/bsp-title.png \
	figures/kernel-dev-title.png figures/kernel-architecture-overview.png \
	figures/bsp-dev-flow.png \
        figures/dev-title.png \
	figures/git-workflow.png figures/index-downloads.png \
        figures/kernel-dev-flow.png \
	figures/kernel-overview-2-generic.png \
	figures/source-repos.png figures/yp-download.png \
        figures/profile-title.png figures/kernelshark-all.png \
        figures/kernelshark-choose-events.png \
        figures/kernelshark-i915-display.png \
        figures/kernelshark-output-display.png \
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
	figures/user-configuration.png \
        figures/source-input.png figures/package-feeds.png \
        figures/layer-input.png figures/images.png figures/sdk.png \
	figures/source-fetching.png figures/patching.png \
        figures/configuration-compile-autoreconf.png \
	figures/analysis-for-package-splitting.png \
        figures/image-generation.png figures/key-dev-elements.png\
	figures/sdk-generation.png figures/recipe-workflow.png \
	figures/build-workspace-directory.png figures/mega-title.png \
	figures/toaster-title.png figures/hosted-service.png figures/multiconfig_files.png \
	figures/simple-configuration.png figures/poky-reference-distribution.png \
	figures/compatible-layers.png figures/import-layer.png figures/new-project.png \
	figures/sdk-environment.png figures/sdk-installed-standard-sdk-directory.png \
	figures/sdk-devtool-add-flow.png figures/sdk-installed-extensible-sdk-directory.png \
	figures/sdk-devtool-modify-flow.png \
	figures/sdk-devtool-upgrade-flow.png figures/bitbake-build-flow.png figures/bypqs-title.png \
	figures/overview-manual-title.png figures/sdk-autotools-flow.png figures/sdk-makefile-flow.png \
	figures/bb_multiconfig_files.png figures/bitbake-title.png figures/cute-files-npm-example.png

MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css

endif

ifeq ($(DOC),ref-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = ref-manual.html ref-style.css figures/poky-title.png \
	figures/build-workspace-directory.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),sdk-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = sdk-manual.html sdk-style.css figures/sdk-title.png \
           figures/sdk-environment.png figures/sdk-installed-standard-sdk-directory.png \
	   figures/sdk-installed-extensible-sdk-directory.png figures/sdk-devtool-add-flow.png \
	   figures/sdk-devtool-modify-flow.png \
	   figures/sdk-devtool-upgrade-flow.png figures/sdk-autotools-flow.png figures/sdk-makefile-flow.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),profile-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = profile-manual.html profile-manual-style.css \
           figures/profile-title.png figures/kernelshark-all.png \
           figures/kernelshark-choose-events.png \
           figures/kernelshark-i915-display.png \
           figures/kernelshark-output-display.png \
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
           figures/sysprof-copy-from-user.png figures/sysprof-copy-to-user.png
MANUALS = $(DOC)/$(DOC).html
FIGURES = figures
STYLESHEET = $(DOC)/*.css
endif

ifeq ($(DOC),kernel-dev)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = kernel-dev.html kernel-dev-style.css \
           figures/kernel-dev-title.png figures/kernel-overview-2-generic.png \
           figures/kernel-architecture-overview.png figures/kernel-dev-flow.png
MANUALS = $(DOC)/$(DOC).html
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


ifeq ($(DOC),test-manual)
XSLTOPTS = --xinclude
ALLPREQ = html tarball
TARFILES = test-manual.html test-manual-style.css \
	   figures/test-manual-title.png figures/ab-test-cluster.png
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
ifeq ($(DOC),brief-yoctoprojectqs)
	@echo " "
	@echo "ERROR: You cannot generate a PDF file for brief-yoctoprojectqs."
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
            scp -r $(MANUALS) $(STYLESHEET) www.yoctoproject.org:/var/www/www.yoctoproject.org-docs/$(VER)/$(DOC); \
            cd $(DOC); scp -r $(FIGURES) www.yoctoproject.org:/var/www/www.yoctoproject.org-docs/$(VER)/$(DOC); \
	else \
          echo " "; \
          echo $(DOC)".html missing.  Generate the file first then try again."; \
          echo " "; \
	fi

clean:
	rm -rf $(MANUALS); rm $(DOC)/$(DOC).tgz;
