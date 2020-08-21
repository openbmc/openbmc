SUMMARY = "Tool for writing very fast and very flexible scanners"
HOMEPAGE = "http://re2c.sourceforge.net/"
AUTHOR = "Marcus Börger <helly@users.sourceforge.net>"
SECTION = "devel"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=64eca4d8a3b67f9dc7656094731a2c8d"

SRC_URI = "https://github.com/skvadrik/re2c/releases/download/${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "89a9d7ee14be10e3779ea7b2c8ea4a964afce6e76b8dbcd5479940681db46d20"
UPSTREAM_CHECK_URI = "https://github.com/skvadrik/re2c/releases"

BBCLASSEXTEND = "native nativesdk"

inherit autotools
