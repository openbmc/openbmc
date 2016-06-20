SUMMARY = "The PyPA recommended tool for installing Python packages"
sHOMEPAGEsss = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=25fba45109565f87de20bae85bc39452"

SRCNAME = "pip"
DEPENDS += "python3 python3-setuptools-native"

SRC_URI = " \
  http://pypi.python.org/packages/source/p/${SRCNAME}/${SRCNAME}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "5601c4323464add1482291634142894d"
SRC_URI[sha256sum] = "90112b296152f270cb8dddcd19b7b87488d9e002e8cf622e14c4da9c2f6319b1"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/pip"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils3

DISTUTILS_INSTALL_ARGS += "--install-lib=${D}${libdir}/${PYTHON_DIR}/site-packages"

do_install_prepend() {
    install -d ${D}/${libdir}/${PYTHON_DIR}/site-packages
}

# Use setuptools site.py instead, avoid shared state issue
do_install_append() {
    rm ${D}/${libdir}/${PYTHON_DIR}/site-packages/site.py
    rm ${D}/${libdir}/${PYTHON_DIR}/site-packages/__pycache__/site.cpython-*.pyc

    # Install as pip3 and leave pip2 as default
    rm ${D}/${bindir}/pip

    # Installed eggs need to be passed directly to the interpreter via a pth file
    echo "./${SRCNAME}-${PV}-py${PYTHON_BASEVERSION}.egg" > ${D}${PYTHON_SITEPACKAGES_DIR}/${SRCNAME}-${PV}.pth
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
