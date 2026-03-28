#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit module

DEPENDS += " rust-native"

RUST_DEBUG_REMAP = "--remap-path-prefix=${S}=${TARGET_DBGSRC_DIR} "
KRUSTFLAGS = " ${RUST_DEBUG_REMAP}"
EXTRA_OEMAKE:append = " KRUSTFLAGS='${KRUSTFLAGS}'"

python __anonymous() {
    if not bb.utils.contains('KERNEL_FEATURES', 'rust', True, False, d):
       raise bb.parse.SkipRecipe("Skipping rust-out-of-tree-module: 'rust' is not enabled in KERNEL_FEATURES")
}
