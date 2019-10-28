require python-pyconnman.inc

inherit setuptools3

SRC_URI_append = " \
    file://0001-Import-local-modules-by-relative-path-for-python3-su.patch \
"

RDEPENDS_${PN} += "python3-dbus python3-pprint"
