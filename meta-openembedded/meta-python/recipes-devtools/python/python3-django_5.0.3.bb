require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "5fb37580dcf4a262f9258c1f4373819aacca906431f505e4688e37f3a99195df"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
