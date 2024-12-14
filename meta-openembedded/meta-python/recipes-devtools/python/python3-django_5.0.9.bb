require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "6333870d342329b60174da3a60dbd302e533f3b0bb0971516750e974a99b5a39"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
