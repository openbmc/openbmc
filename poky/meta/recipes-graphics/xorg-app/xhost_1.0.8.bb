require xorg-app-common.inc

SUMMARY = "Server access control program for X"

DESCRIPTION = "The xhost program is used to add and delete host names or \
user names to the list allowed to make connections to the X server. In \
the case of hosts, this provides a rudimentary form of privacy control \
and security. Environments which require more sophisticated measures \
should implement the user-based mechanism or use the hooks in the \
protocol for passing other authentication data to the server."

LIC_FILES_CHKSUM = "file://COPYING;md5=8fbed71dddf48541818cef8079124199"
DEPENDS += "libxmu libxau"
PE = "1"

SRC_URI[md5sum] = "a48c72954ae6665e0616f6653636da8c"
SRC_URI[sha256sum] = "a2dc3c579e13674947395ef8ccc1b3763f89012a216c2cc6277096489aadc396"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
