SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "f90bb3c680804ec9630bcf8c0965e54b4de84d33b17d7da57c87c30f0c64c6f5"

inherit python_setuptools_build_meta pypi

BBCLASSEXTEND = "native nativesdk"
