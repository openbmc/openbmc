SUMMARY = "Unified diff parsing/metadata extraction library"
HOMEPAGE = "http://github.com/matiasb/python-unidiff"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4c434b08ef42fea235bb019b5e5a97b3"

SRC_URI[md5sum] = "31a61729e64ee409135a2ccec80c7104"
SRC_URI[sha256sum] = "79b4be3e5479dfc6d77747d23ec98200559ca6a842fad1f92c2a7eb56b99195b"

inherit  pypi setuptools

U = "${D}${LIBDIR}${PYTHON_SITEPACKAGES_DIR}"

do_install_append (){
    mv ${U}/tests ${U}/unidiff/
}
