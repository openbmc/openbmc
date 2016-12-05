SUMMARY = "Library and parser for OpenPOWER-format VPD"
DESCRIPTION = "Library and parser for OpenPOWER-format VPD."
HOMEPAGE = "http://github.com/openbmc/openpower-vpd-parser"
PR = "r1"

inherit autotools pkgconfig pythonnative obmc-phosphor-license

SRC_URI = "git://github.com/openbmc/openpower-vpd-parser"
SRCREV = "9b3a25d46a0d99ec346886bef3b3abf398774f80"

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "openpower-read-vpd@.service"

S = "${WORKDIR}/git"

DEPENDS += " \
        autoconf-archive-native \
        "
