SUMMARY = "Serial terminal support for systemd"
HOMEPAGE = "https://www.freedesktop.org/wiki/Software/systemd/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PR = "r5"

SERIAL_CONSOLE ?= "115200 ttyS0"

SRC_URI = "file://serial-getty@.service"

S = "${WORKDIR}"

do_install() {
	if [ ! -z "${SERIAL_CONSOLES}" ] ; then
		default_baudrate=`echo "${SERIAL_CONSOLES}" | sed 's/\;.*//'`
		install -d ${D}${systemd_unitdir}/system/
		install -d ${D}${sysconfdir}/systemd/system/getty.target.wants/
		install -m 0644 ${WORKDIR}/serial-getty@.service ${D}${systemd_unitdir}/system/
		sed -i -e s/\@BAUDRATE\@/$default_baudrate/g ${D}${systemd_unitdir}/system/serial-getty@.service

		tmp="${SERIAL_CONSOLES}"
		for entry in $tmp ; do
			baudrate=`echo $entry | sed 's/\;.*//'`
			ttydev=`echo $entry | sed -e 's/^[0-9]*\;//' -e 's/\;.*//'`
			if [ "$baudrate" = "$default_baudrate" ] ; then
				# enable the service
				ln -sf ${systemd_unitdir}/system/serial-getty@.service \
					${D}${sysconfdir}/systemd/system/getty.target.wants/serial-getty@$ttydev.service
			else
				# install custom service file for the non-default baudrate
				install -m 0644 ${WORKDIR}/serial-getty@.service ${D}${systemd_unitdir}/system/serial-getty$baudrate@.service
				sed -i -e s/\@BAUDRATE\@/$baudrate/g ${D}${systemd_unitdir}/system/serial-getty$baudrate@.service
				# enable the service
				ln -sf ${systemd_unitdir}/system/serial-getty$baudrate@.service \
					${D}${sysconfdir}/systemd/system/getty.target.wants/serial-getty$baudrate@$ttydev.service
			fi
		done
	fi
}

# This is a machine specific file
FILES_${PN} = "${systemd_unitdir}/system/*.service ${sysconfdir}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# As this package is tied to systemd, only build it when we're also building systemd.
python () {
    if not bb.utils.contains ('DISTRO_FEATURES', 'systemd', True, False, d):
        raise bb.parse.SkipPackage("'systemd' not in DISTRO_FEATURES")
}

ALLOW_EMPTY_${PN} = "1"
