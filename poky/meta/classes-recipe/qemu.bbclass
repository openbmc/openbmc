#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# This class contains functions for recipes that need QEMU or test for its
# existence.
#

def qemu_target_binary(data):
    return oe.qemu.qemu_target_binary(data)

def qemu_wrapper_cmdline(data, rootfs_path, library_paths):
    return oe.qemu.qemu_wrapper_cmdline(data, rootfs_path, library_paths)

def qemu_run_binary(data, rootfs_path, binary):
    return oe.qemu.qemu_run_binary(data, rootfs_path, binary)

# QEMU_EXTRAOPTIONS is not meant to be directly used, the extensions are
# PACKAGE_ARCH, *NOT* overrides.
# In some cases (e.g. ppc) simply being arch specific (apparently) isn't good
# enough and a PACKAGE_ARCH specific -cpu option is needed (hence we have to do
# this dance). For others (e.g. arm) a -cpu option is not necessary, since the
# qemu-arm default CPU supports all required architecture levels.
QEMU_OPTIONS = "-r ${OLDEST_KERNEL} ${@d.getVar("QEMU_EXTRAOPTIONS:tune-%s" % d.getVar('TUNE_PKGARCH')) or ""}"
QEMU_OPTIONS[vardeps] += "QEMU_EXTRAOPTIONS:tune-${TUNE_PKGARCH}"
