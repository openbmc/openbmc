require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "30c688af9b63c4800ef9b044e0dd4145"
SRC_URI[sha256sum] = "84f370f6acedbe1f3c41e1a02de44ac206efda3355e427139ecb785b5f596d80"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
