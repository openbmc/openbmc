DESCRIPTION = "netkit-rwho includes, ruptime rwho and rwhod (Daemon to maintain data for rwho/ruptime)"
HOMEPAGE = "ftp://ftp.uk.linux.org/pub/linux/Networking/netkit"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://rwho/rwho.c;beginline=2;endline=3;md5=5a85f13c0142d72fc378e00f15da5b9e"

SRC_URI = "${DEBIAN_MIRROR}/main/n/netkit-rwho/netkit-rwho_${PV}.orig.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/n/netkit-rwho/netkit-rwho_${PV}-13.debian.tar.gz;name=patch13 \
           file://rwhod \
           file://rwhod.default \
"
SRC_URI[archive.md5sum] = "0f71620d45d472f89134ba0d74242e75"
SRC_URI[archive.sha256sum] = "0409e2ce4bfdb2dacb2c193d0fedfc49bb975cb057c5c6b0ffcca603a1188da7"
SRC_URI[patch13.md5sum] = "82ed5a3c6b0bbf00b36508b9787963b9"
SRC_URI[patch13.sha256sum] = "53aefa27d98b565bf756db57120c03bd224a238e45699d92076420272a6eba8e"

inherit autotools-brokensep useradd update-rc.d update-alternatives

CFLAGS += " -D_GNU_SOURCE"

debian_do_patch() {
    cd ${S}
    while read line; do patch -p1 < ${WORKDIR}/debian/patches/$line; done < ${WORKDIR}/debian/patches/series
}

python do_patch() {
    bb.build.exec_func('debian_do_patch', d)
    bb.build.exec_func('patch_do_patch', d)
}

do_configure () {
    ./configure --prefix=${prefix}
    echo "LDFLAGS=${LDFLAGS}" >> MCONFIG
}

do_install () {
    # rwho & ruptime
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    #rwhod
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man8
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/default
    sed -i 's/install -s/install/' rwho/Makefile
    sed -i 's/install -s/install/' ruptime/Makefile
    sed -i 's/install -s/install/' rwhod/Makefile
    oe_runmake 'INSTALLROOT=${D}' 'BINMODE=0755' \
    'DAEMONMODE=0755' 'MANMODE=0644' \
    'BINDIR=${bindir}' 'SBINDIR=${sbindir}' \
    'MANDIR=${mandir}' install

    install -m 0644 ${WORKDIR}/rwhod.default ${D}${sysconfdir}/default/rwhod
    install -m 755 ${WORKDIR}/rwhod ${D}${sysconfdir}/init.d

    mkdir -p -m 755 ${D}${localstatedir}/spool/rwho
    chown -R rwhod ${D}${localstatedir}/spool/rwho
}

pkg_postinst_${PN}-server() {
    ${sysconfdir}/init.d/rwhod start
}

pkg_postrm_${PN}-server() {
    ${sysconfdir}/init.d/rwhod stop
}

INITSCRIPT_NAME = "rwhod"
INITSCRIPT_PARAM = "defaults 85 15"

USERADD_PACKAGES = "${PN}-server"
GROUPADD_PARAM_${PN}-server = "--system rwhod"
USERADD_PARAM_${PN}-server = "--system -g rwhod --home-dir  ${localstatedir}/spool/rwho \
        --no-create-home  --shell /bin/false rwhod"

INSANE_SKIP_${PN} = "already-stripped"

PACKAGES = "${PN}-client ${PN}-server ${PN}-doc ${BPN}-dbg"
FILES_${PN}-client = "${bindir}/*"
FILES_${PN}-server = "${sbindir}/* ${localstatedir} ${sysconfdir}/default/* ${sysconfdir}/init.d/*"
FILES_${PN}-doc = "${mandir}"
FILES_${PN}-dbg = "${prefix}/src/debug \
            ${bindir}/.debug ${sbindir}/.debug"
