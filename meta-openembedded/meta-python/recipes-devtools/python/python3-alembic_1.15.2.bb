DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2ee18d90dcc02d96b76e9e953629936"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1c72391bbdeffccfe317eefba686cb9a3c078005478885413b95c3b26c57a8a7"

RDEPENDS:${PN} += "\
    python3-dateutil \
    python3-editor \
    python3-mako \
    python3-sqlalchemy \
    python3-misc \
"

BBCLASSEXTEND = "native nativesdk"
