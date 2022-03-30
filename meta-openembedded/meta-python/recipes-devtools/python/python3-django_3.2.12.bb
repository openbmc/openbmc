require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "9772e6935703e59e993960832d66a614cf0233a1c5123bc6224ecc6ad69e41e2"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
