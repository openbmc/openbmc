SUMMARY = "Pyiface is a package that exposes the network interfaces of the operating system in a easy to use and transparent way"
HOMEPAGE = "https://pypi.python.org/pypi/pyiface/"
SECTION = "devel/python"
LICENSE = "GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4fe869ee987a340198fb0d54c55c47f1"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "e231e5735d329c5b2d4fc8854f069fdaa5436d3ef91ed64ee49e41e3f5e8a3f5"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-fcntl \
    python3-io \
"
