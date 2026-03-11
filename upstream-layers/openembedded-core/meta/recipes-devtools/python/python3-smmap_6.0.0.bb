SUMMARY = "Python implementation of a sliding window memory map manager"
DESCRIPTION = "A pure Python implementation of a sliding memory map to \
help unifying memory mapped access on 32 and 64 bit systems and to help \
managing resources more efficiently."
HOMEPAGE = "http://github.com/gitpython-developers/GitPython"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

inherit pypi setuptools3

PYPI_PACKAGE = "smmap"

SRC_URI[sha256sum] = "8d79028ea6cc131da5eab099a5d95a998d43c6779956fffe3b455040911076da"

RDEPENDS:${PN} += "python3-codecs \
                   python3-mmap \
"
BBCLASSEXTEND = "native nativesdk"
