# the binaries are statically linked against klibc
require recipes-kernel/kexec/kexec-tools.inc
SUMMARY = "Kexec tools, statically compiled against klibc"
SRC_URI[md5sum] = "92eff93b097475b7767f8c98df84408a"
SRC_URI[sha256sum] = "09e180ff36dee087182cdc939ba6c6917b6adbb5fc12d589f31fd3659b6471f2"

inherit klibc

FILESPATH =. "${FILE_DIRNAME}/kexec-tools-${PV}:"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/kernel/kexec/kexec-tools-${PV}.tar.gz"

SRC_URI += " \
            file://kexec-elf-rel.patch \
            file://kexec-syscall.patch \
            file://cflags_static.patch  \
            file://ifdown_errno.patch  \
            file://purgatory_flags.patch \
            file://purgatory_string.patch \
            file://sha256.patch \
            file://sysconf_nrprocessors.patch \
            file://fix-out-of-tree-build.patch \
            "

SRC_URI_append_arm = " file://arm_crashdump.patch"
SRC_URI_append_powerpc = " file://ppc__lshrdi3.patch"
SRC_URI_append_x86 = " file://x86_sys_io.patch file://x86_basename.patch file://x86_vfscanf.patch file://x86_kexec_test.patch"
SRC_URI_append_x86-64 = " file://x86_sys_io.patch file://x86_basename.patch file://x86_vfscanf.patch file://x86_kexec_test.patch"

S = "${WORKDIR}/kexec-tools-${PV}"

EXTRA_OECONF += "--without-zlib --without-lzma --without-xen"

CFLAGS += "-I${STAGING_DIR_HOST}${libdir}/klibc/include -I${STAGING_DIR_HOST}${libdir}/klibc/include/bits32"
CFLAGS_x86-64 += "-I${STAGING_DIR_HOST}${libdir}/klibc/include -I${STAGING_DIR_HOST}${libdir}/klibc/include/bits64"

PACKAGES =+ "kexec-klibc kdump-klibc"

FILES_kexec-klibc = "${sbindir}/kexec"
FILES_kdump-klibc = "${sbindir}/kdump"
