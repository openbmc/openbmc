SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "ec3a4f6d711a79ed23aea9541638d3353dc3f083f293a13180b14b482e3e19ef"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
