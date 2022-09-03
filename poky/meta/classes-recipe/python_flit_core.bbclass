#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python_pep517 python3native python3-dir setuptools3-base

DEPENDS += "python3 python3-flit-core-native"

python_flit_core_do_manual_build () {
    cd ${PEP517_SOURCE_PATH}
    nativepython3 -m flit_core.wheel --outdir ${PEP517_WHEEL_PATH} .
}
