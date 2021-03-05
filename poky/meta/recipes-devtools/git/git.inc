SUMMARY = "Distributed version control system"
HOMEPAGE = "http://git-scm.com"
DESCRIPTION = "Git is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency."
SECTION = "console/utils"
LICENSE = "GPLv2"
DEPENDS = "openssl curl zlib expat"

PROVIDES_append_class-native = " git-replacement-native"

SRC_URI = "${KERNELORG_MIRROR}/software/scm/git/git-${PV}.tar.gz;name=tarball \
           ${KERNELORG_MIRROR}/software/scm/git/git-manpages-${PV}.tar.gz;name=manpages \
           file://fixsort.patch"

S = "${WORKDIR}/git-${PV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=7c0d7ef03a7eb04ce795b0f60e68e7e1"

CVE_PRODUCT = "git-scm:git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cvsserver] = ""
PACKAGECONFIG[svn] = ""

EXTRA_OECONF = "--with-perl=${STAGING_BINDIR_NATIVE}/perl-native/perl \
		--without-tcltk \
		--without-iconv \
"
EXTRA_OECONF_append_class-nativesdk = " --with-gitconfig=/etc/gitconfig "

# Needs brokensep as this doesn't use automake
inherit autotools-brokensep perlnative bash-completion

EXTRA_OEMAKE = "NO_PYTHON=1 CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"
EXTRA_OEMAKE += "'PERL_PATH=/usr/bin/env perl'"
EXTRA_OEMAKE_append_class-native = " NO_CROSS_DIRECTORY_HARDLINKS=1"

do_compile_prepend () {
	# Remove perl/perl.mak to fix the out-of-date perl.mak error
	# during rebuild
	rm -f perl/perl.mak
}

do_install () {
	oe_runmake install DESTDIR="${D}" bindir=${bindir} \
		template_dir=${datadir}/git-core/templates

	for section in man1 man5 man7; do
		install -d ${D}/${mandir}/$section
		install -t ${D}/${mandir}/$section ${WORKDIR}/$section/*
	done

	install -d ${D}/${datadir}/bash-completion/completions/
	install -m 644 ${S}/contrib/completion/git-completion.bash ${D}/${datadir}/bash-completion/completions/git
}

perl_native_fixup () {
	sed -i -e 's#${STAGING_BINDIR_NATIVE}/perl-native/#${bindir}/#' \
	       -e 's#${libdir}/perl-native/#${libdir}/#' \
	    ${@d.getVar("PERLTOOLS").replace(' /',d.getVar('D') + '/')}

	if [ ! "${@bb.utils.filter('PACKAGECONFIG', 'cvsserver', d)}" ]; then
		# Only install the git cvsserver command if explicitly requested
		# as it requires the DBI Perl module, which does not exist in
		# OE-Core.
		rm ${D}${libexecdir}/git-core/git-cvsserver \
		   ${D}${bindir}/git-cvsserver
	fi

	if [ ! "${@bb.utils.filter('PACKAGECONFIG', 'svn', d)}" ]; then
		# Only install the git svn command and all Git::SVN Perl modules
		# if explicitly requested as they require the SVN::Core Perl
		# module, which does not exist in OE-Core.
		rm -r ${D}${libexecdir}/git-core/git-svn \
		      ${D}${datadir}/perl5/Git/SVN*
	fi
}

REL_GIT_EXEC_PATH = "${@os.path.relpath(libexecdir, bindir)}/git-core"
REL_GIT_TEMPLATE_DIR = "${@os.path.relpath(datadir, bindir)}/git-core/templates"

do_install_append_class-target () {
	perl_native_fixup
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
	perl_native_fixup
}

FILES_${PN} += "${datadir}/git-core ${libexecdir}/git-core/"

PERLTOOLS = " \
    ${bindir}/git-cvsserver \
    ${libexecdir}/git-core/git-add--interactive \
    ${libexecdir}/git-core/git-archimport \
    ${libexecdir}/git-core/git-cvsexportcommit \
    ${libexecdir}/git-core/git-cvsimport \
    ${libexecdir}/git-core/git-cvsserver \
    ${libexecdir}/git-core/git-send-email \
    ${libexecdir}/git-core/git-svn \
    ${libexecdir}/git-core/git-instaweb \
    ${datadir}/gitweb/gitweb.cgi \
    ${datadir}/git-core/templates/hooks/prepare-commit-msg.sample \
    ${datadir}/git-core/templates/hooks/pre-rebase.sample \
    ${datadir}/git-core/templates/hooks/fsmonitor-watchman.sample \
"

# Git tools requiring perl
PACKAGES =+ "${PN}-perltools"
FILES_${PN}-perltools += " \
    ${PERLTOOLS} \
    ${libdir}/perl \
    ${datadir}/perl5 \
"

RDEPENDS_${PN}-perltools = "${PN} perl perl-module-file-path findutils"

# git-tk package with gitk and git-gui
PACKAGES =+ "${PN}-tk"
#RDEPENDS_${PN}-tk = "${PN} tk tcl"
#EXTRA_OEMAKE = "TCL_PATH=${STAGING_BINDIR_CROSS}/tclsh"
FILES_${PN}-tk = " \
    ${bindir}/gitk \
    ${datadir}/gitk \
"

PACKAGES =+ "gitweb"
FILES_gitweb = "${datadir}/gitweb/"
RDEPENDS_gitweb = "perl"

BBCLASSEXTEND = "native nativesdk"
