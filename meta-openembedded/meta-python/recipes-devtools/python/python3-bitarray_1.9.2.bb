SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "d7a49d21ae04c5af195023b140800186ebf208e3a4fc5b21a1389531cb7a7170"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
