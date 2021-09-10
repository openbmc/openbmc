SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "4bee3ba9164b66cef64f1099e9a3b88e99ddcd0c943807e99443613e184b48b4"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
