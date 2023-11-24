SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "f90b2f44b5b23364d5fbade2c34652e15b1fcfe813c46f828e008f68a709160f"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
