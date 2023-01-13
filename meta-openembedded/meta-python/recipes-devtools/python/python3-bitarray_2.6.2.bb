SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "90bac83ba6c37ab5048b43e07eba7d0de12f301ad6641633656fa269618a7301"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
