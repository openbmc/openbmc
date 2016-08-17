SUMMARY  = "A Python enumeration package."
DESCRIPTION = "The flufl.enum library is a Python enumeration package. Its goal is to provide simple, \
specific, concise semantics in an easy to read and write syntax. flufl.enum has just enough of the \
features needed to make enumerations useful, but without a lot of extra baggage to weigh them down. "

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://PKG-INFO;startline=8;endline=9;md5=250adb4c74c359b7049abcd9d6b115e7"

SRC_URI[md5sum] = "461779384c07ed2b5f554a5d51a000c4"
SRC_URI[sha256sum] = "94da1413ba085473652f81565847913ea1543d1739972ecbd6afe57d7b1b68b4"

PYPI_PACKAGE = "flufl.enum"
inherit pypi setuptools

PACKAGES =+ "\  
    ${PN}-test \
"

FILES_${PN}-doc += "\
    ${libdir}/${PYTHON_DIR}/site-packages/flufl/enum/*.rst \
    ${libdir}/${PYTHON_DIR}/site-packages/flufl/enum/docs \
"

FILES_${PN}-test += "${libdir}/${PYTHON_DIR}/site-packages/flufl/enum/tests"
