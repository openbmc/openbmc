require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "6005d030df924f85088454c18bd35dcf082b058aa355ad3df520d8de9aef9f41"
SRC_URI += "file://0001-Revert-Bumped-minimum-setuptools-version-to-83.patch"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
