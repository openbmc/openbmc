SUMMARY = "Pyiface is a package that exposes the network interfaces of the operating system in a easy to use and transparent way"
SECTION = "devel/python"
HOMEPAGE = "https://pypi.python.org/pypi/pyiface/"
LICENSE = "GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4fe869ee987a340198fb0d54c55c47f1"

DEPENDS += "python3-setuptools-scm-native"

inherit setuptools3

SRC_URI = "git://github.com/bat-serjo/PyIface.git;protocol=https;branch=master"
SRCREV = "4557dbda96d2e4b1142c60603d4a27d007a9ffe6"
PV = "0.1.dev33+g4557dbd"

PIP_INSTALL_PACKAGE = "pyiface"

S = "${WORKDIR}/git"
