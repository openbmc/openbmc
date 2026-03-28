SUMMARY = "Administration tool for arp packet filtering"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=53b4a999993871a28ab1488fdbd2e73e"
SRCREV = "efae8949e31f8b2eb6290f377a28384cecaf105a"
PV = "0.0.5+git"

SRC_URI = " \
    git://git.netfilter.org/arptables;branch=master \
    file://0001-Use-ARPCFLAGS-for-package-specific-compiler-flags.patch \
    file://arptables-arpt-get-target-fix.patch \
    file://arptables.service \
"

SYSTEMD_SERVICE:${PN} = "arptables.service"

inherit systemd

EXTRA_OEMAKE = "'BINDIR=${sbindir}' 'MANDIR=${mandir}'"

do_install() {
    oe_runmake install DESTDIR=${D}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${UNPACKDIR}/arptables.service ${D}${systemd_unitdir}/system
    fi
}

RDEPENDS:${PN} += "perl"

# the install target is not multi-job safe, but it doesn't do much
# so we just install serially
#
PARALLEL_MAKEINST = "-j1"
