SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "87f0e9ef4f828578ab06af30e4d7944043bf4ecd3f4b7bd1cbe37e2173cde94a"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
