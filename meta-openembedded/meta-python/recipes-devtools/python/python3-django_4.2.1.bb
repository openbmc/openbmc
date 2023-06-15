require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "7efa6b1f781a6119a10ac94b4794ded90db8accbe7802281cd26f8664ffed59c"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 4.x branch, 
# PREFERRED_VERSION_python3-django = "4.0.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
