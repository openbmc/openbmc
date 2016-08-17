SUMMARY = "GRUB2 is the next-generation GRand Unified Bootloader"

DESCRIPTION = "GRUB2 is the next generaion of a GPLed bootloader \
intended to unify bootloading across x86 operating systems. In \
addition to loading the Linux kernel, it implements the Multiboot \
standard, which allows for flexible loading of multiple boot images."

HOMEPAGE = "http://www.gnu.org/software/grub/"
SECTION = "bootloaders"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "ftp://ftp.gnu.org/gnu/grub/grub-${PV}.tar.gz \
           file://grub-2.00-fpmath-sse-387-fix.patch \
           file://check-if-liblzma-is-disabled.patch \
           file://fix-issue-with-flex-2.5.37.patch \
           file://grub-2.00-add-oe-kernel.patch \
           file://grub-install.in.patch \
           file://remove-gets.patch \
           file://fix-endianness-problem.patch \
           file://grub2-remove-sparc64-setup-from-x86-builds.patch \
           file://grub-2.00-fix-enable_execute_stack-check.patch \
           file://grub-no-unused-result.patch \
           file://grub-efi-allow-a-compilation-without-mcmodel-large.patch \
           file://grub-efi-fix-with-glibc-2.20.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0001-parse_dhcp_vendor-Add-missing-const-qualifiers.patch \
           file://grub2-fix-initrd-size-bug.patch \
           file://0001-Fix-CVE-2015-8370-Grub2-user-pass-vulnerability.patch \
           file://0001-Remove-direct-_llseek-code-and-require-long-filesyst.patch \
           file://fix-texinfo.patch \
            "

DEPENDS = "flex-native bison-native"

SRC_URI[md5sum] = "e927540b6eda8b024fb0391eeaa4091c"
SRC_URI[sha256sum] = "65b39a0558f8c802209c574f4d02ca263a804e8a564bc6caf1cd0fd3b3cc11e3"

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-(linux|freebsd.*)'

inherit autotools gettext texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG[grub-mount] = "--enable-grub-mount,--disable-grub-mount,fuse"
PACKAGECONFIG[device-mapper] = "--enable-device-mapper,--disable-device-mapper,lvm2"

# grub2 creates its own set of -nostdinc / -isystem / -ffreestanding CFLAGS and
# OE's default BUILD_CFLAGS (assigned to CFLAGS for native builds) etc, conflict
# with that. Note that since BUILD_CFLAGS etc are not used by grub2 target
# builds, it's safe to clear them unconditionally for both target and native.
BUILD_CPPFLAGS = ""
BUILD_CFLAGS = ""
BUILD_CXXFLAGS = ""
BUILD_LDFLAGS = ""

do_configure_prepend() {
	# The grub2 configure script uses variables such as TARGET_CFLAGS etc
	# for its own purposes. Remove the OE versions from the environment to
	# avoid conflicts.
	unset TARGET_CPPFLAGS TARGET_CFLAGS TARGET_CXXFLAGS TARGET_LDFLAGS
}

# grub and grub-efi's sysroot/${datadir}/grub/grub-mkconfig_lib are
# conflicted, remove it since no one uses it.
SYSROOT_PREPROCESS_FUNCS_class-target += "remove_sysroot_mkconfig_lib"
remove_sysroot_mkconfig_lib() {
    rm -r "${SYSROOT_DESTDIR}${datadir}/grub/grub-mkconfig_lib"
}
