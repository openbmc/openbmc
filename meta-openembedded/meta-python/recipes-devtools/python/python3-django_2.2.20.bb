require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "947060d96ccc0a05e8049d839e541b25"
SRC_URI[sha256sum] = "2569f9dc5f8e458a5e988b03d6b7a02bda59b006d6782f4ea0fd590ed7336a64"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
