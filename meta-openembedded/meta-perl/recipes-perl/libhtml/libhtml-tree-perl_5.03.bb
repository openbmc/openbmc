SUMMARY = "HTML::TreeBuilder - Parser that builds a HTML syntax tree"
DESCRIPTION = "This distribution contains a suite of modules for representing, \
creating, and extracting information from HTML syntax trees; there is \
also relevent documentation.  These modules used to be part of the \
libwww-perl distribution, but are now unbundled in order to facilitate \
a separate development track."
SECTION = "libs"

HOMEPAGE = "https://metacpan.org/release/CJM/HTML-Tree-5.03/source/README"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb57a8958cae73cb65e7d0c26339242"

SRC_URI = "${CPAN_MIRROR}/authors/id/C/CJ/CJM/HTML-Tree-${PV}.tar.gz \
           file://bin-htmltree-fix-shebang.patch \
"
SRC_URI[sha256sum] = "7d6d73fca622aa74855a8b088faa39454a0f91b7af83c9ec0387f01eefc2148f"

S = "${WORKDIR}/HTML-Tree-${PV}"

inherit cpan_build

export PERL_USE_UNSAFE_INC = "1"

DEPENDS += "libmodule-build-perl-native \
"

do_install:append() {
  sed -i \
    -e 's|${TMPDIR}||g' \
    `find ${D}/usr/share/doc/perl/html/site/lib/HTML/ -type f` \
    `find ${D}/usr/lib/perl5 -type f -name .packlist`
}

RPROVIDES:${PN} = " libhtml-element-perl \
    libhtml-tree-assubs-perl \
    libhtml-tree-perl \
    libhtml-treebuilder-perl \
"

RDEPENDS:${PN} = " perl-module-b \
    perl-module-base \
    perl-module-strict \
    perl-module-warnings \
    perl-module-exporter \
    perl-module-carp \
"

BBCLASSEXTEND = "native"

