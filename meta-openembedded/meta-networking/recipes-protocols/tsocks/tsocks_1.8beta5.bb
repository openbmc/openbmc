SUMMARY = "Libraries and wrapper for using a SOCKS proxy"
DESCRIPTION = "The role of tsocks is to allow non SOCKS aware \
applications (e.g telnet, ssh, ftp etc) to use SOCKS without any \
modification. It does this by intercepting the calls that applications \
make to establish network connections and negotating them through a \
SOCKS server as necessary."
HOMEPAGE = "http://sourceforge.net/projects/tsocks/"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

SRC_URI = "http://downloads.sourceforge.net/tsocks/tsocks-${PV}.tar.gz \
           file://makefile-add-ldflags.patch \
          "

SRC_URI[md5sum] = "51caefd77e5d440d0bbd6443db4fc0f8"
SRC_URI[sha256sum] = "849d7ef5af80d03e76cc05ed9fb8fa2bcc2b724b51ebfd1b6be11c7863f5b347"

inherit autotools-brokensep

LIBS_append_libc-musl = " -lssp_nonshared"
LIBS_append_libc-glibc = " -lc_nonshared"

S = "${WORKDIR}/tsocks-1.8"

FILES_${PN} = "${libdir}/* ${bindir}/tsocks"
FILES_${PN}-dev = ""
INSANE_SKIP_${PN} = "dev-so"

EXTRA_OEMAKE = "SHCC='${CC} -fPIC ${LDFLAGS}' LIBS='${LIBS}'"

