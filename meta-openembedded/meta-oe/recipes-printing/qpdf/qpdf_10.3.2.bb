DESCRIPTION = "PDF transformation/inspection software"
HOMEPAGE = "http://qpdf.sourceforge.net"
LICENSE = "Artistic-2.0"
SECTION = "libs"
DEPENDS = "libpcre zlib libjpeg-turbo"

SRC_URI = "${SOURCEFORGE_MIRROR}/qpdf/qpdf-${PV}.tar.gz"

LIC_FILES_CHKSUM = "file://Artistic-2.0;md5=7806296b9fae874361e6fb10072b7ee3"
SRC_URI[sha256sum] = "062808c40ef8741ec8160ae00168638a712cfa1d4bf673e8e595ab5eba1da947"

inherit autotools-brokensep gettext

# disable random file detection for cross-compile
EXTRA_OECONF = "--without-random \
                --disable-static \
                --disable-check-autofiles \
                "

EXTRA_OEMAKE_class-target = "LIBTOOL=${HOST_SYS}-libtool"

LDFLAGS_append_mipsarch = " -latomic"
LDFLAGS_append_riscv32 = " -latomic"

S="${WORKDIR}/${BPN}-${PV}"

# avoid Makefile returning error on 'make clean' before configure was run
CLEANBROKEN = "1"

DEBIAN_NOAUTONAME_libqpdf = "1"

PACKAGES =+ "libqpdf"
FILES_libqpdf = "${libdir}/libqpdf.so.*"

RDEPENDS_${PN} = "libqpdf"
