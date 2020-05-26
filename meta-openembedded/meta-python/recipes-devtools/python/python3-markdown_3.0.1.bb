SUMMARY = "A Python implementation of John Gruber's Markdown."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=745aaad0c69c60039e638bff9ffc59ed"

inherit pypi setuptools3

PYPI_PACKAGE = "Markdown"
PYPI_SRC_URI = "https://files.pythonhosted.org/packages/3c/52/7bae9e99a7a4be6af4a713fe9b692777e6468d28991c54c273dfb6ec9fb2/Markdown-${PV}.tar.gz"
SRC_URI[md5sum] = "72219f46ca440b657bf227500731bdf1"
SRC_URI[sha256sum] = "d02e0f9b04c500cde6637c11ad7c72671f359b87b9fe924b2383649d8841db7c"

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "${PYTHON_PN}-logging ${PYTHON_PN}-setuptools"
