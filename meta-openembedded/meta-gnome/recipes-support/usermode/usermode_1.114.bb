SUMMARY = "Tools for certain user account management tasks"
DESCRIPTION = "The usermode contains the userhelper program, which can be used to allow configured \
programs to be run with superuser privileges by ordinary users, and several \
graphical tools for users: \
* userinfo allows users to change their finger information. \
* usermount lets users mount, unmount, and format filesystems. \
* userpasswd allows users to change their passwords. \
"
HOMEPAGE = "https://pagure.io/usermode"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "libuser ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
           gtk+ desktop-file-utils-native \
           startup-notification intltool-native \
           util-linux \
"

SRC_URI = "https://releases.pagure.org/${BPN}/${BPN}-${PV}.tar.xz \
           file://0001-fix-compile-failure-against-musl-C-library.patch \
           file://0001-Makefile.am-Link-with-libm-for-powl-API.patch \
           "
SRC_URI[sha256sum] = "e7f58712b12175965b3a21522052863a061f3f1a888df3ffbe713b434f80254f"

REQUIRED_DISTRO_FEATURES = "x11 pam"

inherit features_check autotools gettext pkgconfig

EXTRA_OEMAKE += "INSTALL='install -p'"

