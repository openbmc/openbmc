SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "0edf630a4471a48627aec0b840cf3b8e10901191d328f6511560420459de282e"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
