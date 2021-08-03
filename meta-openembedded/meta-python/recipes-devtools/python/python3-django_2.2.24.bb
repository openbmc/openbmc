require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "ebf3bbb7716a7b11029e860475b9a122"
SRC_URI[sha256sum] = "3339ff0e03dee13045aef6ae7b523edff75b6d726adf7a7a48f53d5a501f7db7"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
