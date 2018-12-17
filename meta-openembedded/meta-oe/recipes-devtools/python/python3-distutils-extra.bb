require python-distutils-extra.inc

inherit setuptools3

S = "${WORKDIR}/python-distutils-extra-${PV}"

RDEPENDS_${PN} = "\
    python3-setuptools \
"
