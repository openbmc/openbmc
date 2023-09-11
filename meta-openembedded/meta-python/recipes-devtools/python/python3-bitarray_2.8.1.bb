SUMMARY = "A high-level Python efficient arrays of booleans -- C extension"
HOMEPAGE = "https://github.com/ilanschnell/bitarray"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2ad702cdcd49e8d2ac01d7e7d0810d2d"

SRC_URI[sha256sum] = "e68ceef35a88625d16169550768fcc8d3894913e363c24ecbf6b8c07eb02c8f3"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
