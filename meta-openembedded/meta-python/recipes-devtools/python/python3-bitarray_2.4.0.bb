SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "f1203e902d51df31917d77eeba9c3fe78d032873a2ad78c737e26420f0080e58"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
