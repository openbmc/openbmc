DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff111c41e8748bbfa45e8ba92347b681"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "1212aa3778626f2b0f0aa6dd4e99a5f99b94bd25a0c1ac0bba3be65e081e50b0"

RDEPENDS:${PN} += "\
    python3-dateutil \
    python3-editor \
    python3-mako \
    python3-sqlalchemy \
    python3-misc \
"

BBCLASSEXTEND = "native nativesdk"
