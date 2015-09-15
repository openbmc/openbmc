SUMMARY = "The PyPA recommended tool for installing Python packages"
sHOMEPAGEsss = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=45665b53032c02b35e29ddab8e61fa91"

SRCNAME = "pip"
DEPENDS += "python3 python3-setuptools-native"

SRC_URI = " \
  http://pypi.python.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "6b19e0a934d982a5a4b798e957cb6d45"
SRC_URI[sha256sum] = "89f3b626d225e08e7f20d85044afa40f612eb3284484169813dc2d0631f2a556"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils3

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${libdir}/${PYTHON_DIR}/site-packages"

do_install_prepend() {
    install -d ${D}/${libdir}/${PYTHON_DIR}/site-packages
}

# Use setuptools site.py instead, avoid shared state issue
do_install_append() {
    rm ${D}/${libdir}/${PYTHON_DIR}/site-packages/site.py
    rm ${D}/${libdir}/${PYTHON_DIR}/site-packages/__pycache__/site.cpython-34.pyc
}

RDEPENDS_${PN} = "\
  python3-compile \
  python3-io \
  python3-json \
  python3-netserver \
  python3-setuptools \
  python3-unixadmin \
  python3-xmlrpc \
"
