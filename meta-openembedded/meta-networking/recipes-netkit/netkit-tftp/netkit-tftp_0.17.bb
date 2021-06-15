SUMMARY = "tftp - Trivial file transfer protocol client"
SECTION = "net"
LICENSE = "BSD-4-Clause"
DEPENDS = "tcp-wrappers"

LIC_FILES_CHKSUM = "file://tftp/tftp.c;beginline=2;endline=3;md5=84d2cfe1e60863a7d82648734ba4d30c"

SRC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}.orig.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}-18.diff.gz;name=patch18 \
           file://tftp.conf \
           file://0001-tftp-Include-missing-fcntl.h.patch \
           "

SRC_URI[archive.md5sum] = "b7262c798e2ff50e29c2ff50dfd8d6a8"
SRC_URI[archive.sha256sum] = "3a43c0010d4e61f412563fd83769d4667d8b8e82903526d21cb9205fe55ad14d"
SRC_URI[patch18.md5sum] = "cb29e7a33dd85105ba6e6ec4f971e42c"
SRC_URI[patch18.sha256sum] = "092437d27b4fa88c044ef6290372fee5ce06d223607f0e22a6e527065c8930e7"

inherit autotools-brokensep update-alternatives

do_configure () {
    ./configure --prefix=${prefix}
    echo "CFLAGS=${CFLAGS}" > MCONFIG
}

do_compile () {
    oe_runmake 'CC=${CC}' 'LD=${LD}' 'LDFLAGS=${LDFLAGS}' 'CFLAGS=${CFLAGS}'
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man8
    install -d ${D}${sysconfdir}/xinetd.d

    sed -i 's/install -s/install/' tftp/Makefile
    sed -i 's/install -s/install/' tftpd/Makefile

    oe_runmake 'INSTALLROOT=${D}' 'BINMODE=0755' \
    'DAEMONMODE=0755' 'MANMODE=0644' \
    'BINDIR=${bindir}' 'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' install

    install ${WORKDIR}/tftp.conf ${D}/${sysconfdir}/xinetd.d/tftp
}

PACKAGES = "${PN}-client ${PN}-server ${PN}-doc ${BPN}-dbg"
FILES_${PN}-client = "${bindir}/*"
FILES_${PN}-server = "${sbindir}/* ${sysconfdir}/xinetd.d/*"
FILES_${PN}-doc = "${mandir}"
FILES_${PN}-dbg = "${prefix}/src/debug \
    ${bindir}/.debug ${sbindir}/.debug"

RDEPENDS_${PN}-server = "tcp-wrappers xinetd"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN}-client = "tftp"
ALTERNATIVE_LINK_NAME[tftp] = "${bindir}/tftp"
