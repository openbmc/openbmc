SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "5ed37f0482199de5a3e096405a646ab3f45dd020d234c42539b3c11ea0e2278d"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
