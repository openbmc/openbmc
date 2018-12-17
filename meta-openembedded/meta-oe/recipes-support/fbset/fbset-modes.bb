SUMMARY = "Default display timings and resolutions for fbset"
HOMEPAGE = "http://users.telenet.be/geertu/Linux/fbdev/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PV = "0.1.0"
PR = "r6"

SRC_URI = "file://fb.modes"
S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fb.modes ${D}${sysconfdir}
}

# fb.modes file is MACHINE_ARCH, base.bbclass correctly changes it to MACHINE_ARCH, but too late for allarch.bbclass
# to disable "all" behavior (found when comparing qemuarm and qemux86 signatures)
PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES_${PN} = "${sysconfdir}/fb.modes"
