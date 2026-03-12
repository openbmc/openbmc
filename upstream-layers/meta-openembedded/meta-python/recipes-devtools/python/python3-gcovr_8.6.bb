DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ba06c93410cc51bafc66fa06456879bf"

SRC_URI = " \
    git://github.com/gcovr/gcovr.git;branch=main;protocol=https \
    file://0001-pyproject.toml-Support-newer-versions.patch \
"
SRCREV = "e01ad73582821b5f90e079482164f8e885121e57"


inherit python_hatchling

DEPENDS += " \
    python3-hatchling-native \
    python3-hatch-vcs-native \
    python3-hatch-fancy-pypi-readme-native \
"

RDEPENDS:${PN} += " \
    python3-colorlog \
    python3-jinja2 \
    python3-lxml \
    python3-multiprocessing \
    python3-pygments \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
