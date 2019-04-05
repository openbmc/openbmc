SUMMARY = "TCTI module for use with TSS2 libraries in UEFI environment"
SECTION = "security/tpm"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"
DEPENDS = "libtss2-dev gnu-efi-native gnu-efi pkgconfig"

SRC_URI = "git://github.com/tpm2-software/tpm2-tcti-uefi.git \
           file://configure_oe_fixup.patch \
          "
SRCREV = "131889d12d2c7d8974711d2ebd1032cd32577b7f"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"
EXTRA_OECONF_append = " --with-efi-includedir=${STAGING_INCDIR}/efi --with-efi-lds=${STAGING_LIBDIR_NATIVE}/"
RDEPENDS_${PN} = "gnu-efi"
