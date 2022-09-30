require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "032f8a6fc7cf05ccd1214e4a2e21dfcd6a23b9d575c6573cacc8c67828dbe642"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
