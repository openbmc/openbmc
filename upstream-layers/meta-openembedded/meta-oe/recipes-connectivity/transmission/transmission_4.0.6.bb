DESCRIPTION = "Transmission is a fast, easy, and free BitTorrent client"
SECTION = "network"
HOMEPAGE = "https://transmissionbt.com/"
LICENSE = "MIT & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ba8199e739948e198310093de27175fa"

DEPENDS = "curl libevent libpsl gnutls openssl libtool intltool-native glib-2.0-native"
RDEPENDS:${PN}-web = "${PN}"

SRC_URI = " \
	gitsm://github.com/transmission/transmission;branch=4.0.x;protocol=https \
	file://0001-build-bump-CMake-version-to-3.10-4.patch;patchdir=third-party/dht \
	file://0001-bump-cmake-to-3.10.patch;patchdir=third-party/libb64 \
	file://0001-Increase-minimum-CMake-version-to-3.10.patch;patchdir=third-party/libdeflate \
	file://0001-miniupnpc-bump-CMake-version-to-3.14.patch;patchdir=third-party/miniupnpc \
	file://0001-build-set-minimum-required-CMake-to-3.5.patch;patchdir=third-party/libnatpmp \
	file://transmission-daemon \
"

# Transmission release 4.0.6
SRCREV = "38c164933e9f77c110b48fe745861c3b98e3d83e"


inherit cmake gettext update-rc.d pkgconfig systemd mime-xdg

LDFLAGS:append:riscv32 = " -latomic"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'gtk', '', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"

PACKAGECONFIG[gtk] = "-DENABLE_GTK=ON,-DENABLE_GTK=OFF,gtk4 gtkmm4,"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF,systemd,"

# Weak default values for transmission user and group
# Change them in bbappend if needed
TRANSMISSION_USER ??= "root"
TRANSMISSION_GROUP ??= "root"

do_install:append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		sed -i '/USERNAME=/c\USERNAME=${TRANSMISSION_USER}' ${UNPACKDIR}/transmission-daemon
		install -d ${D}${sysconfdir}/init.d
		install -m 0744 ${UNPACKDIR}/transmission-daemon ${D}${sysconfdir}/init.d/
		chown ${TRANSMISSION_USER}:${TRANSMISSION_GROUP} ${D}${sysconfdir}/init.d/transmission-daemon
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		sed -i '/User=/c\User=${TRANSMISSION_USER}' ${S}/daemon/transmission-daemon.service
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${S}/daemon/transmission-daemon.service ${D}${systemd_unitdir}/system
	fi
}

PACKAGES += "${PN}-gtk ${PN}-client ${PN}-web"

FILES:${PN}-client = "${bindir}/transmission-remote ${bindir}/transmission-cli ${bindir}/transmission-create ${bindir}/transmission-show ${bindir}/transmission-edit"
FILES:${PN}-gtk += "${bindir}/transmission-gtk ${datadir}/icons ${datadir}/applications ${datadir}/pixmaps ${datadir}/metainfo"
FILES:${PN}-web = "${datadir}/transmission/web ${datadir}/transmission/public_html"
FILES:${PN} = "${bindir}/transmission-daemon ${sysconfdir}/init.d/transmission-daemon ${datadir}/appdata"

SYSTEMD_SERVICE:${PN} = "transmission-daemon.service"

# Script transmission-daemon following the guidelines in:
# https://trac.transmissionbt.com/wiki/Scripts/initd
INITSCRIPT_PACKAGES = "transmission-daemon"
INITSCRIPT_NAME = "transmission-daemon"
INITSCRIPT_PARAMS = "start 99 5 3 2 . stop 10 0 1 6 ."
