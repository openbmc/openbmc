#
# This class contains functions for recipes that need QEMU or test for its
# existence.
#

def qemu_target_binary(data):
    package_arch = data.getVar("PACKAGE_ARCH")
    qemu_target_binary = (data.getVar("QEMU_TARGET_BINARY_%s" % package_arch) or "")
    if qemu_target_binary:
        return qemu_target_binary

    target_arch = data.getVar("TARGET_ARCH")
    if target_arch in ("i486", "i586", "i686"):
        target_arch = "i386"
    elif target_arch == "powerpc":
        target_arch = "ppc"
    elif target_arch == "powerpc64":
        target_arch = "ppc64"
    elif target_arch == "powerpc64le":
        target_arch = "ppc64le"

    return "qemu-" + target_arch

def qemu_wrapper_cmdline(data, rootfs_path, library_paths):
    import string

    qemu_binary = qemu_target_binary(data)
    if qemu_binary == "qemu-allarch":
        qemu_binary = "qemuwrapper"

    qemu_options = data.getVar("QEMU_OPTIONS")    

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
def qemu_run_binary(data, rootfs_path, binary):
    libdir = rootfs_path + data.getVar("libdir", False)
    base_libdir = rootfs_path + data.getVar("base_libdir", False)

    return qemu_wrapper_cmdline(data, rootfs_path, [libdir, base_libdir]) + rootfs_path + binary

# QEMU_EXTRAOPTIONS is not meant to be directly used, the extensions are
# PACKAGE_ARCH, *NOT* overrides.
# In some cases (e.g. ppc) simply being arch specific (apparently) isn't good
# enough and a PACKAGE_ARCH specific -cpu option is needed (hence we have to do
# this dance). For others (e.g. arm) a -cpu option is not necessary, since the
# qemu-arm default CPU supports all required architecture levels.

QEMU_OPTIONS = "-r ${OLDEST_KERNEL} ${@d.getVar("QEMU_EXTRAOPTIONS_%s" % d.getVar('PACKAGE_ARCH')) or ""}"
QEMU_OPTIONS[vardeps] += "QEMU_EXTRAOPTIONS_${PACKAGE_ARCH}"

QEMU_EXTRAOPTIONS_ppce500v2 = " -cpu e500v2"
QEMU_EXTRAOPTIONS_ppce500mc = " -cpu e500mc"
QEMU_EXTRAOPTIONS_ppce5500 = " -cpu e500mc"
QEMU_EXTRAOPTIONS_ppc64e5500 = " -cpu e500mc"
QEMU_EXTRAOPTIONS_ppce6500 = " -cpu e500mc"
QEMU_EXTRAOPTIONS_ppc64e6500 = " -cpu e500mc"
QEMU_EXTRAOPTIONS_ppc7400 = " -cpu 7400"
QEMU_EXTRAOPTIONS_powerpc64le = " -cpu POWER8"
