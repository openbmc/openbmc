DESCRIPTION = "netkit-telnet includes the telnet daemon and client."
SECTION = "net"
DEPENDS = "ncurses"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://telnet/telnet.cc;beginline=2;endline=3;md5=780868e7b566313e70cb701560ca95ef"

SRC_URI = "ftp://ftp.uk.linux.org/pub/linux/Networking/netkit/${BP}.tar.gz \
    file://To-aviod-buffer-overflow-in-telnet.patch \
    file://Warning-fix-in-the-step-of-install.patch \
    file://telnet-xinetd \
    file://cross-compile.patch \
"

EXTRA_OEMAKE = "INSTALLROOT=${D} SBINDIR=${sbindir} DAEMONMODE=755 \
    MANMODE=644 MANDIR=${mandir}"

do_configure () {
    ./configure --prefix=${prefix}
    sed -e 's#^CFLAGS=\(.*\)$#CFLAGS= -D_GNU_SOURCE \1#' \
        -e 's#^CXXFLAGS=\(.*\)$#CXXFLAGS= -D_GNU_SOURCE \1#' \
        -e 's#^LDFLAGS=.*$#LDFLAGS= ${LDFLAGS}#' \
        -i MCONFIG
}

do_compile () {
    oe_runmake 'CC=${CC}' 'LD=${LD}' 'LDFLAGS=${LDFLAGS}' SUB=telnet
    oe_runmake 'CC=${CC}' 'LD=${LD}' 'LDFLAGS=${LDFLAGS}' LIBS=-lutil SUB=telnetd
    oe_runmake 'CC=${CC}' 'LD=${LD}' 'LDFLAGS=${LDFLAGS}' SUB=telnetlogin
}

do_install () {
    install -d ${D}${bindir}
    install -m 0755 telnet/telnet ${D}${bindir}/telnet.${PN}
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man5
    install -d ${D}${mandir}/man8
    oe_runmake SUB=telnetd install
    rm -rf ${D}${mandir}/man1
    install -D -m 4750 ${B}/telnetlogin/telnetlogin ${D}/${libdir}/telnetlogin
    # fix up hardcoded paths
    sed -i -e 's,/usr/sbin/,${sbindir}/,' ${WORKDIR}/telnet-xinetd
    install -d  ${D}/etc/xinetd.d/
    install -p -m644 ${WORKDIR}/telnet-xinetd ${D}/etc/xinetd.d/telnet
}

pkg_postinst_${PN} () {
#!/bin/sh
    update-alternatives --install ${bindir}/telnet telnet telnet.${PN} 100
}

pkg_prerm_${PN} () {
#!/bin/sh
    update-alternatives --remove telnet telnet.${PN} 100
}

SRC_URI[md5sum] = "d6beabaaf53fe6e382c42ce3faa05a36"
SRC_URI[sha256sum] = "9c80d5c7838361a328fb6b60016d503def9ce53ad3c589f3b08ff71a2bb88e00"
FILES_${PN} += "${sbindir}/in.* ${libdir}/* ${sysconfdir}/xinetd.d/*"
