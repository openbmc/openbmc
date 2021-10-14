SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "f19c62425576d3d1821ed711b94d1a4e5ede8f05ca121e99b6d978ed49c7a765"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
