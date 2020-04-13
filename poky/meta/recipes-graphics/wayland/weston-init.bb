SUMMARY = "Startup script and systemd unit file for the Weston Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://init \
           file://weston.env \
           file://weston.ini \
           file://weston@.service \
           file://71-weston-drm.rules \
           file://weston-start"

S = "${WORKDIR}"

do_install() {
	install -Dm755 ${WORKDIR}/init ${D}/${sysconfdir}/init.d/weston
	install -D -p -m0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini
	install -Dm644 ${WORKDIR}/weston.env ${D}${sysconfdir}/default/weston

	# Install Weston systemd service and accompanying udev rule
	install -D -p -m0644 ${WORKDIR}/weston@.service ${D}${systemd_system_unitdir}/weston@.service
	sed -i -e s:/etc:${sysconfdir}:g \
		-e s:/usr/bin:${bindir}:g \
		-e s:/var:${localstatedir}:g \
		${D}${systemd_unitdir}/system/weston@.service
	install -D -p -m0644 ${WORKDIR}/71-weston-drm.rules \
		${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules
	# Install weston-start script
	install -Dm755 ${WORKDIR}/weston-start ${D}${bindir}/weston-start
	sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
	sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start
}

do_install_append_libc-musl_qemux86() {
        echo "WESTON_DISABLE_ATOMIC=Y" >> ${D}${sysconfdir}/default/weston
}

do_install_append_libc-musl_qemux86-64() {
        echo "WESTON_DISABLE_ATOMIC=Y" >> ${D}${sysconfdir}/default/weston
}

inherit update-rc.d features_check systemd

# rdepends on weston which depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

RDEPENDS_${PN} = "weston kbd"

INITSCRIPT_NAME = "weston"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."

FILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini ${systemd_system_unitdir}/weston@.service ${sysconfdir}/default/weston"

CONFFILES_${PN} += "${sysconfdir}/xdg/weston/weston.ini ${sysconfdir}/default/weston"

SYSTEMD_SERVICE_${PN} = "weston@%i.service"
SYSTEMD_AUTO_ENABLE = "disable"

