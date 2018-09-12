SUMMARY = "HXT Rep board wiring"
DESCRIPTION = "Board wiring information for the HXT Rep system."
PR = "r1"

inherit allarch
inherit setuptools
inherit pythonnative
inherit obmc-phosphor-license

PROVIDES += "virtual/obmc-inventory-data"
RPROVIDES_${PN} += "virtual-obmc-inventory-data"

DEPENDS += "python"

S = "${WORKDIR}"
SRC_URI += "file://Stardragon4800-rep2.py"

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

