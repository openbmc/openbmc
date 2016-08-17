SUMMARY = "Administration tool for arp packet filtering"
LICENSE = "GPL-2.0"
SECTION = "net"

PR = "r2"

RDEPENDS_${PN} += "perl"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
SRC_URI = " \
    ${SOURCEFORGE_MIRROR}/ebtables/arptables-v${PV}.tar.gz;name=arptables \
    file://arptables-compile-install.patch \
    file://arptables-init-busybox.patch \
    file://arptables-arpt-get-target-fix.patch \
    file://arptables-remove-bashism.patch \
"
SRC_URI[arptables.md5sum] = "1d4ab05761f063b0751645d8f2b8f8e5"
SRC_URI[arptables.sha256sum] = "e529fd465c67d69ad335299a043516e6b38cdcd337a5ed21718413e96073f928"

S = "${WORKDIR}/arptables-v${PV}"

do_compile () {
    oe_runmake
}

# the install target is not multi-job safe, but it doesn't do much
# so we just install serially
#
PARALLEL_MAKEINST = "-j1"

fakeroot do_install () {
    oe_runmake 'BINDIR=${sbindir}' 'MANDIR=${mandir}/' 'DESTDIR=${D}' install
}
