DESCRIPTION = "libetpan is a library for communicating with mail and news servers. \
It supports the protocols SMTP, POP3, IMAP and NNTP."
HOMEPAGE = "http://www.etpan.org"
SECTION = "libs"
DEPENDS = "gnutls liblockfile curl"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f18ebe7e452708c26f83954f81062ba7"

PR = "r1"

# http://errors.yoctoproject.org/Errors/Details/25823/
PNBLACKLIST[libetpan] ?= "Not compatible with gnutls version 3.4 currently in oe-core"

SRC_URI = "${SOURCEFORGE_MIRROR}/libetpan/libetpan-${PV}.tar.gz \
           file://libetpan-autoreconf.patch \
           file://libetpan-ldflags.patch \
           file://sepbuild.patch"

inherit autotools pkgconfig gettext binconfig

EXTRA_OECONF = "--without-openssl --without-sasl --with-gnutls --disable-db"

PARALLEL_MAKE = ""

FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev = "${bindir} ${includedir} ${libdir}/lib*.so ${libdir}/*.la ${libdir}/*.a ${libdir}/pkgconfig"


SRC_URI[md5sum] = "8ce8c6c071e81884a475b12b7f9a9cc0"
SRC_URI[sha256sum] = "1f3fda5c9e2961d1a6298dc8aadae321493d37727d8db45bc2e8d58f20547011"
