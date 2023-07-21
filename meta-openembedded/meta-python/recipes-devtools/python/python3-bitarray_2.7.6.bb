SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "3807f9323c308bc3f9b58cbe5d04dc28f34ac32d852999334da96b42f64b5356"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
