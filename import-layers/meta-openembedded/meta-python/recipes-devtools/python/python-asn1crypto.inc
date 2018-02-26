DESCRIPTION = "Python binding to the Networking and Cryptography (NaCl) library"
HOMEPAGE = "https://github.com/pyca/pynacl"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=52010cd3c7d7bd965b55721ef4d93ec2"

PYPI_PACKAGE = "asn1crypto"

SRC_URI[md5sum] = "97d54665c397b72b165768398dfdd876"
SRC_URI[sha256sum] = "0874981329cfebb366d6584c3d16e913f2a0eb026c9463efcc4aaf42a9d94d70"

inherit pypi
BBCLASSEXTEND = "native"
