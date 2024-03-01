SUMMARY = "Better dates and times for Python"
HOMEPAGE = "https://github.com/crsmithdev/arrow"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=14a2e29a9d542fb9052d75344d67619d"

SRC_URI[sha256sum] = "d4540617648cb5f895730f1ad8c82a65f2dad0166f57b75f3ca54759c4d67a85"

inherit setuptools3 pypi

RDEPENDS:${PN} += " \
        python3-dateutil \
        "
