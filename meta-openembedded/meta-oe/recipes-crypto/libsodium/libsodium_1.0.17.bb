SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47203c753972e855179dfffe15188bee"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "0f71e2680187a1558b5461e6879342c5"
SRC_URI[sha256sum] = "0cc3dae33e642cc187b5ceb467e0ad0e1b51dcba577de1190e9ffa17766ac2b1"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
