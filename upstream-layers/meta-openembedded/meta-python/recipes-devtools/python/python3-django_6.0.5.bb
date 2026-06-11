require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "bc6d6872e98a2864c836e42edd644b362db311147dd5aa8d5b82ba7a032f5269"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
