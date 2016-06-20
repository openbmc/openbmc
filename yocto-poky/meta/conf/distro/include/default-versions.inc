#
# Default preferred versions
#

# Force the python versions in one place
PYTHON_BASEVERSION ?= "2.7"
PREFERRED_VERSION_python ?= "2.7.11"
PREFERRED_VERSION_python-native ?= "2.7.11"

# Force the older version of liberation-fonts until we fix the fontforge issue
PREFERRED_VERSION_liberation-fonts ?= "1.04"

# Force db-native's version to keep sync with db while
# 'AGPL-3.0' in ${INCOMPATIBLE_LICENSE} blacklist
PREFERRED_VERSION_db-native = "${@incompatible_license_contains('AGPL-3.0', '5.%', '6.%', d)}"
