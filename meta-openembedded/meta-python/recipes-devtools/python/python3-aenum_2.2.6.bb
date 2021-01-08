SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[md5sum] = "52ec17f5efdfa13952657fe954cd6d99"
SRC_URI[sha256sum] = "260225470b49429f5893a195a8b99c73a8d182be42bf90c37c93e7b20e44eaae"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
