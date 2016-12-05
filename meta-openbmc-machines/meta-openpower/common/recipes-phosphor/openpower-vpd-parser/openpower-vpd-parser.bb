SUMMARY = "Library and parser for OpenPOWER-format VPD"
DESCRIPTION = "Library and parser for OpenPOWER-format VPD."
HOMEPAGE = "http://github.com/openbmc/openpower-vpd-parser"
PR = "r1"

inherit autotools pkgconfig obmc-phosphor-python-autotools obmc-phosphor-license

SRC_URI = "git://github.com/openbmc/openpower-vpd-parser"
SRCREV = "79f9eee0da046577955a68d647b011c1850bec9b"

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "openpower-read-vpd@.service"

S = "${WORKDIR}/git"

DEPENDS += " \
        autoconf-archive-native \
        "
