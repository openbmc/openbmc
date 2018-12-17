DESCRIPTION = "netkit-rusers includes rusers - Displays who is logged in to machines on local network \
    rusersd - Logged in users server"
HOMEPAGE = "ftp://ftp.uk.linux.org/pub/linux/Networking/netkit"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://rusers/rusers.c;beginline=2;endline=3;md5=f4fc634a4ce8c569911196b72b10770e"
DEPENDS = " tcp-wrappers libtirpc rpcbind"

SRC_URI = "http://http.debian.net/debian/pool/main/n/${BPN}/${BPN}_${PV}.orig.tar.gz;name=archive \
           http://http.debian.net/debian/pool/main/n/${BPN}/${BPN}_${PV}-8.diff.gz;name=patch8 \
           file://rpc.rusersd-Makefile-fix-parallel-build-issue.patch \
           file://0001-Link-with-libtirpc.patch \
"

SRC_URI[archive.md5sum] = "dc99a80b9fde2ab427c874f88f1c1602"
SRC_URI[archive.sha256sum] = "f00138651865ad2dcfec5dedda0cda403cb80c4ab68efcc3bbccafe198c24b6d"
SRC_URI[patch8.md5sum] = "1ff498113e0f920d92088092e5570bdc"
SRC_URI[patch8.sha256sum] = "14882dbdda4e37baa84d55b54b46c7e063a20fc9e04d1be1a2807643cd0f3067"

inherit autotools-brokensep

CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LIBS += "-ltirpc"

do_configure () {
    ./configure --prefix=${prefix}
    echo "LDFLAGS=${LDFLAGS}" >> MCONFIG
    echo "USE_GLIBC=1" >> MCONFIG
    echo "LIBS=${LIBS}" >> MCONFIG
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man8
    install -d ${D}${sysconfdir}/xinetd.d

    sed -i 's/install -s/install/' rusers/Makefile
    sed -i 's/install -s/install/' rup/Makefile
    sed -i 's/install -s/install/' rpc.rusersd/Makefile

    oe_runmake 'INSTALLROOT=${D}' 'BINMODE=0755' \
    'DAEMONMODE=0755' 'MANMODE=0644' \
    'BINDIR=${bindir}' 'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' install

    # create the xinetd config file
    cat >rusersd.conf <<EOF
 service rusersd
 {
    disable     = yes
    type        = RPC
    rpc_version = 1-2
    socket_type = dgram
    protocol    = udp
    wait        = yes
    user        = root
    server      = ${sbindir}/rpc.rusersd
 }
EOF
    install rusersd.conf ${D}/${sysconfdir}/xinetd.d/rusersd
}


INSANE_SKIP_${PN} = "already-stripped"

PACKAGES = "${PN}-client ${PN}-server ${PN}-doc ${BPN}-dbg"
FILES_${PN}-client = "${bindir}/*"
FILES_${PN}-server = "${sbindir}/* ${sysconfdir}"
FILES_${PN}-doc = "${mandir}"
FILES_${PN}-dbg = "${prefix}/src/debug \
            ${bindir}/.debug ${sbindir}/.debug"

RDEPENDS_${PN}-server = "tcp-wrappers xinetd rpcbind"

# http://errors.yoctoproject.org/Errors/Details/186962/
EXCLUDE_FROM_WORLD_libc-musl = "1"
