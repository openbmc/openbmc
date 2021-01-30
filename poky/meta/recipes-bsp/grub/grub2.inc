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
