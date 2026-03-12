SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "3eae38daffd77c9621ae80c16932eea3fb3a4af141fb7cc724d4ad93eff9210d"

inherit python_setuptools_build_meta pypi

BBCLASSEXTEND = "native nativesdk"
