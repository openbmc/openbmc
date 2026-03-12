require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "3046a53b0e40d4b676c3b774c73411d7184ae2745fe8ce5e45c0f33d3ddb71a7"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
