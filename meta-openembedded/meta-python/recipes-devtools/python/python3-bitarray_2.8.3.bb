SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "e15587b2bdf18d32eb3ba25f5f5a51bedd0dc06b3112a4c53dab5e7753bc6588"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
