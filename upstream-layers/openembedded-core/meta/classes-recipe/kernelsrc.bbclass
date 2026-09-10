#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

S = "${STAGING_KERNEL_DIR}"
deltask do_fetch
deltask do_unpack
do_patch[depends] += "virtual/kernel:do_shared_workdir"
do_patch[noexec] = "1"
do_package[depends] += "virtual/kernel:do_populate_sysroot"
KERNEL_VERSION = "${@oe.kernel.get_version_file("${STAGING_KERNEL_BUILDDIR}")}"
LOCAL_VERSION = "${@oe.kernel.get_localversion_file("${STAGING_KERNEL_BUILDDIR}")}"

# The final packages get the kernel version instead of the default 1.0
python do_package:prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION").split("-")[0])
}
