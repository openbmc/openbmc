DESCRIPTION = "A Flask extension adding a decorator for JSONP support"
HOMEPAGE = "https://github.com/CoryDolphin/flask-jsonpify"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.rst;md5=bd59445a234a0c8250b39178d42e3148"

PYPI_PACKAGE = "Flask-Jsonpify"

SRC_URI[md5sum] = "8a10e37942c43d93d107644a3fe77d98"
SRC_URI[sha256sum] = "8ac4c732aa5b11d9f6c2de58065d3b669f139518ca8f529bce943817e2fedbfb"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-flask"
