require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "110fb58fb12eca59e072ad59fc42d771cd642dd7a2f2416582aa9da7a8ef954a"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 4.x branch, 
# PREFERRED_VERSION_python3-django = "4.0.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
