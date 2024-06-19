SUMMARY = "Startup script and systemd unit file for the Weston Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://init \
           file://weston.env \
           file://weston.ini \
           file://weston.service \
           file://weston.socket \
           file://weston-socket.sh \
           file://weston-autologin \
           file://weston-start"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xwayland', '', d)}"
PACKAGECONFIG:append:qemuriscv64 = " use-pixman"
PACKAGECONFIG:append:qemuppc64 = " use-pixman"

PACKAGECONFIG[xwayland] = ",,"
PACKAGECONFIG[no-idle-timeout] = ",,"
PACKAGECONFIG[use-pixman] = ",,"

DEFAULTBACKEND ??= ""
DEFAULTBACKEND:qemuall ?= "drm"

do_install() {
	# Install weston-start script
	if [ "${VIRTUAL-RUNTIME_init_manager}" != "systemd" ]; then
		install -Dm755 ${S}/weston-start ${D}${bindir}/weston-start
		sed -i 's,@DATADIR@,${datadir},g' ${D}${bindir}/weston-start
		sed -i 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${bindir}/weston-start
		install -Dm755 ${S}/init ${D}/${sysconfdir}/init.d/weston
		sed -i 's#ROOTHOME#${ROOT_HOME}#' ${D}/${sysconfdir}/init.d/weston
	fi

	# Install Weston systemd service
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -D -p -m0644 ${S}/weston.service ${D}${systemd_system_unitdir}/weston.service
		install -D -p -m0644 ${S}/weston.socket ${D}${systemd_system_unitdir}/weston.socket
		install -D -p -m0644 ${S}/weston-socket.sh ${D}${sysconfdir}/profile.d/weston-socket.sh
		sed -i -e s:/etc:${sysconfdir}:g \
			-e s:/usr/bin:${bindir}:g \
			-e s:/var:${localstatedir}:g \
			${D}${systemd_system_unitdir}/weston.service
	fi

	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
		install -D -p -m0644 ${S}/weston-autologin ${D}${sysconfdir}/pam.d/weston-autologin
	fi

	install -D -p -m0644 ${S}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini
	install -Dm644 ${S}/weston.env ${D}${sysconfdir}/default/weston

	if [ -n "${DEFAULTBACKEND}" ]; then
		sed -i -e "/^\[core\]/a backend=${DEFAULTBACKEND}-backend.so" ${D}${sysconfdir}/xdg/weston/weston.ini
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'xwayland', 'yes', 'no', d)}" = "yes" ]; then
		sed -i -e "/^\[core\]/a xwayland=true" ${D}${sysconfdir}/xdg/weston/weston.ini
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'no-idle-timeout', 'yes', 'no', d)}" = "yes" ]; then
		sed -i -e "/^\[core\]/a idle-time=0" ${D}${sysconfdir}/xdg/weston/weston.ini
	fi

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'use-pixman', 'yes', 'no', d)}" = "yes" ]; then
		sed -i -e "/^\[core\]/a use-pixman=true" ${D}${sysconfdir}/xdg/weston/weston.ini
	fi

	install -dm 755 -o weston -g weston ${D}/home/weston
}

INHIBIT_UPDATERCD_BBCLASS = "${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', '1', '', d)}"

inherit update-rc.d systemd useradd

USERADD_PACKAGES = "${PN}"

# rdepends on weston which depends on virtual/egl
#
require ${THISDIR}/required-distro-features.inc

RDEPENDS:${PN} = "weston kbd"

INITSCRIPT_NAME = "weston"
INITSCRIPT_PARAMS = "start 9 5 2 . stop 20 0 1 6 ."

FILES:${PN} += "\
    ${sysconfdir}/xdg/weston/weston.ini \
    ${sysconfdir}/profile.d/weston-socket.sh \
    ${systemd_system_unitdir}/weston.service \
    ${systemd_system_unitdir}/weston.socket \
    ${sysconfdir}/default/weston \
    ${sysconfdir}/pam.d/ \
    /home/weston \
    "

CONFFILES:${PN} += "${sysconfdir}/xdg/weston/weston.ini ${sysconfdir}/default/weston"

SYSTEMD_SERVICE:${PN} = "weston.service weston.socket"
USERADD_PARAM:${PN} = "--home /home/weston --shell /bin/sh --user-group -G video,input,render,wayland weston"
GROUPADD_PARAM:${PN} = "-r wayland; -r render"

