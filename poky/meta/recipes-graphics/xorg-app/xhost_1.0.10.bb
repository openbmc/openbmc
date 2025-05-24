require xorg-app-common.inc

SUMMARY = "Server access control program for X"

DESCRIPTION = "The xhost program is used to add and delete host names or \
user names to the list allowed to make connections to the X server. In \
the case of hosts, this provides a rudimentary form of privacy control \
and security. Environments which require more sophisticated measures \
should implement the user-based mechanism or use the hooks in the \
protocol for passing other authentication data to the server."

LIC_FILES_CHKSUM = "file://COPYING;md5=b1f81049109f21bb3c365d9f42f79f3b"
DEPENDS += "libxmu libxau gettext-native"
PE = "1"

SRC_URI_EXT = "xz"

SRC_URI[sha256sum] = "a8afd70059479c712948b895e41c35a4a8bfcede3ba2d5a4b855c88bbb725be1"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
