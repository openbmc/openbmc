SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "e02f79fba7a470d438eb39017d503498faaf760b17b6b46af1a9de12fd58d311"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
