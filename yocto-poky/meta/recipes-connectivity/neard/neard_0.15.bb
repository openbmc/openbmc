SUMMARY = "Linux NFC daemon"
DESCRIPTION = "A daemon for the Linux Near Field Communication stack"
HOMEPAGE = "http://01.org/linux-nfc"
LICENSE = "GPLv2"

DEPENDS = "dbus glib-2.0 libnl"

SRC_URI = "${KERNELORG_MIRROR}/linux/network/nfc/${BP}.tar.xz \
           file://neard.in \
           file://Makefile.am-fix-parallel-issue.patch \
           file://Makefile.am-do-not-ship-version.h.patch \
          "
SRC_URI[md5sum] = "b746ce62eeef88e8de90765e00a75a1c"
SRC_URI[sha256sum] = "651f6513d32cdaf8a426255d03aff38a6620a89b0567ec2b36606c6330a93353"

LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
 file://src/near.h;beginline=1;endline=20;md5=358e4deefef251a4761e1ffacc965d13 \
 "

inherit autotools pkgconfig systemd update-rc.d bluetooth

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/ --with-systemduserunitdir=${systemd_unitdir}/user/,--disable-systemd"

EXTRA_OECONF += "--enable-tools"

# This would copy neard start-stop shell and test scripts
do_install_append() {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/init.d/
		sed "s:@installpath@:${libexecdir}/nfc:" ${WORKDIR}/neard.in \
		  > ${D}${sysconfdir}/init.d/neard
		chmod 0755 ${D}${sysconfdir}/init.d/neard
	fi

	# Install the tests for neard-tests
	install -d ${D}${libdir}/neard
	install -m 0755 ${S}/test/* ${D}${libdir}/${BPN}/
	install -m 0755 ${B}/tools/nfctool/nfctool ${D}${libdir}/${BPN}/
}

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests = "${libdir}/${BPN}/*-test"

RDEPENDS_${PN} = "dbus python python-dbus python-pygobject"

# Bluez & Wifi are not mandatory except for handover
RRECOMMENDS_${PN} = "\
                     ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)} \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'wifi','wpa-supplicant', '', d)} \
                    "

RDEPENDS_${PN}-tests = "python python-dbus python-pygobject"

INITSCRIPT_NAME = "neard"
INITSCRIPT_PARAMS = "defaults 64"

SYSTEMD_SERVICE_${PN} = "neard.service"
