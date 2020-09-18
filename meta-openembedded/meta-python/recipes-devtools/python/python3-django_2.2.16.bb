require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "93faf5bbd54a19ea49f4932a813b9758"
SRC_URI[sha256sum] = "62cf45e5ee425c52e411c0742e641a6588b7e8af0d2c274a27940931b2786594"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
