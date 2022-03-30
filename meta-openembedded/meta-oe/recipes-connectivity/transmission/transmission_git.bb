DESCRIPTION = "Transmission is a fast, easy, and free BitTorrent client"
SECTION = "network"
HOMEPAGE = "https://transmissionbt.com/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=73f535ddffcf2a0d3af4f381f84f9b33"

DEPENDS = "curl libevent gnutls openssl libtool intltool-native glib-2.0-native"
RDEPENDS:${PN}-web = "${PN}"

SRC_URI = " \
	gitsm://github.com/transmission/transmission;branch=master;protocol=https \
	file://transmission-daemon \
"

# Transmission release 3.00
SRCREV = "bb6b5a062ee594dfd4b7a12a6b6e860c43849bfd"
PV = "3.00"

S = "${WORKDIR}/git"

inherit autotools-brokensep gettext update-rc.d pkgconfig systemd mime-xdg

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk', '', d)} \
                 ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"

PACKAGECONFIG[gtk] = " --with-gtk,--without-gtk,gtk+3,"
PACKAGECONFIG[systemd] = "--with-systemd,--without-systemd,systemd,"

# Weak default values for transmission user and group
# Change them in bbappend if needed
TRANSMISSION_USER ??= "root"
TRANSMISSION_GROUP ??= "root"

# Configure aborts with:
# config.status: error: po/Makefile.in.in was not created by intltoolize.
do_configure() {
	sed -i /AM_GLIB_GNU_GETTEXT/d ${S}/configure.ac
	cd ${S}
	./update-version-h.sh
	intltoolize --copy --force --automake
        aclocal
        libtoolize --automake --copy --force
        autoconf
        automake -a
        oe_runconf
}

do_install:append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		sed -i '/USERNAME=/c\USERNAME=${TRANSMISSION_USER}' ${WORKDIR}/transmission-daemon
		install -d ${D}${sysconfdir}/init.d
		install -m 0744 ${WORKDIR}/transmission-daemon ${D}${sysconfdir}/init.d/
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
FILES:${PN}-gtk += "${bindir}/transmission-gtk ${datadir}/icons ${datadir}/applications ${datadir}/pixmaps"
FILES:${PN}-web = "${datadir}/transmission/web"
FILES:${PN} = "${bindir}/transmission-daemon ${sysconfdir}/init.d/transmission-daemon ${datadir}/appdata"

SYSTEMD_SERVICE:${PN} = "transmission-daemon.service"

# Script transmission-daemon following the guidelines in:
# https://trac.transmissionbt.com/wiki/Scripts/initd
INITSCRIPT_PACKAGES = "transmission-daemon"
INITSCRIPT_NAME = "transmission-daemon"
INITSCRIPT_PARAMS = "start 99 5 3 2 . stop 10 0 1 6 ."
