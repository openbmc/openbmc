SUMMARY = "Systemd system configuration"
DESCRIPTION = "Systemd may require slightly different configuration for \
different machines.  For example, qemu machines require a longer \
DefaultTimeoutStartSec setting."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit features_check
REQUIRED_DISTRO_FEATURES += "usrmerge"

PE = "1"

PACKAGECONFIG ??= "dhcp-ethernet"
PACKAGECONFIG[dhcp-ethernet] = ""

SRC_URI = "\
    file://journald.conf \
    file://logind.conf \
    file://system.conf \
    file://system.conf-qemuall \
    file://wired.network \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
	install -D -m0644 ${S}/journald.conf ${D}${systemd_unitdir}/journald.conf.d/00-${PN}.conf
	install -D -m0644 ${S}/logind.conf ${D}${systemd_unitdir}/logind.conf.d/00-${PN}.conf
	install -D -m0644 ${S}/system.conf ${D}${systemd_unitdir}/system.conf.d/00-${PN}.conf

        if ${@bb.utils.contains('PACKAGECONFIG', 'dhcp-ethernet', 'true', 'false', d)}; then
		install -D -m0644 ${S}/wired.network ${D}${systemd_unitdir}/network/80-wired.network
        fi
}

# Based on change from YP bug 8141, OE commit 5196d7bacaef1076c361adaa2867be31759c1b52
do_install:append:qemuall() {
	install -D -m0644 ${S}/system.conf-qemuall ${D}${systemd_unitdir}/system.conf.d/01-${PN}.conf
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} = "\
    ${systemd_unitdir}/journald.conf.d/ \
    ${systemd_unitdir}/logind.conf.d/ \
    ${systemd_unitdir}/system.conf.d/ \
    ${systemd_unitdir}/network/ \
"
