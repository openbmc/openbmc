SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[md5sum] = "7cfa242b99351646fab0d76b05aab747"
SRC_URI[sha256sum] = "567631fc922b1c2c528c376795f18dcc0604d18702e0b8b50e8e35f0474214a5"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
