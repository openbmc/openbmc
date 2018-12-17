SUMMARY = "Filtering tool for a Linux-based bridging firewall"
HOMEPAGE = "http://sourceforge.net/projects/ebtables/"
DESCRIPTION = "Utility for basic Ethernet frame filtering on a Linux bridge, \
               advanced logging, MAC DNAT/SNAT and brouting."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=53b4a999993871a28ab1488fdbd2e73e"
SECTION = "net"
PR = "r4"

RDEPENDS_${PN} += "bash"

RRECOMMENDS_${PN} += "kernel-module-ebtables \
    "

SRC_URI = "${SOURCEFORGE_MIRROR}/ebtables/ebtables-v${PV}.tar.gz \
           file://ebtables-save \
           file://installnonroot.patch \
           file://01debian_defaultconfig.patch \
           file://ebtables.init \
           file://ebtables.common \
           file://ebtables.service \
           file://no-as-needed.patch \
           file://0001-add-RARP-and-update-iana-url.patch \
           file://0002-fix-compilation-warning.patch \
           file://0003-add-info-about-Wl-no-as-needed.patch \
           file://0004-workaround-for-kernel-regression-bug-IPv6-source-des.patch \
           file://0005-Add-noflush-command-line-support-for-ebtables-restor.patch \
           file://0006-don-t-print-IPv6-mask-if-it-s-all-ones-based-on-patc.patch \
           file://0007-extensions-Use-stdint-types.patch \
           file://0008-ethernetdb.h-Remove-C-specific-compiler-hint-macro-_.patch \
           file://0009-ebtables-Allow-RETURN-target-rules-in-user-defined-c.patch \
           "

SRC_URI_append_libc-musl = " file://0010-Adjust-header-include-sequence.patch"

SRC_URI[md5sum] = "506742a3d44b9925955425a659c1a8d0"
SRC_URI[sha256sum] = "dc6f7b484f207dc712bfca81645f45120cb6aee3380e77a1771e9c34a9a4455d"

# It is using '-' but not '.' as delimiter for the version in the releases page,
# which causes the version comparison unmatched.
#UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/ebtables/files/ebtables/"
#UPSTREAM_CHECK_REGEX = "ebtables-(?P<pver>\d+(\-\d+)+)"

RECIPE_UPSTREAM_VERSION = "2.0.10-4"
RECIPE_UPSTREAM_DATE = "Dec 15, 2011"
CHECK_DATE = "May 25, 2018"

S = "${WORKDIR}/ebtables-v${PV}"

inherit update-rc.d systemd

python __anonymous () {
    import re

    karch = d.getVar('TARGET_ARCH')
    multilib = d.getVar('MLPREFIX')

    if multilib and ( karch == 'powerpc64' or karch == 'arm' ):
        searchstr = "lib.?32"
        reg = re.compile(searchstr)
        if reg.search(multilib):
            d.appendVar('CFLAGS' ,' -DKERNEL_64_USERSPACE_32 -DEBT_MIN_ALIGN=8')
}

EXTRA_OEMAKE = " \
        BINDIR=${base_sbindir} \
        MANDIR=${mandir} \
        ETHERTYPESPATH=${sysconfdir} \
        INITDIR=${sysconfdir}/init.d \
        SYSCONFIGDIR=${sysconfdir}/default \
        LIBDIR=${base_libdir}/ebtables \
        'CC=${CC}' \
        'CFLAGS=${CFLAGS}' \
        'LDFLAGS=${LDFLAGS} -Wl,--no-as-needed' \
        'LD=${LD}' \
"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/ebtables.common ${D}${sbindir}/ebtables.common
    # Fix hardcoded paths in scripts
    sed -i 's!/sbin/!${base_sbindir}/!g' ${D}${sbindir}/ebtables.common
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sbindir}/ebtables.common

    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sysconfdir}/default
    install -d ${D}${sysconfdir}/ebtables
    oe_runmake DESTDIR='${D}' install
    install -m 0755 ${WORKDIR}/ebtables.init ${D}/${sysconfdir}/init.d/ebtables
    mv ${D}${sysconfdir}/default/ebtables-config ${D}${sysconfdir}/default/ebtables
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/ebtables

    # Replace upstream ebtables-save perl script with Fedora bash based rewrite
    # http://pkgs.fedoraproject.org/cgit/rpms/ebtables.git/tree/ebtables-save
    install -m 0755 ${WORKDIR}/ebtables-save ${D}${base_sbindir}/ebtables-save
    sed -i 's!/sbin/!${base_sbindir}/!g' ${D}${base_sbindir}/ebtables-save

    # Install systemd service files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/ebtables.service ${D}${systemd_unitdir}/system
    sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/ebtables.service
}

CONFFILES_${PN} += "${sysconfdir}/default/ebtables"

INITSCRIPT_NAME = "ebtables"
INITSCRIPT_PARAMS = "start 41 S . stop 41 6 ."

SYSTEMD_SERVICE_${PN} = "ebtables.service"

FILES_${PN}-dbg += "${base_libdir}/ebtables/.debug"
FILES_${PN} += "${base_libdir}/ebtables/*.so"
