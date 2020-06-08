SUMMARY = "TCTI module for use with TSS2 libraries in UEFI environment"
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
DEPENDS = "libtss2-dev libtss2-mu-dev gnu-efi-native gnu-efi pkgconfig autoconf-archive-native"

SRC_URI = "git://github.com/tpm2-software/tpm2-tcti-uefi.git \
           file://configure_oe_fixup.patch \
           file://0001-configure.ac-stop-inserting-host-directories-into-co.patch \
           file://tpm2-get-caps-fixed.patch \
           file://fix_header_file.patch \
          "
SRCREV = "0241b08f069f0fdb3612f5c1b938144dbe9be811"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EFIDIR ?= "/EFI/BOOT"

EFI_ARCH_x86 = "ia32"
EFI_ARCH_x86-64 = "x86_64"

CFLAGS_append = " -I${STAGING_INCDIR}/efi -I${STAGING_INCDIR}/efi/${EFI_ARCH}"

EXTRA_OECONF_append = " \
    --with-efi-includedir=${STAGING_INCDIR} \
    --with-efi-crt0=${STAGING_LIBDIR}/crt0-efi-${EFI_ARCH}.o \
    --with-efi-lds=${STAGING_LIBDIR}/elf_${EFI_ARCH}_efi.lds \
"

do_compile_append() {
	oe_runmake example
}

do_install_append() {
	install -d "${D}${EFIDIR}"
	install -m 0755 "${B}"/example/*.efi "${D}${EFIDIR}"
}

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

FILES_${PN} += "${EFIDIR}"

RDEPENDS_${PN} = "gnu-efi libtss2-mu"
