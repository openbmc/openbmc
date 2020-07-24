SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[md5sum] = "dfb3fe66ae989e4724747fcb0f704f56"
SRC_URI[sha256sum] = "9e26d3dc7fad932fed66e08da3fcf93dd15e8402aa84e764e1e2a9e1b6ae2b7f"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
