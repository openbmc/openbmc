SUMMARY = "GRUB2 is the next-generation GRand Unified Bootloader"

DESCRIPTION = "GRUB2 is the next generaion of a GPLed bootloader \
intended to unify bootloading across x86 operating systems. In \
addition to loading the Linux kernel, it implements the Multiboot \
standard, which allows for flexible loading of multiple boot images."

HOMEPAGE = "http://www.gnu.org/software/grub/"
SECTION = "bootloaders"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

CVE_PRODUCT = "grub2"

# Applies only to RHEL
CVE_CHECK_WHITELIST += "CVE-2019-14865"
# Applies only to SUSE
CVE_CHECK_WHITELIST += "CVE-2021-46705"

SRC_URI = "${GNU_MIRROR}/grub/grub-${PV}.tar.gz \
           file://0001-Disable-mfpmath-sse-as-well-when-SSE-is-disabled.patch \
           file://autogen.sh-exclude-pc.patch \
           file://grub-module-explicitly-keeps-symbole-.module_license.patch \
           file://0001-grub.d-10_linux.in-add-oe-s-kernel-name.patch \
           file://CVE-2020-10713.patch \
           file://calloc-Make-sure-we-always-have-an-overflow-checking.patch \
           file://lvm-Add-LVM-cache-logical-volume-handling.patch \
           file://CVE-2020-14308-calloc-Use-calloc-at-most-places.patch \
           file://safemath-Add-some-arithmetic-primitives-that-check-f.patch \
           file://CVE-2020-14309-CVE-2020-14310-CVE-2020-14311-malloc-Use-overflow-checking-primitives-where-we-do-.patch \
           file://script-Remove-unused-fields-from-grub_script_functio.patch \
           file://CVE-2020-15706-script-Avoid-a-use-after-free-when-redefining-a-func.patch \
           file://CVE-2020-15707-linux-Fix-integer-overflows-in-initrd-size-handling.patch \
           file://determinism.patch \
           file://no-insmod-on-sb.patch \
           file://CVE-2020-14372_1.patch \
           file://CVE-2020-14372_2.patch \
           file://CVE-2020-14372_3.patch \
           file://CVE-2020-14372_4.patch \
           file://CVE-2020-14372_5.patch \
           file://CVE-2020-14372.patch \
           file://CVE-2020-27779.patch \
           file://CVE-2020-27779_2.patch \
           file://CVE-2020-27779_3.patch \
           file://CVE-2020-27779_4.patch \
           file://CVE-2020-27779_5.patch \
           file://CVE-2020-27779_6.patch \
           file://CVE-2020-27779_7.patch \
           file://CVE-2020-25632.patch \
           file://CVE-2020-25647.patch \
           file://0001-mmap-Fix-memory-leak-when-iterating-over-mapped-memo.patch \
           file://0002-net-net-Fix-possible-dereference-to-of-a-NULL-pointe.patch \
           file://0003-net-tftp-Fix-dangling-memory-pointer.patch \
           file://0004-kern-parser-Fix-resource-leak-if-argc-0.patch \
           file://0005-efi-Fix-some-malformed-device-path-arithmetic-errors.patch \
           file://0006-kern-efi-Fix-memory-leak-on-failure.patch \
           file://0007-kern-efi-mm-Fix-possible-NULL-pointer-dereference.patch \
           file://0008-gnulib-regexec-Resolve-unused-variable.patch \
           file://0009-gnulib-regcomp-Fix-uninitialized-token-structure.patch \
           file://0010-gnulib-argp-help-Fix-dereference-of-a-possibly-NULL-.patch \
           file://0011-gnulib-regexec-Fix-possible-null-dereference.patch \
           file://0012-gnulib-regcomp-Fix-uninitialized-re_token.patch \
           file://0013-io-lzopio-Resolve-unnecessary-self-assignment-errors.patch \
           file://0014-zstd-Initialize-seq_t-structure-fully.patch \
           file://0015-kern-partition-Check-for-NULL-before-dereferencing-i.patch \
           file://0016-disk-ldm-Make-sure-comp-data-is-freed-before-exiting.patch \
           file://0017-disk-ldm-If-failed-then-free-vg-variable-too.patch \
           file://0018-disk-ldm-Fix-memory-leak-on-uninserted-lv-references.patch \
           file://0019-disk-cryptodisk-Fix-potential-integer-overflow.patch \
           file://0020-hfsplus-Check-that-the-volume-name-length-is-valid.patch \
           file://0021-zfs-Fix-possible-negative-shift-operation.patch \
           file://0022-zfs-Fix-resource-leaks-while-constructing-path.patch \
           file://0023-zfs-Fix-possible-integer-overflows.patch \
           file://0024-zfsinfo-Correct-a-check-for-error-allocating-memory.patch \
           file://0025-affs-Fix-memory-leaks.patch \
           file://0026-libgcrypt-mpi-Fix-possible-unintended-sign-extension.patch \
           file://0027-libgcrypt-mpi-Fix-possible-NULL-dereference.patch \
           file://0028-syslinux-Fix-memory-leak-while-parsing.patch \
           file://0029-normal-completion-Fix-leaking-of-memory-when-process.patch \
           file://0030-commands-hashsum-Fix-a-memory-leak.patch \
           file://0031-video-efi_gop-Remove-unnecessary-return-value-of-gru.patch \
           file://0032-video-fb-fbfill-Fix-potential-integer-overflow.patch \
           file://0033-video-fb-video_fb-Fix-multiple-integer-overflows.patch \
           file://0034-video-fb-video_fb-Fix-possible-integer-overflow.patch \
           file://0035-video-readers-jpeg-Test-for-an-invalid-next-marker-r.patch \
           file://0036-gfxmenu-gui_list-Remove-code-that-coverity-is-flaggi.patch \
           file://0037-loader-bsd-Check-for-NULL-arg-up-front.patch \
           file://0038-loader-xnu-Fix-memory-leak.patch \
           file://0039-loader-xnu-Free-driverkey-data-when-an-error-is-dete.patch \
           file://0040-loader-xnu-Check-if-pointer-is-NULL-before-using-it.patch \
           file://0041-util-grub-install-Fix-NULL-pointer-dereferences.patch \
           file://0042-util-grub-editenv-Fix-incorrect-casting-of-a-signed-.patch \
           file://0043-util-glue-efi-Fix-incorrect-use-of-a-possibly-negati.patch \
           file://0044-script-execute-Fix-NULL-dereference-in-grub_script_e.patch \
           file://0045-commands-ls-Require-device_name-is-not-NULL-before-p.patch \
           file://0046-script-execute-Avoid-crash-when-using-outside-a-func.patch \
           file://CVE-2021-3981.patch \
           file://CVE-2021-3695.patch \
           file://CVE-2021-3696.patch \
           file://CVE-2021-3697.patch \
           file://CVE-2022-28733.patch \
           file://CVE-2022-28734.patch \
           file://CVE-2022-28736.patch \
           file://CVE-2022-28735.patch \
           file://font-Fix-size-overflow-in-grub_font_get_glyph_intern.patch \
           file://CVE-2022-2601.patch \
           file://CVE-2022-3775.patch \
           "
SRC_URI[md5sum] = "5ce674ca6b2612d8939b9e6abed32934"
SRC_URI[sha256sum] = "f10c85ae3e204dbaec39ae22fa3c5e99f0665417e91c2cb49b7e5031658ba6ea"

DEPENDS = "flex-native bison-native gettext-native"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST_armv7a = 'null'
COMPATIBLE_HOST_armv7ve = 'null'

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM_arm = "efi"
GRUBPLATFORM_aarch64 = "efi"
GRUBPLATFORM ??= "pc"

inherit autotools gettext texinfo pkgconfig

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

do_configure_prepend() {
	cd ${S}
	FROM_BOOTSTRAP=1 ${S}/autogen.sh
	cd ${B}
}

RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
