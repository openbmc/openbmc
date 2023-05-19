SUMMARY = "A simple, lightweight JavaScript API for handling cookies."
HOMEPAGE = "https://github.com/js-cookie/js-cookie"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e16cf0e247d84f8999bf55865a9c98cf"

SRC_URI = "git://github.com/js-cookie/js-cookie.git;protocol=https;branch=main"

SRCREV = "ab3f67fc4fad88cdf07b258c08e4164e06bf7506"

S = "${WORKDIR}/git"

JQUERYCOOKIEDIR = "${datadir}/javascript/jquery-cookie"
JQUERYCOOKIEDOCDIR = "${docdir}/libjs-jquery-cookie"

do_install() {
        install -d ${D}${JQUERYCOOKIEDIR}
        install -m 0644 ${S}/*.js ${D}${JQUERYCOOKIEDIR}
        install -m 0644 ${S}/src/*.mjs ${D}${JQUERYCOOKIEDIR}
        install -m 0644 ${S}/*.json ${D}${JQUERYCOOKIEDIR}

        install -d ${D}${JQUERYCOOKIEDOCDIR}
        install -m 0644 ${S}/*.md ${D}${JQUERYCOOKIEDOCDIR}

}

FILES:${PN} += "${datadir}/javascript/jquery-cookie"
FILES:${PN}-doc += "${docdir}/libjs-jquery-cookie"
