SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "a8f286a51a32323715d77755ed959f94bef13972e9a2fe71b609e40e6d27957e"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
