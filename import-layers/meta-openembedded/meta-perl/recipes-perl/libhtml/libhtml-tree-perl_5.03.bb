SUMMARY = "HTML::TreeBuilder - Parser that builds a HTML syntax tree"
DESCRIPTION = "This distribution contains a suite of modules for representing, \
creating, and extracting information from HTML syntax trees; there is \
also relevent documentation.  These modules used to be part of the \
libwww-perl distribution, but are now unbundled in order to facilitate \
a separate development track."
SECTION = "libs"

HOMEPAGE = "http://www.cpan.org/authors/id/C/CJ/CJM/HTML-Tree-${PV}.readme"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb57a8958cae73cb65e7d0c26339242"

SRC_URI = "${CPAN_MIRROR}/authors/id/C/CJ/CJM/HTML-Tree-${PV}.tar.gz \
           file://bin-htmltree-fix-shebang.patch \
"
SRC_URI[md5sum] = "d9271d60b872ed6fbe68b2d0fe8c450e"
SRC_URI[sha256sum] = "7d6d73fca622aa74855a8b088faa39454a0f91b7af83c9ec0387f01eefc2148f"

S = "${WORKDIR}/HTML-Tree-${PV}"

inherit cpan_build

DEPENDS += "libmodule-build-perl-native \
"

RPROVIDES_${PN} = " libhtml-element-perl \
		    libhtml-tree-assubs-perl \
		    libhtml-tree-perl \
		    libhtml-treebuilder-perl \
"

RDEPENDS_${PN} = " perl-module-b \
                   perl-module-base \
                   perl-module-strict \
                   perl-module-warnings \
                   perl-module-exporter \
                   perl-module-carp \
"

BBCLASSEXTEND = "native"

