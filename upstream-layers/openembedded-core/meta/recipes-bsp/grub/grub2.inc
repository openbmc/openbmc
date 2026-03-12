SUMMARY = "GRUB2 is the next-generation GRand Unified Bootloader"

DESCRIPTION = "GRUB2 is the next generaion of a GPLed bootloader \
intended to unify bootloading across x86 operating systems. In \
addition to loading the Linux kernel, it implements the Multiboot \
standard, which allows for flexible loading of multiple boot images."

HOMEPAGE = "http://www.gnu.org/software/grub/"
SECTION = "bootloaders"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

CVE_PRODUCT = "grub2"

SRC_URI = "${GNU_MIRROR}/grub/grub-${PV}.tar.gz \
           file://0001-autogen.sh-exclude-.pc-from-po-POTFILES.in.patch \
           file://0002-grub-module-explicitly-keeps-symbole-.module_license.patch \
           file://0003-grub.d-10_linux.in-add-oe-s-kernel-name.patch \
           file://0001-Revert-configure-Check-linker-for-image-base-support.patch \
           file://0002-Revert-configure-Print-a-more-helpful-error-if-autoc.patch \
"

SRC_URI[sha256sum] = "d0415fbb3e739237064e173743a6e5f60c33a81ec02a069cc9152d80efff4967"

CVE_STATUS[CVE-2019-14865] = "not-applicable-platform: applies only to RHEL"
CVE_STATUS[CVE-2023-4001]  = "not-applicable-platform: Applies only to RHEL/Fedora"
CVE_STATUS[CVE-2024-1048]  = "not-applicable-platform: Applies only to RHEL/Fedora"
CVE_STATUS[CVE-2024-2312]  = "not-applicable-platform: Applies only to Ubuntu"

DEPENDS = "flex-native bison-native gettext-native gawk-replacement-native"

GRUB_COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*|loongarch64.*|riscv.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST = "${GRUB_COMPATIBLE_HOST}"
# Grub doesn't support hard float toolchain and won't be able to forcefully
# disable it on some of the target CPUs. See 'configure.ac' for
# supported/unsupported CPUs in hardfp.
COMPATIBLE_HOST:armv7a = "${@'null' if bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', True, False, d) else d.getVar('GRUB_COMPATIBLE_HOST')}"
COMPATIBLE_HOST:armv7ve = "${@'null' if bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', True, False, d) else d.getVar('GRUB_COMPATIBLE_HOST')}"

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM:arm = "efi"
GRUBPLATFORM:aarch64 = "efi"
GRUBPLATFORM:loongarch64 = "efi"
GRUBPLATFORM:riscv32 = "efi"
GRUBPLATFORM:riscv64 = "efi"
GRUBPLATFORM ??= "pc"

inherit autotools gettext texinfo pkgconfig bash-completion

CFLAGS:remove = "-O2"
# It doesn't support sse, its make.defaults sets:
# CFLAGS += -mno-mmx -mno-sse
# So also remove -mfpmath=sse from TUNE_CCARGS
TUNE_CCARGS:remove = "-mfpmath=sse"
TUNE_CCARGS:append:toolchain-clang = " -fuse-ld=bfd -Wno-error=unused-command-line-argument"

EXTRA_OECONF = "--with-platform=${GRUBPLATFORM} \
                --disable-grub-mkfont \
                --program-prefix="" \
                --enable-liblzma=no \
                --enable-libzfs=no \
                --enable-largefile \
                --disable-werror \
"

PACKAGECONFIG ??= ""
PACKAGECONFIG[grub-mount] = "--enable-grub-mount,--disable-grub-mount,fuse"
PACKAGECONFIG[device-mapper] = "--enable-device-mapper,--disable-device-mapper,libdevmapper"

# grub2 creates its own set of -nostdinc / -isystem / -ffreestanding CFLAGS and
# OE's default BUILD_CFLAGS (assigned to CFLAGS for native builds) etc, conflict
# with that. Note that since BUILD_CFLAGS etc are not used by grub2 target
# builds, it's safe to clear them unconditionally for both target and native.
BUILD_CPPFLAGS = ""
BUILD_CFLAGS = ""
BUILD_CXXFLAGS = ""
BUILD_LDFLAGS = ""

export PYTHON = "python3"

do_configure:prepend() {
	cd ${S}
	FROM_BOOTSTRAP=1 ${S}/autogen.sh
	cd ${B}
}

# | aarch64-poky-linux-clang: error: invalid argument '-mcmodel=large' only allowed with '-fno-pic'
# see - https://bugs.gentoo.org/942843
TOOLCHAIN:aarch64 = "gcc"
