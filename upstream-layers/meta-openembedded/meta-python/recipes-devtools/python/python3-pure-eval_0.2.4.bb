SUMMARY = "Safely evaluate AST nodes without side effects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "260c2774686e651b79f8b8e7fc9d80b3599ea6a66334b47d5f4abb69fc2c0ea1"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-numbers \
"
