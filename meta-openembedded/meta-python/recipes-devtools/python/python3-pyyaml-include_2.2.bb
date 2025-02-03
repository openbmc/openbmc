SUMMARY = "Extending PyYAML with a custom constructor for including YAML files within YAML files"
HOMEPAGE = "https://github.com/tanbro/pyyaml-include"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS += "python3-setuptools-scm-native"
SRCREV = "c5be2b7dfc4aaa91e44dbeb6fa42adc80936ef9d"

SRC_URI = " \
            git://github.com/tanbro/pyyaml-include;protocol=https;branch=main \
          "

S = "${WORKDIR}/git"

inherit python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-pyyaml \
    python3-fsspec \
"
RDEPENDS:${PN}-ptest += " \
    python3-fsspec \
    python3-aiohttp \
    python3-requests \
"
BBCLASSEXTEND = "native nativesdk"
