SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[md5sum] = "ccd872c5effef77bf4028c67c0619f6c"
SRC_URI[sha256sum] = "38fe66d5f7720835703a07fe8d0a4b5eda87f692f3cf4fb01543407b79857edf"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
