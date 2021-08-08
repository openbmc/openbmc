SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "b5d707d9c4aa75e684e21ff1848b234f3d2ff41d5038db89e2465e5527f90c68"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
