DESCRIPTION = "Scripting support for flask"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e686048adb69341fc8a08caeda528b41"

SRC_URI[md5sum] = "3fbd91fe13cebedfb2431331f6eabb68"
SRC_URI[sha256sum] = "6425963d91054cfcc185807141c7314a9c5ad46325911bd24dcb489bd0161c65"

PYPI_PACKAGE = "Flask-Script"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask \
    "
