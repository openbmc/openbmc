DESCRIPTION = "generate GCC code coverage reports"
HOMEPAGE = "https://gcovr.com"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ae27363fce24765bc79a095313a3b002"

SRC_URI = " \
    git://github.com/gcovr/gcovr.git;branch=main;protocol=https \
    file://0001-pyproject.toml-Support-newer-versions.patch \
"
SRCREV = "fe536afac4da31e86909191ef31708755ab8cf83"

S = "${WORKDIR}/git"

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
