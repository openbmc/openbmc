SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "cd69a926a3363e25e94a64408303283c59085be96d71524bdbe6bfc8da2e34e0"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
