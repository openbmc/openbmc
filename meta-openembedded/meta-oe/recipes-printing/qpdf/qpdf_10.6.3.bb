DESCRIPTION = "PDF transformation/inspection software"
HOMEPAGE = "http://qpdf.sourceforge.net"
LICENSE = "Artistic-2.0"
SECTION = "libs"
DEPENDS = "libpcre zlib libjpeg-turbo"

SRC_URI = "${SOURCEFORGE_MIRROR}/qpdf/qpdf-${PV}.tar.gz"
SRC_URI[sha256sum] = "e8fc23b2a584ea68c963a897515d3eb3129186741dd19d13c86d31fa33493811"

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
