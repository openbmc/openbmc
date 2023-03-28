SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "f71256a32609b036adad932e1228b66a6b4e2cae6be397e588ddc0babd9a78b9"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
