SUMMARY = "EFI executable for fwupd"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/fwupd/fwupd-efi;protocol=https;branch=main \
           file://cc.patch"
SRCREV = "36ce593f58e391cca43fd388824496ff98d83480"
S = "${WORKDIR}/git"

DEPENDS = "gnu-efi"

COMPATIBLE_HOST = "(x86_64.*|i.86.*|aarch64.*|arm.*)-linux"

inherit meson

# These should be configured as needed
SBAT_DISTRO_ID ?= "${DISTRO}"
SBAT_DISTRO_SUMMARY ?= "${DISTRO_NAME}"
SBAT_DISTRO_URL ?= ""

EXTRA_OEMESON += "-Defi-cc="${@meson_array('CC', d)}" \
                  -Defi-ld='${HOST_PREFIX}ld' \
                  -Defi-includedir=${STAGING_INCDIR}/efi \
                  -Defi-libdir=${STAGING_LIBDIR} \
                  -Defi_sbat_distro_id='${SBAT_DISTRO_ID}' \
                  -Defi_sbat_distro_summary='${SBAT_DISTRO_SUMMARY}' \
                  -Defi_sbat_distro_url='${SBAT_DISTRO_URL}' \
                  -Defi_sbat_distro_pkgname='${PN}' \
                  -Defi_sbat_distro_version='${PV}'\
                  "

# The compile assumes GCC at present
TOOLCHAIN = "gcc"
