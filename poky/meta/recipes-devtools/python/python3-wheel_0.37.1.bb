SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "e9a504e793efbca1b8e0e9cb979a249cf4a0a7b5b8c9e8b65a5e39d49529c1c4"

inherit flit_core pypi setuptools3-base

SRC_URI += " file://0001-Backport-pyproject.toml-from-flit-backend-branch.patch"

DEPENDS:remove:class-native = "python3-pip-native"

do_install:class-native () {
    # We need to bootstrap python3-wheel-native
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    PYPA_WHEEL="${B}/dist/${PYPI_PACKAGE}-${PV}-*.whl"
    unzip -d ${D}${PYTHON_SITEPACKAGES_DIR} ${PYPA_WHEEL} || \
    bbfatal_log "Failed to install"

    # pip install would normally generate [project.scripts] in ${bindir}
    install -d ${D}/${bindir}
    cat << EOF >> ${D}/${bindir}/wheel
#!/bin/sh
'''exec' ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} "\$0" "\$@"
' '''
# -*- coding: utf-8 -*-
import re
import sys
from wheel.cli import main
if __name__ == '__main__':
    sys.argv[0] = re.sub(r'(-script\.pyw|\.exe)?$', '', sys.argv[0])
    sys.exit(main())
EOF
    chmod 0755 ${D}${bindir}/wheel
}

BBCLASSEXTEND = "native nativesdk"

