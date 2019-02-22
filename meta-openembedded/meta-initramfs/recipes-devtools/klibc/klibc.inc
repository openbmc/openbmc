DESCRIPTION = "klibc is intended to be a minimalistic libc subset for \
use with initramfs.  It is deliberately written for small size, \
minimal entaglement, and portability, not speed."
SECTION = "libs"
LICENSE = "BSD-3-Clause & GPL-2.0 & MIT & Zlib"
LIC_FILES_CHKSUM = "file://usr/klibc/LICENSE;md5=d75181f10e998c21eb147f6d2e43ce8b"
DEPENDS = "linux-libc-headers perl-native"
SRCREV = "4d19974d7020488f63651244e1f9f51727c3f66c"

SRC_URI = "git://git.kernel.org/pub/scm/libs/klibc/klibc.git \
           ${ARMPATCHES} \
           file://klibc-linux-libc-dev.patch \
           file://staging.patch \
           file://klcc-consider-sysroot.patch \
           file://klcc-cross-accept-clang-options.patch \
           file://0001-dash-Specify-format-string-in-fmtstr.patch \
           file://0001-Define-in_-structs-for-non-glibc-system-libs.patch \
           file://0001-include-linux-sysinfo.h-directly.patch \
           file://0001-mkfifo-Implement-mkfifo.patch \
           file://0001-always-use-bfd-linker.patch \
           file://0001-Kbuild.klibc-Use-print-libgcc-file-name-instead-of-p.patch \
           file://0001-Kbuild.klibc-Add-path-to-compiler-headers-via-isyste.patch \
           file://0001-arm-Do-not-set-a-fallback-march-and-mtune.patch \
           file://0001-klibc_2.0.4-add-kexec_file_load-syscall.patch \
           file://0001-klibc-add-getrandom-syscall.patch \
           file://0001-klibc-Kbuild-Accept-EXTRA_KLIBCAFLAGS.patch \
           file://0001-Define-ulocked_-fgets-fread-fwrite-aliases.patch \
           "

ARMPATCHES ?= ""

ARMPATCHES_arm = "file://klibc-config-eabi.patch \
                  file://armv4-fix-v4bx.patch \
                 "

S = "${WORKDIR}/git"

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
                'KLIBCOPTFLAGS=${TUNE_CCARGS} -Os' \
                 V=1 \
                "
EXTRA_OEMAKE += "'EXTRA_KLIBCAFLAGS=${SECURITY_CFLAGS} -Wa,--noexecstack' \
                 'EXTRA_KLIBCLDFLAGS=${SECURITY_LDFLAGS} -z noexecstack'"

export FIX_ARMV4_EABI_BX = "${FIX_V4BX}"
KLIBCTHUMB = "${@['CONFIG_KLIBC_THUMB=n', 'CONFIG_KLIBC_THUMB=y'][(d.getVar('ARM_INSTRUCTION_SET') == 'thumb')]}"

do_configure () {
    ln -sf "${STAGING_DIR_TARGET}${exec_prefix}" linux
}

do_compile_prepend_toolchain-clang() {
    sed -i -e 's#$(KLIBCROSS)gcc#$(KLIBCROSS)clang#g' ${S}/Makefile
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP_${PN} = "already-stripped"
INSANE_SKIP_libklibc-dev = "dev-elf"
KLIBC_ARCH = "${TARGET_ARCH}"
KLIBC_ARCH_aarch64 = "arm64"
KLIBC_ARCH_armeb = "arm"
KLIBC_ARCH_mipsel = "mips"
KLIBC_ARCH_mips64el = "mips64"
KLIBC_ARCH_x86 = "i386"
KLIBC_ARCH_x86-64 = "x86_64"
KLIBC_ARCH_powerpc = "ppc"
KLIBC_ARCH_powerpc64 = "ppc64"
THIS_LIBKLIBC = "libklibc (= ${PV}-${PR})"

GCCPIE ?= ""
SECURITY_LDFLAGS = "${@'-z relro -z now -pie' if '${GCCPIE}' else ''}"
SECURITY_LDFLAGS_mips = ""
SECURITY_LDFLAGS_mipsel = ""
SECURITY_LDFLAGS_mips64 = ""
SECURITY_LDFLAGS_mips64el = ""
