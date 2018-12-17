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
           file://0001-fix-compile-failure-against-musl-C-library.patch \
           file://0001-Makefile.am-Link-with-libm-for-powl-API.patch \
           file://0001-Missing-n-in-translated-string.patch \
           file://0001-Include-sys-sysmacros.h-for-major-minor.patch \
           "
SRC_URI[md5sum] = "a766a9f7600f573fb6de4655d4162196"
SRC_URI[sha256sum] = "37c4d667209da14082c08df6e48fe955d1532efebd5322f13f94683c6cc10370"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit distro_features_check autotools gettext pkgconfig

EXTRA_OEMAKE += "INSTALL='install -p'"

REQUIRED_DISTRO_FEATURES = "x11 pam"
