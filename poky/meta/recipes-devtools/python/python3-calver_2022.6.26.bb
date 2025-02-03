SUMMARY = "Setuptools extension for CalVer package versions"
HOMEPAGE = "https://github.com/di/calver"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = " \
    git://github.com/di/calver;branch=master;protocol=https \
    file://0001-setup.py-hard-code-version.patch \
"
SRCREV = "3268d8acf2c345f32a1c5f08ba25dc67f76cca81"

inherit python_setuptools_build_meta ptest-python-pytest

S = "${WORKDIR}/git"

RDEPENDS:${PN}-ptest += " \
    python3-pretend \
"

BBCLASSEXTEND = "native nativesdk"
