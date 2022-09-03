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
           file://0001-Disable-mfpmath-sse-as-well-when-SSE-is-disabled.patch \
           file://autogen.sh-exclude-pc.patch \
           file://grub-module-explicitly-keeps-symbole-.module_license.patch \
           file://0001-grub.d-10_linux.in-add-oe-s-kernel-name.patch \
           file://determinism.patch \
           file://0001-RISC-V-Restore-the-typcast-to-long.patch \
           file://CVE-2021-3981-grub-mkconfig-Restore-umask-for-the-grub.cfg.patch \
           file://0001-configure.ac-Use-_zicsr_zifencei-extentions-on-riscv.patch \
           file://video-Remove-trailing-whitespaces.patch \
           file://CVE-2021-3695-video-readers-png-Drop-greyscale-support-to-fix-heap.patch \
           file://CVE-2021-3696-video-readers-png-Avoid-heap-OOB-R-W-inserting-huff.patch \
           file://video-readers-jpeg-Abort-sooner-if-a-read-operation-.patch \
           file://video-readers-jpeg-Refuse-to-handle-multiple-start-o.patch \
           file://CVE-2021-3697-video-readers-jpeg-Block-int-underflow-wild-pointer.patch \
           file://CVE-2022-28733-net-ip-Do-IP-fragment-maths-safely.patch \
           file://CVE-2022-28734-net-http-Fix-OOB-write-for-split-http-headers.patch \
           file://CVE-2022-28734-net-http-Error-out-on-headers-with-LF-without-CR.patch \
           file://CVE-2022-28735-kern-efi-sb-Reject-non-kernel-files-in-the-shim_lock.patch \
"

SRC_URI[sha256sum] = "23b64b4c741569f9426ed2e3d0e6780796fca081bee4c99f62aa3f53ae803f5f"

# Applies only to RHEL
CVE_CHECK_IGNORE += "CVE-2019-14865"
# Applies only to SUSE
CVE_CHECK_IGNORE += "CVE-2021-46705"

DEPENDS = "flex-native bison-native gettext-native"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*|riscv.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST:armv7a = 'null'
COMPATIBLE_HOST:armv7ve = 'null'

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM:arm = "efi"
GRUBPLATFORM:aarch64 = "efi"
GRUBPLATFORM:riscv32 = "efi"
GRUBPLATFORM:riscv64 = "efi"
GRUBPLATFORM ??= "pc"

inherit autotools gettext texinfo pkgconfig

CFLAGS:remove = "-O2"

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
