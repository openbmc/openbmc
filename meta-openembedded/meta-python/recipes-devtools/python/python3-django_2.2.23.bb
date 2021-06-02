require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "d72405637143e201b745714e300bb546"
SRC_URI[sha256sum] = "12cfc045a4ccb2348719aaaa77b17e66a26bff9fc238b4c765a3e825ef92e414"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
