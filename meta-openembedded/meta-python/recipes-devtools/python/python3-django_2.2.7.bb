require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "b0833024aac4c8240467e4dc91a12e9b"
SRC_URI[sha256sum] = "16040e1288c6c9f68c6da2fe75ebde83c0a158f6f5d54f4c5177b0c1478c5b86"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
