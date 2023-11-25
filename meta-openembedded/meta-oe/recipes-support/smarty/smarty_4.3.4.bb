DESCRIPTION = "the compiling PHP template engine"
SECTION = "console/network"
HOMEPAGE = "https://www.smarty.net/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c0f216b2120ffc367e20f2b56df51b3"

DEPENDS += "php"

SRC_URI = "git://github.com/smarty-php/smarty.git;protocol=https;branch=support/4.3"

SRCREV = "3931d8f54b8f7a4ffab538582d34d4397ba8daa5"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}${datadir}/php/smarty3/libs/
        install -m 0644 ${S}/libs/*.php ${D}${datadir}/php/smarty3/libs/

        install -d ${D}${datadir}/php/smarty3/libs/plugins
        install -m 0644 ${S}/libs/plugins/*.php ${D}${datadir}/php/smarty3/libs/plugins/

        install -d ${D}${datadir}/php/smarty3/libs/sysplugins
        install -m 0644 ${S}/libs/sysplugins/*.php ${D}${datadir}/php/smarty3/libs/sysplugins/
}
FILES:${PN} = "${datadir}/php/smarty3/"
