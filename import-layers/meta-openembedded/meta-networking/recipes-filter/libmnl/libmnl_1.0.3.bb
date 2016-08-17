SUMMARY = "Minimalistic user-space Netlink utility library"
DESCRIPTION = "Minimalistic user-space library oriented to Netlink developers, providing \
    functions for common tasks in parsing, validating, and constructing both the Netlink header and TLVs."
HOMEPAGE = "http://www.netfilter.org/projects/libmnl/index.html"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://www.netfilter.org/projects/libmnl/files/libmnl-${PV}.tar.bz2;name=tar"
SRC_URI[tar.md5sum] = "7d95fc3bea3365bc03c48e484224f65f"
SRC_URI[tar.sha256sum] = "6f14336e9acdbc62c2dc71bbb59ce162e54e9af5c80153e92476c5443fe784de"

inherit autotools pkgconfig
