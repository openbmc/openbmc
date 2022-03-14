SUMMARY = "Connman config to ignore wired interface on qemu machines"
DESCRIPTION = "This is the ConnMan configuration to avoid touching wired \
network interface inside qemu machines."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

PR = "r2"

S = "${WORKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} = "${sysconfdir}/*"

# Kernel IP-Config is perfectly capable of setting up networking passed in via ip=
do_install:append:qemuall() {
    mkdir -p ${D}${sysconfdir}/default
    echo "export EXTRA_PARAM=\"-I eth0\"" > ${D}${sysconfdir}/default/connman
}
