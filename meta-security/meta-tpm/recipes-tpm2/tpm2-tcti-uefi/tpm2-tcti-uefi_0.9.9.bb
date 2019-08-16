SUMMARY = "TCTI module for use with TSS2 libraries in UEFI environment"
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
DEPENDS = "libtss2-dev gnu-efi-native gnu-efi pkgconfig autoconf-archive-native"

SRC_URI = "git://github.com/tpm2-software/tpm2-tcti-uefi.git \
           file://configure_oe_fixup.patch \
	   file://0001-configure.ac-stop-inserting-host-directories-into-co.patch \
          "
SRCREV = "7baf1eebfeb56a896bdd5d677fb24377d619eb9d"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EFIDIR ?= "/EFI/BOOT"

do_compile_append() {
	oe_runmake example
}

do_install_append() {
	install -d "${D}${EFIDIR}"
	install -m 0755 "${B}"/example/*.efi "${D}${EFIDIR}"
}

EFI_ARCH_x86 = "ia32"
EFI_ARCH_x86-64 = "x86_64"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
EXTRA_OECONF_append = "\
    --with-efi-includedir=${STAGING_INCDIR}/efi \
    --with-efi-crt0=${STAGING_LIBDIR_NATIVE}/crt0-efi-${EFI_ARCH}.o \
    --with-efi-lds=${STAGING_LIBDIR_NATIVE}/elf_${EFI_ARCH}_efi.lds \
"
RDEPENDS_${PN} = "gnu-efi"

FILES_${PN} += "${EFIDIR}"
