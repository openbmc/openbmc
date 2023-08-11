SUMMARY = "Encode::Locale - Determine the locale encoding"
HOMEPAGE = "https://metacpan.org/module/Encode::Locale"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;md5=14e8006c2134045725fd81292a323d24"

SRC_URI = "${CPAN_MIRROR}/authors/id/G/GA/GAAS/Encode-Locale-${PV}.tar.gz"
SRC_URI[md5sum] = "fcfdb8e4ee34bcf62aed429b4a23db27"
SRC_URI[sha256sum] = "176fa02771f542a4efb1dbc2a4c928e8f4391bf4078473bd6040d8f11adb0ec1"

S = "${WORKDIR}/Encode-Locale-${PV}"

inherit cpan

RDEPENDS:${PN} += "libencode-perl \
                   libencode-alias-perl \
                   perl-module-base \
"

BBCLASSEXTEND = "native"
