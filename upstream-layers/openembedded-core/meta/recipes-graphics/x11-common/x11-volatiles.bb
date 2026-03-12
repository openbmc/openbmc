SUMMARY = "Xserver Volatile Directories"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
SECTION = "x11"

SRC_URI = "file://02_x11"

S = "${UNPACKDIR}"

inherit bin_package features_check

REQUIRED_DISTRO_FEATURES = "x11"
CONFLICT_DISTRO_FEATURES = "systemd"

do_install() {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${UNPACKDIR}/02_x11 ${D}${sysconfdir}/default/volatiles
}

FILES:${PN} += "${sysconfdir}/default/volatiles"
