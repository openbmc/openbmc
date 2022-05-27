SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "5abed04adcd2031f6e714993d95223bf9ae85354c640c270b2ed6f46b83573ba"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
