SUMMARY = "run tests in isolated forked subprocesses"
HOMEPAGE = "https://github.com/pytest-dev/pytest-forked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772fcdaca14b378878d05c7d857e6c3e"

SRC_URI[sha256sum] = "8b67587c8f98cbbadfdd804539ed5455b6ed03802203485dd2f53c1422d7440e"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-py \
    python3-pytest \
"
