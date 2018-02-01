SUMMARY = "Parser of the C language, written in pure Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0cfca7bbc0610f849707d9e4fd82eac"

SRC_URI[md5sum] = "ca98dcb50bc1276f230118f6af5a40c7"
SRC_URI[sha256sum] = "0aac31e917c24cb3357f5a4d5566f2cc91a19ca41862f6c3c22dc60a629673b6"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-subprocess \
    cpp \
    cpp-symlinks \
    "
