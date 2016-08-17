DESCRIPTION = "Brcrypt hashing for Flask."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ee2ee5bee7fe96597770e92db5719a8"

SRC_URI[md5sum] = "d345c36ac6637d3ca9fa942e238d00ca"
SRC_URI[sha256sum] = "d71c8585b2ee1c62024392ebdbc447438564e2c8c02b4e57b56a4cafd8d13c5f"

PYPI_PACKAGE = "Flask-Bcrypt"

inherit pypi setuptools

RDEPENDS_${PN} = "python-bcrypt"
