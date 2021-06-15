SUMMARY = "Administration tool for arp packet filtering"
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
SRCREV = "efae8949e31f8b2eb6290f377a28384cecaf105a"
PV = "0.0.5+git${SRCPV}"

SRC_URI = " \
    git://git.netfilter.org/arptables \
    file://0001-Use-ARPCFLAGS-for-package-specific-compiler-flags.patch \
    file://arptables-arpt-get-target-fix.patch \
    file://arptables.service \
"
SRC_URI[arptables.md5sum] = "1d4ab05761f063b0751645d8f2b8f8e5"
SRC_URI[arptables.sha256sum] = "e529fd465c67d69ad335299a043516e6b38cdcd337a5ed21718413e96073f928"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE_${PN} = "arptables.service"

inherit systemd

EXTRA_OEMAKE = "'BINDIR=${sbindir}' 'MANDIR=${mandir}'"

do_install() {
    oe_runmake install DESTDIR=${D}
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/arptables.service ${D}${systemd_unitdir}/system
    fi
}

RDEPENDS_${PN} += "perl"

# the install target is not multi-job safe, but it doesn't do much
# so we just install serially
#
PARALLEL_MAKEINST = "-j1"
