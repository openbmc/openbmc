require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "bceb0fe1a386781af0788cae4108622756cd05e7775448deec04a71ddf87685d"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
