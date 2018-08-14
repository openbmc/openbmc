SUMMARY = "A command line Linux program which mounts and unmounts removable devices"
HOMEPAGE = "http://ignorantguru.github.io/udevil/"

DEPENDS = "glib-2.0 \
    glib-2.0-native \
    intltool-native \
    udev \
"
RDEPENDS_${PN} = "udev bash"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools systemd

SRC_URI = "https://github.com/IgnorantGuru/udevil/raw/pkg/${PV}/udevil-${PV}.tar.xz \
    file://0001-udevil-0.4.3-fix-compile-with-gcc6.patch \
    file://0002-etc-Makefile.am-Use-systemd_unitdir-instead-of-libdi.patch \
"

SRC_URI[md5sum] = "dc1c489b603a0500a04dc7e1805ac1d9"
SRC_URI[sha256sum] = "ce8c51fd4d589cda7be56e75b42188deeb258c66fc911a9b3a70a3945c157739"

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

SYSTEMD_SERVICE_${PN} = "devmon@.service"
SYSTEMD_AUTO_ENABLE = "disable"
