SUMMARY = "Python 2 and 3 compatibility library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f00d4a50713fa859858dd9abaa35b21"

SRC_URI[md5sum] = "34eed507548117b2ab523ab14b2f8b55"
SRC_URI[sha256sum] = "105f8d68616f8248e24bf0e9372ef04d3cc10104f1980f54d57b2ce73a5ad56a"

do_compile_append() {
    export BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS}
    ${PYTHON} setup.py -q bdist_egg --dist-dir ./
}
do_install_append() {
    install -m 0644 ${S}/*.egg ${D}/${PYTHON_SITEPACKAGES_DIR}/
}

RDEPENDS_${PN} += "python-io"
