SUMMARY = "The bidirectional mapping library for Python."
DESCRIPTION = "The bidirectional mapping library for Python."
HOMEPAGE = "https://bidict.readthedocs.io/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI[sha256sum] = "42c84ffbe6f8de898af6073b4be9ea7ccedcd78d3474aa844c54e49d5a079f6f"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
