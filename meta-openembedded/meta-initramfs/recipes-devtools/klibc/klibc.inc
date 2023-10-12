DESCRIPTION = "klibc is intended to be a minimalistic libc subset for \
use with initramfs.  It is deliberately written for small size, \
minimal entaglement, and portability, not speed."
SECTION = "libs"
LICENSE = "BSD-3-Clause & GPL-2.0-only & MIT & Zlib"
LIC_FILES_CHKSUM = "file://usr/klibc/LICENSE;md5=d75181f10e998c21eb147f6d2e43ce8b"
DEPENDS = "linux-libc-headers perl-native"

SRC_URI = "${KERNELORG_MIRROR}/linux/libs/klibc/2.0/klibc-${PV}.tar.xz \
           ${ARMPATCHES} \
           file://klcc-consider-sysroot.patch \
           file://klcc-cross-accept-clang-options.patch \
           file://0001-Define-in_-structs-for-non-glibc-system-libs.patch \
           file://0001-include-linux-sysinfo.h-directly.patch \
           file://0001-mkfifo-Implement-mkfifo.patch \
           file://0001-always-use-bfd-linker.patch \
           file://0001-arm-Do-not-set-a-fallback-march-and-mtune.patch \
           file://0001-klibc_2.0.4-add-kexec_file_load-syscall.patch \
           file://0001-klibc-add-getrandom-syscall.patch \
           file://0001-klibc-Kbuild-Accept-EXTRA_KLIBCAFLAGS.patch \
           file://cross-clang.patch \
           file://0001-workaround-for-overlapping-sections-in-binary.patch \
           "

ARMPATCHES ?= ""

ARMPATCHES:arm = " \
                  file://armv4-fix-v4bx.patch \
                 "
SRC_URI[sha256sum] = "d673a294f742d59368222ff5c38462d81098c55063799de6fb8a7ba3d4af0436"

S = "${WORKDIR}/klibc-${PV}"

OPTFLAGS = "${TUNE_CCARGS} -Os -fcommon"
OPTFLAGS:append:toolchain-clang = " -fno-builtin-bcmp"
OPTFLAGS:append:toolchain-clang:mipsarch = " -no-integrated-as"

PARALLEL_MAKE = ""
EXTRA_OEMAKE = "'KLIBCARCH=${KLIBC_ARCH}' \
                'CROSS_COMPILE=${TARGET_PREFIX}' \
                'KLIBCKERNELSRC=${STAGING_DIR_TARGET}${exec_prefix}' \
                'KLIBCLIBGCC=${STAGING_DIR_TARGET}${libdir}/${TARGET_SYS}/*/libgcc.a' \
                'prefix=${exec_prefix}' \
                'INSTALLROOT=${D}' \
                'INSTALLDIR=${libdir}/klibc' \
                'SHLIBDIR=${libdir}' \
                '${KLIBCTHUMB}' \
                'KLIBCOPTFLAGS=${OPTFLAGS}' \
                 V=1 \
                "

export FIX_ARMV4_EABI_BX = "${FIX_V4BX}"
KLIBCTHUMB = "${@['CONFIG_KLIBC_THUMB=n', 'CONFIG_KLIBC_THUMB=y'][(d.getVar('ARM_INSTRUCTION_SET') == 'thumb')]}"

do_configure () {
    ln -sf "${STAGING_DIR_TARGET}${exec_prefix}" linux
}

do_compile:prepend:toolchain-clang() {
    sed -i -e 's#$(KLIBCROSS)gcc#$(KLIBCROSS)clang#g' ${S}/Makefile
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} = "already-stripped"
INSANE_SKIP:libklibc-dev = "dev-elf"
KLIBC_ARCH = "${TARGET_ARCH}"
KLIBC_ARCH:aarch64 = "arm64"
KLIBC_ARCH:armeb = "arm"
KLIBC_ARCH:mipsel = "mips"
KLIBC_ARCH:mips64el = "mips64"
KLIBC_ARCH:x86 = "i386"
KLIBC_ARCH:x86-64 = "x86_64"
KLIBC_ARCH:powerpc = "ppc"
KLIBC_ARCH:powerpc64 = "ppc64"
KLIBC_ARCH:powerpc64le = "ppc64"
THIS_LIBKLIBC = "libklibc (= ${PV}-${PR})"
