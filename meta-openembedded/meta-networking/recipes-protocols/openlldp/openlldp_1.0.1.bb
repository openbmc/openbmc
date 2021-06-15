SUMMARY = "Open-LLDP"
DESCRIPTION = "Link Layer Discovery Protocol for Linux that includes support for DCBX"
HOMEPAGE = "http://open-lldp.org/start"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8c2bc283e65df398ced5f5b747e78162"

S = "${WORKDIR}/git"

inherit pkgconfig autotools
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}

DEPENDS = "libnl libconfig readline"

SRCREV = "b71bfb87fefb31c4b1a6a7ae351791c90966c3a8"
PV .= "+git${SRCPV}"
SRC_URI = "git://github.com/intel/openlldp.git;protocol=https;branch=master \
           file://0001-Fix-musl-libc-build-issue.patch \
           file://0001-autotools-Add-include-path-to-generated-version.h.patch \
           file://0001-autotools-Add-option-to-disable-installation-of-syst.patch \
           file://0001-cmds-fix-enum-conversion.patch \
           file://0002-lldp_head-rename-and-make-extern.patch \
           file://0003-lldp-add-packed-struct-definition.patch \
           file://0004-lldptool-make-extern.patch \
           file://0005-cisco_oui-match-encode-handler-prototypes.patch \
           file://0006-ecp22-make-enum-a-type-rather-than-instance.patch \
           file://0007-lldp_8021qaz-extern-config-object.patch \
           file://0008-stringops-fix-some-string-copy-errors.patch \
           file://0009-8021qaz-mark-prio-map-functions-static.patch \
           "

# Makefile.am adds -Werror to AM_CFLAGS. There are warnings so disable it.
TARGET_CFLAGS += "-Wno-error"

# Enable install of systemd conf files.
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_system_unitdir}', '', d)}"

SYSTEMD_SERVICE_${PN} = "lldpad.service lldpad.socket"

# To enable service at boot set to enable in local.conf.
SYSTEMD_AUTO_ENABLE ?= "disable"

RRECOMMENDS_${PN} = "iproute2-tc"
