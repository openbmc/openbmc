SUMMARY = "Advanced Enumerations library"
HOMEPAGE = "https://pypi.org/project/aenum/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://aenum/LICENSE;md5=c6a85477543f8b8591b9c1f82abebbe9"

SRC_URI[md5sum] = "8983562361efe5be865617341dadbb9b"
SRC_URI[sha256sum] = "81828d1fbe20b6b188d75b21a0fa936d7d929d839ef843ef385d9c2a97082864"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
