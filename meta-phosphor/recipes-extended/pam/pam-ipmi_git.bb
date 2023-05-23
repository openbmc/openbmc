SUMMARY = "PAM modules for IPMI support"
DESCRIPTION = "PAM modules managing password for IPMI"
HOMEPAGE = "http://github.com/openbmc/pam-ipmi"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "autoconf-archive-native"
DEPENDS += "openssl libpam"
SRCREV = "9e7627a5fff4cb72abd10d96d3aee794e588b2ee"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/pam-ipmi;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF += "--libdir=${base_libdir}"

FILES:${PN} += " \
               ${base_libdir}/security/ \
               ${sysconfdir}/key_file \
               ${sysconfdir}/ipmi_pass \
               "
