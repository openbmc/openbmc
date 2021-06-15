SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
AUTHOR = "Scott Griffiths <dr.scottgriffiths@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d973e8e0c311da41424357236b7b8908"

SRC_URI[md5sum] = "18661a3e5c47c59bd926dd0fefae0baa"
SRC_URI[sha256sum] = "fdf3eb72b229d2864fb507f8f42b1b2c57af7ce5fec035972f9566de440a864a"

PYPI_PACKAGE = "bitstring"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-mmap \
"

BBCLASSEXTEND = "native nativesdk"
