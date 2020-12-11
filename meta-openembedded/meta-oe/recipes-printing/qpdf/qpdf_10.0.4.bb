DESCRIPTION = "PDF transformation/inspection software"
HOMEPAGE = "http://qpdf.sourceforge.net"
LICENSE = "Artistic-2.0"
SECTION = "libs"
DEPENDS = "libpcre zlib libjpeg-turbo"

SRC_URI = "${SOURCEFORGE_MIRROR}/qpdf/qpdf-${PV}.tar.gz"

LIC_FILES_CHKSUM = "file://Artistic-2.0;md5=7806296b9fae874361e6fb10072b7ee3"
SRC_URI[md5sum] = "b4c6a66c45bb946944c2fe14ef02d0e0"
SRC_URI[sha256sum] = "b191dc4af4ea4630cdc404a4ee95a0f21c12f56ecc4ae045f04425464d7c696e"

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
