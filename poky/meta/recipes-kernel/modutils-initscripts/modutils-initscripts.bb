SUMMARY = "Initscript for auto-loading kernel modules on boot"
SECTION = "base"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7bf87fc37976e93ec66ad84fac58c098"
SRC_URI = "file://modutils.sh \
	   file://PD.patch"

PR = "r7"

S = "${WORKDIR}"

INITSCRIPT_NAME = "modutils.sh"
INITSCRIPT_PARAMS = "start 05 S ."

inherit update-rc.d

do_compile () {
}

do_install () {
	install -d ${D}${sysconfdir}/init.d/
	install -m 0755 ${WORKDIR}/modutils.sh ${D}${sysconfdir}/init.d/
}

PACKAGE_WRITE_DEPS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"
pkg_postinst_${PN} () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask modutils.service
	fi
}
