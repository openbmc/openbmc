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

S = "${WORKDIR}"

MOUNT_BASE = "/run/media"

do_install() {
    install -d ${D}${sysconfdir}/udev/rules.d

    install -m 0644 ${WORKDIR}/automount.rules     ${D}${sysconfdir}/udev/rules.d/automount.rules
    install -m 0644 ${WORKDIR}/autonet.rules       ${D}${sysconfdir}/udev/rules.d/autonet.rules
    install -m 0644 ${WORKDIR}/localextra.rules    ${D}${sysconfdir}/udev/rules.d/localextra.rules

    install -d ${D}${sysconfdir}/udev/mount.ignorelist.d
    install -m 0644 ${WORKDIR}/mount.ignorelist     ${D}${sysconfdir}/udev/

    install -d ${D}${sysconfdir}/udev/scripts/

    install -m 0755 ${WORKDIR}/mount.sh ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's|@systemd_unitdir@|${systemd_unitdir}|g' ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's|@base_sbindir@|${base_sbindir}|g' ${D}${sysconfdir}/udev/scripts/mount.sh
    sed -i 's|@MOUNT_BASE@|${MOUNT_BASE}|g' ${D}${sysconfdir}/udev/scripts/mount.sh

    install -m 0755 ${WORKDIR}/network.sh ${D}${sysconfdir}/udev/scripts
}

pkg_postinst:${PN} () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/\[Service\]/aMountFlags=shared" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}

pkg_postrm:${PN} () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/MountFlags=shared/d" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}

RDEPENDS:${PN} = "udev util-linux-blkid ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'util-linux-lsblk', '', d)}"
CONFFILES:${PN} = "${sysconfdir}/udev/mount.ignorelist"

# to replace udev-extra-rules from meta-oe
RPROVIDES:${PN} = "udev-extra-rules"
RREPLACES:${PN} = "udev-extra-rules"
RCONFLICTS:${PN} = "udev-extra-rules"
