FILESEXTRAPATHS:prepend:gem5-arm64 := "${THISDIR}:"

#
# virtualization kmeta extra
#
SRC_URI:append:gem5-arm64 = " file://virtualization-kmeta-extra-gem5;type=kmeta;name=virtualization-kmeta-extra-gem5;destsuffix=virtualization-kmeta-extra-gem5"

# We need to turn off SVE support in the Linux kernel otherwise Xen is stopping
# Linux kernel with a coredump while trying to access XEN bit of CPACR1 core
# register.
LINUX_VIRTUALIZATION_DISABLE_ARM64_SVE:gem5-arm64 = "${@bb.utils.contains('DISTRO_FEATURES', \
                                         'xen', ' features/disable-arm64-sve.scc','',d)}"

KERNEL_FEATURES:append:gem5-arm64 = "${LINUX_VIRTUALIZATION_DISABLE_ARM64_SVE}"
