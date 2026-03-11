#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

def qemu_target_binary(d):
    package_arch = d.getVar("PACKAGE_ARCH")
    qemu_target_binary = (d.getVar("QEMU_TARGET_BINARY_%s" % package_arch) or "")
    if qemu_target_binary:
        return qemu_target_binary

    target_arch = d.getVar("TARGET_ARCH")
    if target_arch in ("i486", "i586", "i686"):
        target_arch = "i386"
    elif target_arch == "powerpc":
        target_arch = "ppc"
    elif target_arch == "powerpc64":
        target_arch = "ppc64"
    elif target_arch == "powerpc64le":
        target_arch = "ppc64le"

    return "qemu-" + target_arch

def qemu_wrapper_cmdline(d, rootfs_path, library_paths, qemu_options=None):
    import string

    package_arch = d.getVar("PACKAGE_ARCH")
    if package_arch == "all":
        return "false"

    qemu_binary = qemu_target_binary(d)
    if qemu_binary == "qemu-allarch":
        qemu_binary = "qemuwrapper"

    if qemu_options == None:
        qemu_options = d.getVar("QEMU_OPTIONS") or ""

    return "PSEUDO_UNLOAD=1 " + qemu_binary + " " + qemu_options + " -L " + rootfs_path\
            + " -E LD_LIBRARY_PATH=" + ":".join(library_paths) + " "

# Next function will return a string containing the command that is needed to
# to run a certain binary through qemu. For example, in order to make a certain
# postinstall scriptlet run at do_rootfs time and running the postinstall is
# architecture dependent, we can run it through qemu. For example, in the
# postinstall scriptlet, we could use the following:
#
# ${@qemu_run_binary(d, '$D', '/usr/bin/test_app')} [test_app arguments]
#
def qemu_run_binary(d, rootfs_path, binary):
    libdir = rootfs_path + d.getVar("libdir", False)
    base_libdir = rootfs_path + d.getVar("base_libdir", False)

    return qemu_wrapper_cmdline(d, rootfs_path, [libdir, base_libdir]) + rootfs_path + binary
