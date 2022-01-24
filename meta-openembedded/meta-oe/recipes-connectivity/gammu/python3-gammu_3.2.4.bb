SUMMARY = "Gammu bindings for Python"
DESCRIPTION ="Python bindings for the Gammu library."
HOMEPAGE = "https://wammu.eu/python-gammu/"
BUGRACKER = "https://github.com/gammu/python-gammu/issues"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PYPI_PACKAGE = "python-gammu"

inherit pypi setuptools3 pkgconfig

SRC_URI += "file://0001-setup.py-StrictVersion-packaging.version.patch"
SRC_URI[sha256sum] = "49fc70f01bc192c43ff3ec815e082df5261ea4c8d36a695e977734c4eb4df868"

S = "${WORKDIR}/python-gammu-${PV}"

DEPENDS += "gammu python3-packaging-native"

RDEPENDS:${PN} += "python3-asyncio python3-core python3-threading"

RRECOMMENDS:${PN} += "gammu"
