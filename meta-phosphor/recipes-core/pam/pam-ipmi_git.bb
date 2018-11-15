SUMMARY = "PAM modules for IPMI support"
DESCRIPTION = "PAM modules managing password for IPMI"
HOMEPAGE = "http://github.com/openbmc/pam-ipmi"
PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI += "git://github.com/openbmc/pam-ipmi"
SRCREV = "d0e324ab377a1b4fb94768907eba0b48ac1857d4"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "openssl libpam"

FILES_${PN} += " \
               ${base_libdir}/security/ \
               ${sysconfdir}/key_file \
               "
