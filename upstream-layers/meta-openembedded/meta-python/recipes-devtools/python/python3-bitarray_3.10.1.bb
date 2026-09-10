SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "c33e48906407ab3d0edb96cc5ab2a599bda5dd04704ebcd9b3e0eedce7310e0a"

inherit python_setuptools_build_meta pypi

BBCLASSEXTEND = "native nativesdk"
