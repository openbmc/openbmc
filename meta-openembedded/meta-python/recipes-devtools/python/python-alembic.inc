DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=28aaf14a6592d14dbfb2f4abd597aa27"

SRC_URI[md5sum] = "69fb6b25923d4f1e3c392447b342e358"
SRC_URI[sha256sum] = "4a4811119efbdc5259d1f4c8f6de977b36ad3bcc919f59a29c2960c5ef9149e4"

PYPI_PACKAGE = "alembic"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-editor \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-sqlalchemy \
"
