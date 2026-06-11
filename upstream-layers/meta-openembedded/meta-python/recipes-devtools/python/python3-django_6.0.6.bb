require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "ad03916ba59523d781ae5c3f631960c23d69a9d9c43cecda52fc23b47e953713"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
