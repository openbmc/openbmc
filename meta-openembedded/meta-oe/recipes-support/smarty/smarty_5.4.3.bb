SUMMARY = "Template engine for PHP"
DESCRIPTION = "\
    Smarty facilitates the separation of presentation (HTML/CSS) from \
    application logic. This implies that PHP code is application logic, and is \
    separated from the presentation. \
"
HOMEPAGE = "https://smarty-php.github.io/smarty/"
BUGTRACKER = "https://github.com/smarty-php/smarty/issues"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c0f216b2120ffc367e20f2b56df51b3"

SRC_URI = "git://github.com/smarty-php/smarty.git;protocol=https;branch=master"

SRCREV = "c6bff5795081ca5e60aabda59fb87daa511acd1e"

S = "${UNPACKDIR}/git"

INHIBIT_DEFAULT_DEPS = "1"

inherit allarch

# `make clean` removes files, they can't be rebuild with `make all` 
CLEANBROKEN = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
        install -d ${D}${datadir}/php/smarty3/libs/
        install -m 0644 ${S}/libs/*.php ${D}${datadir}/php/smarty3/libs/

        install -d ${D}${datadir}/php/smarty3/src/
        cp -rf ${S}/src/* ${D}${datadir}/php/smarty3/src/
}
FILES:${PN} += "${datadir}/php/smarty3/"

RDEPENDS:${PN} = "php"

CVE_STATUS[CVE-2020-10375] = "cpe-incorrect: The recipe used in the meta-openembedded is a different smarty package compared to the one which has the CVE issue."
