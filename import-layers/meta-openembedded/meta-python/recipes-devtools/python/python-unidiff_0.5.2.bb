SUMMARY = "Unified diff parsing/metadata extraction library"
HOMEPAGE = "http://github.com/matiasb/python-unidiff"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c434b08ef42fea235bb019b5e5a97b3"

SRC_URI[md5sum] = "20dd70ba5a35bc95bf869322d6852227"
SRC_URI[sha256sum] = "344330ec3637e96b44dca77e086b205645b55648cf4d2b80fc673200f8a6a7e9"

inherit  pypi setuptools

U = "${D}${LIBDIR}${PYTHON_SITEPACKAGES_DIR}"

do_install_append (){
    mv ${U}/tests ${U}/unidiff/
}
