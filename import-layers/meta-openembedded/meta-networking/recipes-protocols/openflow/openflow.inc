SUMMARY = "OpenFlow communications protocol"
DESCRIPTION = "\
Open standard that enables researchers to run experimental protocols in \
contained networks.  OpenFlow is a communications interface between \
control and forwarding planes of a software-defined networking architecture.\
"
HOMEPAGE = "http://www.openflow.org"

SECTION = "net"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=e870c934e2c3d6ccf085fd7cf0a1e2e2"

SRC_URI = "git://gitosis.stanford.edu/openflow.git;protocol=git"

DEPENDS = "virtual/libc"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--enable-ssl,--disable-ssl, openssl openssl-native, libssl"

EXTRA_OECONF += " \
                 KARCH=${TARGET_ARCH} \
                 ${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'SSL_LIBS="-lssl -lcrypto"', '', d)} \
                "

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

do_configure_prepend() {
    ./boot.sh
}

do_install_append() {
    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run
}
