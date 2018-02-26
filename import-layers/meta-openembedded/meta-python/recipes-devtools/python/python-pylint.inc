SUMMARY="Pylint is a Python source code analyzer"
HOMEPAGE= "http://www.pylint.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

SRC_URI[md5sum] = "98a1ee985a3ebc876a20eb7577dbab68"
SRC_URI[sha256sum] = "1f65b3815c3bf7524b845711d54c4242e4057dd93826586620239ecdfe591fb1"

inherit pypi

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

RDEPENDS_${PN} += "${PYTHON_PN}-astroid \
                   ${PYTHON_PN}-isort \
                   ${PYTHON_PN}-numbers \
                   ${PYTHON_PN}-shell \
                   ${PYTHON_PN}-json \
                   ${PYTHON_PN}-pkgutil \
                   ${PYTHON_PN}-difflib \
                   ${PYTHON_PN}-netserver \
                  "

do_install_append(){
    rm ${D}${bindir}/pylint
    cat >> ${D}${bindir}/pylint <<EOF
#!/usr/bin/env ${PYTHON_PN}
from pylint import run_pylint
run_pylint()
EOF
    chmod 755 ${D}${bindir}/pylint
    sed -i -e 's:^#!/usr/bin/python:#!/usr/bin/env\ ${PYTHON_PN}:g' ${D}/${PYTHON_SITEPACKAGES_DIR}/pylint/test/data/ascript
}
