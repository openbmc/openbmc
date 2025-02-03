require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "0f6cbc56cc298b0451d20a5120c6a8731e9073330fb5d84295c23c151a1eb300"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
