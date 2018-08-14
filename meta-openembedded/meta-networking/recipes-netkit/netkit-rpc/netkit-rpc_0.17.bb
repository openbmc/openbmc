DESCRIPTION = "netkit-rpc includes rpcinfo and rpcgen."
HOMEPAGE = "http://ftp.linux.org.uk/pub/linux/Networking/netkit"
SECTION = "net"
LICENSE = "SPL-1.0"
LIC_FILES_CHKSUM = "file://rpcinfo/rpcinfo.c;beginline=2;endline=3;md5=3e6339e3ce266e1122c5ba293e04bc89"

DEPENDS_append_libc-musl = " libtirpc"
SRC_URI = "http://sources.openembedded.org/${BPN}-${PV}.tar.gz \
           file://gcc4.patch \
           file://0001-rpcgen-Fix-printf-formats.patch \
           "
SRC_URI[md5sum] = "67212720482ea1aea9182a98653a9642"
SRC_URI[sha256sum] = "421d63b414162237a72867061f1bd3e3752a0d962cd5d30b5e933ddad8a14d3b"
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LIBS_append_libc-musl = " -ltirpc"

do_configure () {
    ./configure --prefix=${prefix}
    echo "LDFLAGS=${LDFLAGS}" > MCONFIG
    echo "CC=${CC}" >> MCONFIG
    echo "LD=${LD}" >> MCONFIG
    echo "CFLAGS=${CFLAGS}" >> MCONFIG
    echo "LDFLAGS=${LDFLAGS}" >> MCONFIG
    echo "LIBS=${LIBS}" >> MCONFIG
}

do_compile () {
    oe_runmake 'CC=${CC}' 'LD=${LD}' 'LDFLAGS=${LDFLAGS}'
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man8

    # remove strip flag
    sed -i 's/install -s/install/' rpcinfo/Makefile
    sed -i 's/install -s/install/' rpcgen/Makefile

    oe_runmake 'INSTALLROOT=${D}' 'BINMODE=0755' \
    'DAEMONMODE=0755' 'MANMODE=0644' \
    'BINDIR=${bindir}' 'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' install
}
