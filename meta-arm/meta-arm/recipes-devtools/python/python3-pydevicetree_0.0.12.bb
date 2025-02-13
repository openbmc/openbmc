SUMMARY = "Python 3 library for parsing, querying, and modifying Devicetree Source v1 files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=23f9ad5cad3d8cc0336e2a5d8a87e1fa"
HOMEPAGE = "https://github.com/sifive/pydevicetree"

inherit pypi setuptools3

SRC_URI[sha256sum] = "1ca19ec38bec0095a0a8dc4753a578e8c6e9a70873112547d30c92b32411f667"

BBCLASSEXTEND = "native nativesdk"
