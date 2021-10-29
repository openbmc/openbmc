DESCRIPTION = "A JavaScript library for internationalization and localization that leverages the official Unicode CLDR JSON data"
SECTION = "console/network"
HOMEPAGE = "https://github.com/globalizejs/globalize"
LICENSE = "MIT"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4db68fb4d1d9986d736b35039f2ad9ea"

SRC_URI = "git://github.com/globalizejs/globalize;tag=1.7.0;nobranch=1"

FILES:${PN} = "${datadir}/javascript/jquery-globalize"
FILES:${PN}-doc += "${docdir}/${PN}"

do_install() {
        install -d ${D}${datadir}/javascript/jquery-globalize/
        install -m 0644 ${S}/dist/*.js ${D}${datadir}/javascript/jquery-globalize/
        install -m 0644 ${S}/dist/globalize/*.js ${D}${datadir}/javascript/jquery-globalize/

        install -d ${D}${docdir}/${PN}/
        install -m 0644 ${S}/LICENSE ${D}${docdir}/${PN}/
}
