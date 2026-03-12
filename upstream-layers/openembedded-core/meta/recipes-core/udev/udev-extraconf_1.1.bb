SUMMARY = "Extra machine specific configuration files"
HOMEPAGE = "https://wiki.gentoo.org/wiki/Eudev"
DESCRIPTION = "Extra machine specific configuration files for udev, specifically information on devices to ignore."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
       file://automount.rules \
       file://mount.sh \
       file://mount.ignorelist \
       file://autonet.rules \
       file://network.sh \
       file://localextra.rules \
"

S = "${UNPACKDIR}"

MOUNT_BASE = "/run/media"
MOUNT_GROUP ?= "disk"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d

    install -m 0644 ${S}/automount.rules     ${D}${sysconfdir}/udev/rules.d/automount.rules
    install -m 0644 ${S}/autonet.rules       ${D}${sysconfdir}/udev/rules.d/autonet.rules
    install -m 0644 ${S}/localextra.rules    ${D}${sysconfdir}/udev/rules.d/localextra.rules

    install -d ${D}${sysconfdir}/udev/mount.ignorelist.d
    install -m 0644 ${S}/mount.ignorelist     ${D}${sysconfdir}/udev/

    install -d ${D}${sysconfdir}/udev/scripts/

    install -m 0755 ${S}/mount.sh ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -e 's|@systemd_unitdir@|${systemd_unitdir}|g' \
        -e 's|@base_sbindir@|${base_sbindir}|g' \
        -e 's|@MOUNT_BASE@|${MOUNT_BASE}|g' \
        -e 's|@MOUNT_GROUP@|${MOUNT_GROUP}|g' \
        -i ${D}${sysconfdir}/udev/scripts/mount.sh

    install -m 0755 ${S}/network.sh ${D}${sysconfdir}/udev/scripts
}

PACKAGES =+ "${PN}-automount ${PN}-autonet"

FILES:${PN}-automount = " \
    ${sysconfdir}/udev/rules.d/automount.rules \
    ${sysconfdir}/udev/scripts/mount.sh \
    ${sysconfdir}/udev/mount.ignorelist \
"

RDEPENDS:${PN}-automount = "udev util-linux-blkid ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'util-linux-lsblk', '', d)}"
CONFFILES:${PN}-automount = "${sysconfdir}/udev/mount.ignorelist"

pkg_postinst:${PN}-automount () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/\[Service\]/aMountFlags=shared" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}

pkg_postrm:${PN}-automount () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/MountFlags=shared/d" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}

FILES:${PN}-autonet = " \
    ${sysconfdir}/udev/rules.d/autonet.rules \
    ${sysconfdir}/udev/scripts/network.sh \
"

RDEPENDS:${PN}-autonet = "udev"

# to replace udev-extra-rules from meta-oe
RPROVIDES:${PN} = "udev-extra-rules"
RREPLACES:${PN} = "udev-extra-rules"
RCONFLICTS:${PN} = "udev-extra-rules"

RDEPENDS:${PN} = "${PN}-automount ${PN}-autonet"
