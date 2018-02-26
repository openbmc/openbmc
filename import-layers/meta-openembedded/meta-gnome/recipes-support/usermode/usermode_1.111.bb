DESCRIPTION = "The usermode contains the userhelper program, which can be used to allow configured \
programs to be run with superuser privileges by ordinary users, and several \
graphical tools for users: \
* userinfo allows users to change their finger information. \
* usermount lets users mount, unmount, and format filesystems. \
* userpasswd allows users to change their passwords. \
"
HOMEPAGE = "https://pagure.io/usermode"
LICENSE = "GPLv2+"
DEPENDS = "libuser ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
           gtk+ desktop-file-utils-native \
           startup-notification intltool-native \
           util-linux \
"

SRC_URI = "https://releases.pagure.org/${BPN}/${BPN}-${PV}.tar.xz \
           file://0001-formatting-issues.patch \
           file://0001-fix-compile-failure-against-musl-C-library.patch \
           file://0001-Makefile.am-Link-with-libm-for-powl-API.patch \
           "
SRC_URI[md5sum] = "28ba510fbd8da9f4e86e57d6c31cff29"
SRC_URI[sha256sum] = "3dd0b9639b5bd396b7ea5fada6aaa694dbfdaa3ad06eb95a6eabcdfd02f875c6"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit autotools gettext pkgconfig

EXTRA_OEMAKE += "INSTALL='install -p'"

