DESCRIPTION = "A database migration tool for SQLAlchemy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff111c41e8748bbfa45e8ba92347b681"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "83490def1f723413db5096591f7609ac52544805be93849170088ed04f5a2e0b"

RDEPENDS:${PN} += "\
    python3-dateutil \
    python3-editor \
    python3-mako \
    python3-sqlalchemy \
    python3-misc \
"

BBCLASSEXTEND = "native nativesdk"
