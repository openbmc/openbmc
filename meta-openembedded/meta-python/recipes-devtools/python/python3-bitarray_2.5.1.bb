SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "8d38f60751008099a659d5acfb35ef4150183effd5b2bfa6c10199270ddf4c9c"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
