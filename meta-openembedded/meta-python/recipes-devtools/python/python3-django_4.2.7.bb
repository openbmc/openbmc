require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "8e0f1c2c2786b5c0e39fe1afce24c926040fad47c8ea8ad30aaf1188df29fc41"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
