SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[sha256sum] = "17cd8cfed1ee4b617198c9fabbabd70ebd8f01e54ac29cd6c3a92df14bd86656"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
