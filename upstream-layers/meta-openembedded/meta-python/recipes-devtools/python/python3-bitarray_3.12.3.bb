SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "919e35a951c0a551631b94bf568f3f5e38d004e0c60866cb2ca51bd8c4778fec"

inherit python_setuptools_build_meta pypi

BBCLASSEXTEND = "native nativesdk"
