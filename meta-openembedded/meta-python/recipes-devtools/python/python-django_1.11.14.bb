require python-django.inc
inherit setuptools

SRC_URI[md5sum] = "38e82b59a1c27bbf98ccf0564ead7426"
SRC_URI[sha256sum] = "eb9271f0874f53106a2719c0c35ce67631f6cc27cf81a60c6f8c9817b35a3f6e"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-zlib \
"
