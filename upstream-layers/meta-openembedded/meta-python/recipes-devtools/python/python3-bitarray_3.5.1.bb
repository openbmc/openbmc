SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "b03c49d1a2eb753cc6090053f1c675ada71e1c3ea02011f1996cf4c2b6e9d6d6"

inherit python_setuptools_build_meta pypi

BBCLASSEXTEND = "native nativesdk"
