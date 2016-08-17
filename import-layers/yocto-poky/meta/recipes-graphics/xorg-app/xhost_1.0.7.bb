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

SRC_URI[md5sum] = "f5d490738b148cb7f2fe760f40f92516"
SRC_URI[sha256sum] = "93e619ee15471f576cfb30c663e18f5bc70aca577a63d2c2c03f006a7837c29a"
