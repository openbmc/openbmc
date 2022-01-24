DESCRIPTION = "PDF transformation/inspection software"
HOMEPAGE = "http://qpdf.sourceforge.net"
LICENSE = "Artistic-2.0"
SECTION = "libs"
DEPENDS = "libpcre zlib libjpeg-turbo"

SRC_URI = "${SOURCEFORGE_MIRROR}/qpdf/qpdf-${PV}.tar.gz"
SRC_URI[sha256sum] = "88257d36a44fd5c50b2879488324dd9cafc11686ae49d8c4922a4872203ce006"

LIC_FILES_CHKSUM = "file://Artistic-2.0;md5=7806296b9fae874361e6fb10072b7ee3"

inherit autotools-brokensep gettext

# disable random file detection for cross-compile
EXTRA_OECONF = "--without-random \
                --disable-static \
                --disable-check-autofiles \
                "

LDFLAGS:append:mipsarch = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

S="${WORKDIR}/${BPN}-${PV}"

# avoid Makefile returning error on 'make clean' before configure was run
CLEANBROKEN = "1"

DEBIAN_NOAUTONAME:libqpdf = "1"

PACKAGES =+ "libqpdf"
FILES:libqpdf = "${libdir}/libqpdf.so.*"

RDEPENDS:${PN} = "libqpdf"
