require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "8c8659665bc6e3a44fefe1ab0a291e5a3fb3979f9a8230be29de975e57e8f854"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
