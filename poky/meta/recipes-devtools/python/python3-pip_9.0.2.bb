SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=25fba45109565f87de20bae85bc39452"

DEPENDS += "python3 python3-setuptools-native"

SRC_URI[md5sum] = "2fddd680422326b9d1fbf56112cf341d"
SRC_URI[sha256sum] = "88110a224e9d30e5d76592a0b2130ef10e7e67a6426e8617bb918fffbfe91fe5"

inherit pypi distutils3

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${PYTHON_SITEPACKAGES_DIR}"

do_install_prepend() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

# Use setuptools site.py instead, avoid shared state issue
do_install_append() {
    rm ${D}${PYTHON_SITEPACKAGES_DIR}/site.py
    rm ${D}${PYTHON_SITEPACKAGES_DIR}/__pycache__/site.cpython-*.pyc

    # Install as pip3 and leave pip2 as default
    rm ${D}/${bindir}/pip

    # Installed eggs need to be passed directly to the interpreter via a pth file
    echo "./${PYPI_PACKAGE}-${PV}-py${PYTHON_BASEVERSION}.egg" > ${D}${PYTHON_SITEPACKAGES_DIR}/${PYPI_PACKAGE}-${PV}.pth

    # Make sure we use /usr/bin/env python3
    for PYTHSCRIPT in `grep -rIl ${bindir} ${D}${bindir}/pip3*`; do
        sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
    done
}

RDEPENDS_${PN} = "\
  python3-compile \
  python3-io \
  python3-html \
  python3-json \
  python3-netserver \
  python3-setuptools \
  python3-unixadmin \
  python3-xmlrpc \
"

BBCLASSEXTEND = "native nativesdk"
