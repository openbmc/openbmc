SUMMARY = "run tests in isolated forked subprocesses"
HOMEPAGE = "https://github.com/pytest-dev/pytest-forked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=772fcdaca14b378878d05c7d857e6c3e"

SRC_URI += "file://0001-Fix-report-serialization-with-pytest-9.2.patch"

SRC_URI[sha256sum] = "00f2bee51612f29b8e6b81eed2c3b2975e824c2693394f5bdaf7a1369078ba5f"

inherit pypi python_setuptools_build_meta

PEP517_BUILD_OPTS = "--skip-dependency-check"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-multiprocessing \
    python3-pytest \
"
