require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "1a47f7a7a3d43ce64570d350e008d2949abe8c7e21737b351b6a1611277c6d89"

SRC_URI += "\
           file://0001-Fixed-35980-Updated-setuptools-to-normalize-package.patch \
"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
