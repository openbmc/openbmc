SUMMARY = "Startup script and systemd unit file for the Weston Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

SRC_URI = "file://init \
           file://weston.service"

S = "${WORKDIR}"

do_install() {
	install -d ${D}/${sysconfdir}/init.d
	install -m755 ${WORKDIR}/init ${D}/${sysconfdir}/init.d/weston

	install -d ${D}${systemd_system_unitdir}
	install -m0644 ${WORKDIR}/weston.service ${D}${systemd_system_unitdir}
}

inherit allarch update-rc.d distro_features_check systemd

# rdepends on weston which depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

RDEPENDS_${PN} = "weston kbd"

INITSCRIPT_NAME = "weston"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."

SYSTEMD_SERVICE_${PN} = "weston.service"
