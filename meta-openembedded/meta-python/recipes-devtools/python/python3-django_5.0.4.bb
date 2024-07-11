require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "4bd01a8c830bb77a8a3b0e7d8b25b887e536ad17a81ba2dce5476135c73312bd"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
