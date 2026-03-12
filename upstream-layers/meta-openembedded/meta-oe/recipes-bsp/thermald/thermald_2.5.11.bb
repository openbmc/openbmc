SUMMARY = "Linux thermal daemon"

DESCRIPTION = "Thermal Daemon is a Linux daemon used to prevent the \
overheating of platforms. This daemon monitors temperature and applies \
compensation using available cooling methods."

HOMEPAGE = "https://github.com/intel/thermal_daemon"

DEPENDS = "dbus dbus-glib dbus-glib-native libxml2 glib-2.0 glib-2.0-native upower libevdev"
DEPENDS += "autoconf-archive-native"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea8831610e926e2e469075b52bf08848"

SRC_URI = "git://github.com/intel/thermal_daemon/;branch=master;protocol=https \
           "

SRCREV = "5269afcf3e021e4e1b672b4640a0358f4ae5821b"

inherit pkgconfig autotools systemd gtk-doc

# gtkdocsize runs before autotools do_configure and it copies gtk-doc.m4 and fails
# to copy becuase there is no m4 dir yet.
do_configure:prepend () {
	mkdir -p ${S}/m4
}

EXTRA_OECONF = " \
                 --with-systemdsystemunitdir=${systemd_system_unitdir} \
                 "

FILES:${PN} += "${datadir}/dbus-1"

SYSTEMD_SERVICE:${PN} = "thermald.service"

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

CONFFILES:${PN} = " \
                   ${sysconfdir}/thermald/thermal-conf.xml \
                   ${sysconfdir}/thermald/thermal-cpu-cdev-order.xml \
                  "

UPSTREAM_CHECK_URI = "https://github.com/intel/thermal_daemon/releases/"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(?!2\.15\.10$)\d+(\.\d+)+)"
