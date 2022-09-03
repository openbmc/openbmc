SUMMARY = "GLOME Login Client"
HOME_PAGE = "https://github.com/google/glome"
DESCRIPTION = "GLOME is used to authorize serial console access to Linux machines"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit meson pkgconfig

DEPENDS += "openssl"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/google/glome.git;branch=master;protocol=https"
SRCREV = "978ad9fb165f1e382c875f2ce08a1fc4f2ddcf1b"

FILES:${PN} += "${libdir}/security"

PACKAGECONFIG ??= ""
PACKAGECONFIG[glome-cli] = "-Dglome-cli=true,-Dglome-cli=false"
PACKAGECONFIG[pam-glome] = "-Dpam-glome=true,-Dpam-glome=false,libpam"

EXTRA_OEMESON = "-Dtests=false"

