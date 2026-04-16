require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "8cfa2572b3f2768b2e84983cf3c4811877a01edb64e817986ec5d60751c113ac"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
