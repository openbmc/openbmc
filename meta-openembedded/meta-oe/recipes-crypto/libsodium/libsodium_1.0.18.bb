SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=47203c753972e855179dfffe15188bee"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "3ca9ebc13b6b4735acae0a6a4c4f9a95"
SRC_URI[sha256sum] = "6f504490b342a4f8a4c4a02fc9b866cbef8622d5df4e5452b46be121e46636c1"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
