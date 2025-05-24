SUMMARY = "efficient arrays of booleans -- C extension"
DESCRIPTION = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=6abe80c028e4ee53045a33ae807c64fd"

SRC_URI[sha256sum] = "8c89219a672d0a15ab70f8a6f41bc8355296ec26becef89a127c1a32bb2e6345"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
