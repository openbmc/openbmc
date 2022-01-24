DESCRIPTION = "A JavaScript library for internationalization and localization that leverages the official Unicode CLDR JSON data"
SECTION = "console/network"
HOMEPAGE = "https://github.com/globalizejs/globalize"
LICENSE = "MIT"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4db68fb4d1d9986d736b35039f2ad9ea"

SRCREV = "771061fb35f7fd8d7c2c37f5d5bb26115be382bc"
SRC_URI = "git://github.com/globalizejs/globalize;protocol=https;nobranch=1"

FILES:${PN} = "${datadir}/javascript/jquery-globalize"
FILES:${PN}-doc += "${docdir}/${PN}"

do_install() {
        install -d ${D}${datadir}/javascript/jquery-globalize/
        install -m 0644 ${S}/dist/*.js ${D}${datadir}/javascript/jquery-globalize/
        install -m 0644 ${S}/dist/globalize/*.js ${D}${datadir}/javascript/jquery-globalize/

        install -d ${D}${docdir}/${PN}/
        install -m 0644 ${S}/LICENSE ${D}${docdir}/${PN}/
}
