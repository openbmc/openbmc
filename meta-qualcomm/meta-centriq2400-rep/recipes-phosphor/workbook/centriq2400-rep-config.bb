SUMMARY = "Qualcomm Rep board wiring"
DESCRIPTION = "Board wiring information for the Qualcomm Rep system."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${QUALCOMMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit setuptools
inherit pythonnative

PROVIDES += "virtual/obmc-inventory-data"
RPROVIDES_${PN} += "virtual-obmc-inventory-data"

DEPENDS += "python"

S = "${WORKDIR}"
SRC_URI += "file://Centriq2400-rep.py"

python() {
        machine = d.getVar('MACHINE', True).capitalize() + '.py'
        d.setVar('_config_in_skeleton', machine)
}

do_make_setup() {
        cp ${S}/${_config_in_skeleton} \
                ${S}/obmc_system_config.py
        cat <<EOF > ${S}/setup.py
from distutils.core import setup

setup(name='${BPN}',
    version='${PR}',
    py_modules=['obmc_system_config'],
    )
EOF
}

addtask make_setup after do_patch before do_configure

