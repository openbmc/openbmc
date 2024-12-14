SUMMARY = "Distributed version control system"
HOMEPAGE = "http://git-scm.com"
DESCRIPTION = "Git is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency."
SECTION = "console/utils"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later & BSD-3-Clause & MIT & BSL-1.0 & LGPL-2.1-or-later"
DEPENDS = "openssl zlib"
DEPENDS:class-native += "ca-certificates"

PROVIDES:append:class-native = " git-replacement-native"

SRC_URI = "${KERNELORG_MIRROR}/software/scm/git/git-${PV}.tar.gz;name=tarball \
           file://fixsort.patch \
           file://0001-config.mak.uname-do-not-force-RHEL-7-specific-build-.patch \
           "

S = "${WORKDIR}/git-${PV}"

LIC_FILES_CHKSUM = "\
	file://COPYING;md5=7c0d7ef03a7eb04ce795b0f60e68e7e1 \
	file://reftable/LICENSE;md5=1a6424cafc4c9c88c689848e165af33b \
	file://sha1dc/LICENSE.txt;md5=9bbe4c990a9e98ea4b98ef5d3bcb8a7a \
	file://compat/nedmalloc/License.txt;md5=e4224ccaecb14d942c71d31bef20d78c \
	file://compat/inet_ntop.c;md5=76593c6f74e8ced5b24520175688d59b;endline=16 \
	file://compat/obstack.h;md5=08ad25fee5428cd879ceef451ce3a22e;endline=18 \
	file://compat/poll/poll.h;md5=9fc00170a53b8e3e52157c91ac688dd1;endline=19 \
	file://compat/regex/regex.h;md5=30cc8af0e6f0f8a25acec6d8783bb763;beginline=4;endline=22 \
"

CVE_PRODUCT = "git-scm:git"

PACKAGECONFIG ??= "expat curl"
PACKAGECONFIG[cvsserver] = ""
PACKAGECONFIG[svn] = ""
PACKAGECONFIG[manpages] = ",,asciidoc-native xmlto-native"
PACKAGECONFIG[curl] = "--with-curl,--without-curl,curl"
PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat"

EXTRA_OECONF = "--with-perl=${STAGING_BINDIR_NATIVE}/perl-native/perl \
		--without-tcltk \
		--without-iconv \
"
EXTRA_OECONF:append:class-nativesdk = " --with-gitconfig=/etc/gitconfig "
EXTRA_OECONF:append:class-native = " --with-gitconfig=/etc/gitconfig "

# Needs brokensep as this doesn't use automake
inherit autotools-brokensep perlnative bash-completion manpages

EXTRA_OEMAKE = "NO_PYTHON=1 CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"
EXTRA_OEMAKE += "'PERL_PATH=/usr/bin/env perl'"
EXTRA_OEMAKE += "COMPUTE_HEADER_DEPENDENCIES=no"
EXTRA_OEMAKE:append:class-native = " NO_CROSS_DIRECTORY_HARDLINKS=1"

do_compile:prepend () {
	# Remove perl/perl.mak to fix the out-of-date perl.mak error
	# during rebuild
	rm -f perl/perl.mak

        if [ "${@bb.utils.filter('PACKAGECONFIG', 'manpages', d)}" ]; then
            oe_runmake man
        fi
}

do_install () {
	oe_runmake install DESTDIR="${D}" bindir=${bindir} \
		template_dir=${datadir}/git-core/templates

	install -d ${D}/${datadir}/bash-completion/completions/
	install -m 644 ${S}/contrib/completion/git-completion.bash ${D}/${datadir}/bash-completion/completions/git

        if [ "${@bb.utils.filter('PACKAGECONFIG', 'manpages', d)}" ]; then
            # Needs to be serial with make 4.4 due to https://savannah.gnu.org/bugs/index.php?63362
            make install-man DESTDIR="${D}"
        fi
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
REL_GIT_SSL_CAINFO = "${@os.path.relpath(sysconfdir, bindir)}/ssl/certs/ca-certificates.crt"

do_install:append:class-target () {
	perl_native_fixup
}

do_install:append:class-native() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_SSL_CAINFO='`dirname $''realpath`'/${REL_GIT_SSL_CAINFO} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
}

do_install:append:class-nativesdk() {
	create_wrapper ${D}${bindir}/git \
		GIT_EXEC_PATH='`dirname $''realpath`'/${REL_GIT_EXEC_PATH} \
		GIT_TEMPLATE_DIR='`dirname $''realpath`'/${REL_GIT_TEMPLATE_DIR}
	perl_native_fixup
}

FILES:${PN} += "${datadir}/git-core ${libexecdir}/git-core/"

PERLTOOLS = " \
    ${bindir}/git-cvsserver \
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
FILES:${PN}-perltools += " \
    ${PERLTOOLS} \
    ${libdir}/perl \
    ${datadir}/perl5 \
"

RDEPENDS:${PN}-perltools = "${PN} perl perl-module-file-path findutils"

# git-tk package with gitk and git-gui
PACKAGES =+ "${PN}-tk"
#RDEPENDS:${PN}-tk = "${PN} tk tcl"
#EXTRA_OEMAKE = "TCL_PATH=${STAGING_BINDIR_CROSS}/tclsh"
FILES:${PN}-tk = " \
    ${bindir}/gitk \
    ${datadir}/gitk \
"

PACKAGES =+ "gitweb"
FILES:gitweb = "${datadir}/gitweb/"
RDEPENDS:gitweb = "perl"

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "f4c4e98667800585d218dfdf415eb72f73baa7abcac4569e2ce497970f8d6665"
