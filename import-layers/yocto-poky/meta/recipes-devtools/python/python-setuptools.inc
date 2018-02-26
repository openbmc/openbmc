SUMMARY = "Download, build, install, upgrade, and uninstall Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"
SECTION = "devel/python"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=19;md5=9a33897f1bca1160d7aad3835152e158"

SRCNAME = "setuptools"

SRC_URI = "https://files.pythonhosted.org/packages/source/s/${SRCNAME}/${SRCNAME}-${PV}.zip"

SRC_URI[md5sum] = "b9e6c049617bac0f9e908a41ab4a29ac"
SRC_URI[sha256sum] = "b0fe5d432d922df595e918577c51458d63f245115d141b309ac32ecfca329df5"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/setuptools"

S = "${WORKDIR}/${SRCNAME}-${PV}"

do_install_prepend() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
}
