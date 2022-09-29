SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "56d3f16dd807b1c56732a244ce071c135ee973d3edc9929418c1b24c5439a0fd"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
