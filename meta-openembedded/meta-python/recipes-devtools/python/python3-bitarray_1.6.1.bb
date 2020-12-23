SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[md5sum] = "a082075dbae478fb53c69e25cc7f9cd6"
SRC_URI[sha256sum] = "ab85b38365dd9956264226b30dababa02161ed49bb36c7ee82cc6545e07b1599"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
