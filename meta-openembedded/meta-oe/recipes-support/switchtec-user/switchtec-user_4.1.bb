SUMMARY = "Easy to use CLI and C library for communicating with Microsemi's Switchtec management interface"
HOMEPAGE = "https://github.com/Microsemi/switchtec-user"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3d6b07c89629cff2990d2e8e1f4c2382"

DEPENDS = "ncurses openssl"

SRCREV = "762ba441d2c02685f98e5f56e984db033bab279a"
SRC_URI = " \
    git://github.com/Microsemi/switchtec-user.git;protocol=https;branch=master \
    file://0001-cli-Fix-format-security-warning.patch \
"
SRC_URI[sha256sum] = "f98c1fe23e1d7a11fb23e8bcf9b563929fc805ea669191a7fd525ad16519f655"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

EXTRA_OEMAKE = "DESTDIR='${D}' PREFIX='${prefix}' LDCONFIG='true' LIBDIR='${D}${libdir}'"

do_install () {
     oe_runmake install
}
