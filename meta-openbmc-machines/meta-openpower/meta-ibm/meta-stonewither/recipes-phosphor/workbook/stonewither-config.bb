SUMMARY = "Stonewither board wiring"
DESCRIPTION = "Board wiring information for the Stonewither OpenPOWER system."
PR = "r1"

inherit config-in-skeleton

#Use Witherspoon's config
do_make_setup() {
        cp ${S}/Witherspoon.py \
                ${S}/obmc_system_config.py
        cat <<EOF > ${S}/setup.py
from distutils.core import setup

setup(name='${BPN}',
    version='${PR}',
    py_modules=['obmc_system_config'],
    )
EOF
}
