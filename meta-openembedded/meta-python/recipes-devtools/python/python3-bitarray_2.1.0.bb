SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "97224a19325ecee49a3bf4df3ee0531d3af9cf288b67d089a7ef44a3c4ea3839"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
