SUMMARY = "Security oriented static analyser for python code."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

SRC_URI[sha256sum] = "72ce7bc9741374d96fb2f1c9a8960829885f1243ffde743de70a19cee353e8f3"

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
