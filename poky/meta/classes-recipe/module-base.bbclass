#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit kernel-arch

# We do the dependency this way because the output is not preserved
# in sstate, so we must force do_compile to run (once).
do_configure[depends] += "make-mod-scripts:do_compile"

export OS = "${TARGET_OS}"
export CROSS_COMPILE = "${TARGET_PREFIX}"

# This points to the build artefacts from the main kernel build
# such as .config and System.map
# Confusingly it is not the module build output (which is ${B}) but
# we didn't pick the name.
export KBUILD_OUTPUT = "${STAGING_KERNEL_BUILDDIR}"

export KERNEL_VERSION = "${@oe.utils.read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}"
export LOCALVERSION = "${@oe.utils.read_file('${STAGING_KERNEL_BUILDDIR}/kernel-localversion')}"
KERNEL_OBJECT_SUFFIX = ".ko"

# kernel modules are generally machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

