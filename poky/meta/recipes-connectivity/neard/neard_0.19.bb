SUMMARY = "Linux NFC daemon"
DESCRIPTION = "A daemon for the Linux Near Field Communication stack"
HOMEPAGE = "http://01.org/linux-nfc"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://src/near.h;beginline=1;endline=20;md5=358e4deefef251a4761e1ffacc965d13 \
                   "

DEPENDS = "dbus glib-2.0 libnl autoconf-archive-native"

SRC_URI = "git://git.kernel.org/pub/scm/network/nfc/neard.git;protocol=https;branch=master \
           file://neard.in \
           file://Makefile.am-fix-parallel-issue.patch \
           file://Makefile.am-do-not-ship-version.h.patch \
           file://0001-Add-header-dependency-to-nciattach.o.patch \
          "

SRCREV = "a1dc8a75cba999728e154a0f811ab9dd50c809f7"

S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd update-rc.d

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_system_unitdir}/ --with-systemduserunitdir=${systemd_unitdir}/user/,--disable-systemd"

EXTRA_OECONF += "--enable-tools"

# This would copy neard start-stop shell and test scripts
do_install:append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/init.d/
		sed "s:@installpath@:${libexecdir}/nfc:" ${WORKDIR}/neard.in \
		  > ${D}${sysconfdir}/init.d/neard
		chmod 0755 ${D}${sysconfdir}/init.d/neard
	fi
}

RDEPENDS:${PN} = "dbus"

# Bluez & Wifi are not mandatory except for handover
RRECOMMENDS:${PN} = "\
                     ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez5', '', d)} \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'wifi','wpa-supplicant', '', d)} \
                    "

INITSCRIPT_NAME = "neard"
INITSCRIPT_PARAMS = "defaults 64"

SYSTEMD_SERVICE:${PN} = "neard.service"
