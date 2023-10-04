SUMMARY = "Initscript for auto-loading kernel modules on boot"
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://modutils.sh;beginline=3;endline=3;md5=b2dccaa94b3629a08bfb4f983cad6f89"
SRC_URI = "file://modutils.sh"


S = "${WORKDIR}"

INITSCRIPT_NAME = "modutils.sh"
INITSCRIPT_PARAMS = "start 06 S ."

inherit update-rc.d

do_compile () {
}

do_install () {
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/modutils.sh ${D}${sysconfdir}/init.d/
}

PACKAGE_WRITE_DEPS:append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"
pkg_postinst:${PN} () {
	if type systemctl >/dev/null 2>/dev/null; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask modutils.service
	fi
}
