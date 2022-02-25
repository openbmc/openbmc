DESCRIPTION = "Highly customizable checkboxes and radio buttons (jQuery & Zepto)"
SECTION = "console/network"
HOMEPAGE = "http://fronteed.com/iCheck"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://icheck.js;start_line=1;end_line=8;md5=404078d7de9f05ed64d364274f790055"

SRC_URI = "git://github.com/fronteed/icheck.git;protocol=https;branch=${PV}"

SRCREV = "c8c1af84e4b90b4aea31466aad09bf877619e943"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}${datadir}/javascript/jquery-icheck/
        install -m 0644 ${S}/icheck.js ${D}${datadir}/javascript/jquery-icheck/
        install -m 0644 ${S}/icheck.min.js ${D}${datadir}/javascript/jquery-icheck/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/
        install -m 0644 ${S}/skins/all.css ${D}${datadir}/javascript/jquery-icheck/skins/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/flat/
        install -m 0644 ${S}/skins/flat/* ${D}${datadir}/javascript/jquery-icheck/skins/flat/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/futurico/
        install -m 0644 ${S}/skins/futurico/* ${D}${datadir}/javascript/jquery-icheck/skins/futurico/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/line/
        install -m 0644 ${S}/skins/line/* ${D}${datadir}/javascript/jquery-icheck/skins/line/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/minimal/
        install -m 0644 ${S}/skins/minimal/* ${D}${datadir}/javascript/jquery-icheck/skins/minimal/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/polaris/
        install -m 0644 ${S}/skins/polaris/* ${D}${datadir}/javascript/jquery-icheck/skins/polaris/

        install -d ${D}${datadir}/javascript/jquery-icheck/skins/square/
        install -m 0644 ${S}/skins/square/* ${D}${datadir}/javascript/jquery-icheck/skins/square/
}

FILES:${PN} += "${datadir}/javascript/jquery-icheck"

