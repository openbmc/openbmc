require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "45a747e1c5b3d6df1b141b1481e193b033fd1fdbda3ff52677dc81afdaacbaed"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
