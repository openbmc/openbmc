DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=28aaf14a6592d14dbfb2f4abd597aa27"

SRC_URI[md5sum] = "b8603f4c9bac851570bd1798e8f23982"
SRC_URI[sha256sum] = "cdb7d98bd5cbf65acd38d70b1c05573c432e6473a82f955cdea541b5c153b0cc"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
