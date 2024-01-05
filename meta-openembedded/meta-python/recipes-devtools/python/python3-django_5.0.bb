require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "7d29e14dfbc19cb6a95a4bd669edbde11f5d4c6a71fdaa42c2d40b6846e807f7"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
    ${PYTHON_PN}-asgiref \
"
