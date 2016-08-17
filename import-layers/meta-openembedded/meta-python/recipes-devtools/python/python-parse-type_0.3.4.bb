SUMMARY = "Simplifies building parse types based on the parse module"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=be38402eb4b6c224e4914fd82e0e1436"
PYPI_PACKAGE = "parse_type"

SRC_URI[md5sum] = "00ba70bc5ca67624207b0dfced01dc8a"
SRC_URI[sha256sum] = "3dd0b323bafcb8c25e000ce5589042a1c99cba9c3bec77b9f591e46bc9606147"

RDEPENDS_${PN} += "python-parse"

inherit pypi setuptools
