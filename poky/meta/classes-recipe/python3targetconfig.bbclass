#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python3native

EXTRA_PYTHON_DEPENDS ?= ""
EXTRA_PYTHON_DEPENDS:class-target = "python3"
DEPENDS:append = " ${EXTRA_PYTHON_DEPENDS}"

setup_target_config() {
        export _PYTHON_SYSCONFIGDATA_NAME="_sysconfigdata"
        export PYTHONPATH=${STAGING_LIBDIR}/python-sysconfigdata:$PYTHONPATH
        export PATH=${STAGING_EXECPREFIXDIR}/python-target-config/:$PATH
}

do_configure:prepend:class-target() {
        setup_target_config
}

do_compile:prepend:class-target() {
        setup_target_config
}

do_install:prepend:class-target() {
        setup_target_config
}

do_configure:prepend:class-nativesdk() {
        setup_target_config
}

do_compile:prepend:class-nativesdk() {
        setup_target_config
}

do_install:prepend:class-nativesdk() {
        setup_target_config
}
