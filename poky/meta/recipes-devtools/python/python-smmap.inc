SUMMARY = "Python implementation of a sliding window memory map manager"
DESCRIPTION = "A pure Python implementation of a sliding memory map to \
help unifying memory mapped access on 32 and 64 bit systems and to help \
managing resources more efficiently."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

inherit pypi

PYPI_PACKAGE = "smmap2"

SRC_URI[md5sum] = "1c59a985be5aa645c4c5a4e063a40dd5"
SRC_URI[sha256sum] = "29a9ffa0497e7f2be94ca0ed1ca1aa3cd4cf25a1f6b4f5f87f74b46ed91d609a"

RDEPENDS_${PN} += "${PYTHON_PN}-codecs \
                   ${PYTHON_PN}-mmap \
"
BBCLASSEXTEND = "native nativesdk"
