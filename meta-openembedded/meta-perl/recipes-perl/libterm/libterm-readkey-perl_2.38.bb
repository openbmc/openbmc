SUMMARY = "Term::ReadKey - A perl module for simple terminal control."
DESCRIPTION = "Term::ReadKey is a compiled perl module dedicated to providing simple \
control over terminal driver modes (cbreak, raw, cooked, etc.,) support \
for non-blocking reads, if the architecture allows, and some generalized \
handy functions for working with terminals. One of the main goals is to \
have the functions as portable as possible, so you can just plug in 'use \
Term::ReadKey' on any architecture and have a good likelihood of it \
working."
HOMEPAGE = "http://search.cpan.org/~jstowe/TermReadKey-${PV}"
SECTION = "libraries"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=c275db663c8489a5709ebb22b185add5"

SRC_URI = "${CPAN_MIRROR}/authors/id/J/JS/JSTOWE/TermReadKey-${PV}.tar.gz"

SRC_URI[md5sum] = "b2b4aab7a0e6bddb7ac3b21ba637482c"
SRC_URI[sha256sum] = "5a645878dc570ac33661581fbb090ff24ebce17d43ea53fd22e105a856a47290"

S = "${WORKDIR}/TermReadKey-${PV}"

UPSTREAM_CHECK_URI = "https://metacpan.org/release/TermReadKey"
UPSTREAM_CHECK_REGEX = "TermReadKey\-(?P<pver>(\d+\.\d+))(?!_\d+)\.tar.gz"

# It needs depend on native to let dynamic loader use native modules
# rather than target ones.
DEPENDS = "libterm-readkey-perl-native"

inherit cpan ptest-perl

RDEPENDS:${PN}-ptest += " \
    perl-module-test-more \
"

do_configure:append () {
    # Hack the dynamic module loader so that it use native modules since it can't load
    # the target ones.
    if [ "${BUILD_SYS}" != "${HOST_SYS}" ]; then
        sed -i -e "s#-I\$(INST_ARCHLIB)#-I${STAGING_BINDIR_NATIVE}/perl-native/perl/vendor_perl/${@get_perl_version(d)}#g" Makefile
    fi
}

BBCLASSEXTEND = "native"
