#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

RUST_DEBUG_REMAP = "--remap-path-prefix=${WORKDIR}=${TARGET_DBGSRC_DIR} \
                    --remap-path-prefix=${TMPDIR}/work-shared=${TARGET_DBGSRC_DIR} \
"
KRUSTFLAGS = " ${RUST_DEBUG_REMAP}"
EXTRA_OEMAKE:append = " KRUSTFLAGS='${KRUSTFLAGS}'"

RUST_KERNEL_DEPENDS ?= "clang-native rust-native bindgen-cli-native"
DEPENDS += "${RUST_KERNEL_DEPENDS}"
RUST_KERNEL_TASK_DEPENDS ?=  "rust-native:do_populate_sysroot clang-native:do_populate_sysroot bindgen-cli-native:do_populate_sysroot"
do_kernel_configme[depends] += "${RUST_KERNEL_TASK_DEPENDS}"

do_kernel_configme:append () {
        oe_runmake -C ${S} O=${B} rustavailable
}

# Linux rust build infrastructure does not currently support ccache
# see https://github.com/Rust-for-Linux/linux/issues/1224
# Quick summary: There are 2 issues: $HOSTCC is not escaped and rustc expect a path (and not a command)
# More details in: https://lists.openembedded.org/g/openembedded-core/message/229336
# Disable ccache for kernel build if kernel rust support is enabled to workaround this.
CCACHE_DISABLE ?= "1"
