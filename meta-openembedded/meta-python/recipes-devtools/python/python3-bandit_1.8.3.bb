SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI[sha256sum] = "f5847beb654d309422985c36644649924e0ea4425c76dec2e89110b87506193a"

DEPENDS = "python3-pbr-native python3-git python3-pbr python3-pyyaml python3-six python3-stevedore"

inherit setuptools3 pypi

RDEPENDS:${PN} += "\
        python3-git \
        python3-modules \
        python3-pbr \
        python3-pyyaml \
        python3-rich \
        python3-six \
        python3-stevedore \
        "
