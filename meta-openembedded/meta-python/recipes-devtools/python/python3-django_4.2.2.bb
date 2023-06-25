require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "2a6b6fbff5b59dd07bef10bcb019bee2ea97a30b2a656d51346596724324badf"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
