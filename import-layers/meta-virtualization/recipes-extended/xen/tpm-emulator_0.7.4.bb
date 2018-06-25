# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "TPM Emulator"
HOMEPAGE = "http://xenbits.xen.org/xen-extfiles"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README;md5=eeabd77cf8fd8a8bc42983884cb09863"

SRC_URI = "\
    http://xenbits.xen.org/xen-extfiles/tpm_emulator-${PV}.tar.gz;name=tpm-emulator \
    file://tpmemu-0.7.4.patch \
    file://vtpm-bufsize.patch \
    file://vtpm-locality.patch \
    file://vtpm-parent-sign-ek.patch \
    file://vtpm-deepquote.patch \
    file://vtpm-deepquote-anyloc.patch \
    file://vtpm-cmake-Wextra.patch \
    file://vtpm-implicit-fallthrough.patch \
"
SRC_URI[tpm-emulator.md5sum] = "e26becb8a6a2b6695f6b3e8097593db8"
SRC_URI[tpm-emulator.sha256sum] = "4e48ea0d83dd9441cc1af04ab18cd6c961b9fa54d5cbf2c2feee038988dea459"

S="${WORKDIR}/tpm_emulator-${PV}"
B="${S}/build"

require tpm-emulator.inc
