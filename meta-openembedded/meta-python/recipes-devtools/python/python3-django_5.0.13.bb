require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "f9d4b7b87a9dae248d5f20cec940cf7290e07d508d6d8432e3c2cabf09b3b0ff"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
