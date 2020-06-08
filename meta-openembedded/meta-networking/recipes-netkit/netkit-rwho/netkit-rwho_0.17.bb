DESCRIPTION = "netkit-rwho includes, ruptime rwho and rwhod (Daemon to maintain data for rwho/ruptime)"
HOMEPAGE = "ftp://ftp.uk.linux.org/pub/linux/Networking/netkit"
SECTION = "net"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://rwho/rwho.c;beginline=2;endline=3;md5=5a85f13c0142d72fc378e00f15da5b9e"

SRC_URI = "${DEBIAN_MIRROR}/main/n/netkit-rwho/netkit-rwho_${PV}.orig.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/n/netkit-rwho/netkit-rwho_${PV}-13.debian.tar.gz;subdir=${BP};name=patch13 \
           file://rwhod \
           file://rwhod.default \
           file://0001-Add-missing-include-path-to-I-options.patch \
           file://0002-Fix-build-issues-found-with-musl.patch \
           "
SRC_URI[archive.md5sum] = "0f71620d45d472f89134ba0d74242e75"
SRC_URI[archive.sha256sum] = "0409e2ce4bfdb2dacb2c193d0fedfc49bb975cb057c5c6b0ffcca603a1188da7"
SRC_URI[patch13.md5sum] = "82ed5a3c6b0bbf00b36508b9787963b9"
SRC_URI[patch13.sha256sum] = "53aefa27d98b565bf756db57120c03bd224a238e45699d92076420272a6eba8e"

inherit autotools-brokensep useradd update-rc.d update-alternatives

CFLAGS += " -D_GNU_SOURCE"

# Unlike other Debian packages, net-tools *.diff.gz contains another series of
# patches maintained by quilt. So manually apply them before applying other local
# patches. Also remove all temp files before leaving, because do_patch() will pop
# up all previously applied patches in the start
do_patch[depends] += "quilt-native:do_populate_sysroot"
netkit_do_patch() {
        cd ${S}
        # it's important that we only pop the existing patches when they've
        # been applied, otherwise quilt will climb the directory tree
        # and reverse out some completely different set of patches
        if [ -d ${S}/patches ]; then
                # whilst this is the default directory, doing it like this
                # defeats the directory climbing that quilt will otherwise
                # do; note the directory must exist to defeat this, hence
                # the test inside which we operate
                QUILT_PATCHES=${S}/patches quilt pop -a
        fi
        if [ -d ${S}/.pc-${BPN} ]; then
                rm -rf ${S}/.pc
                mv ${S}/.pc-${BPN} ${S}/.pc
                QUILT_PATCHES=${S}/debian/patches quilt pop -a
                rm -rf ${S}/.pc ${S}/debian
        fi
        QUILT_PATCHES=${S}/debian/patches quilt push -a
        mv ${S}/.pc ${S}/.pc-${BPN}
}

do_unpack[cleandirs] += "${S}"

python do_patch() {
    bb.build.exec_func('netkit_do_patch', d)
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
