SUMMARY = "A Python library for creating editable wheels"
HOMEPAGE = "https://github.com/pfmoore/editables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d731fc2dd253a6e78010aabef22fa987"

SRC_URI[sha256sum] = "1163834902381c4613787951c5914800fdf155ae08848a373b8ea5006780977c"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-io"

BBCLASSEXTEND = "native nativesdk"
