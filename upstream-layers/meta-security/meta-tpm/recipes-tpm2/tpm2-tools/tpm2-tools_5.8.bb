SUMMARY = "Tools for TPM2."
DESCRIPTION = "tpm2-tools"
HOMEPAGE = "https://github.com/tpm2-software/tpm2-tools"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"
SECTION = "tpm"

DEPENDS = "tpm2-tss openssl curl autoconf-archive-native"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "1cb73185cae814b4e15c7c2d0b22642d640faf48775f4156a1fd92edf84bef73"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

inherit autotools pkgconfig bash-completion

PACKAGECONFIG ??= "efivar"
PACKAGECONFIG[efivar] = "--with-efivar,--without-efivar,efivar"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2017-7524] = "fixed-version: Fixed since version 3.0.0"
CVE_STATUS[CVE-2024-29039] = "fixed-version: Fixed since version 5.7"
