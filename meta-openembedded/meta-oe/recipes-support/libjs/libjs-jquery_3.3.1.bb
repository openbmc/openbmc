SUMMARY = "JavaScript library for dynamic web applications"
HOMEPAGE = "https://jquery.com/"
LICENSE = "GPL-2.0-only | MIT"
LIC_FILES_CHKSUM = "file://usr/share/doc/libjs-jquery/copyright;md5=04bfd6e5b918af29f2f79ce44527da62"

SRC_URI = "http://kr.archive.ubuntu.com/ubuntu/pool/main/j/jquery/${BPN}_${PV}~dfsg-3_all.deb"

SRC_URI[sha256sum] = "e04d192c2356e9d4c2b2c7d83fde9408713212b53c4d106e5b9e46c1a56da33b"

JQUERYDIR = "${datadir}/javascript/jquery"
JQUERYDOCDIR = "${docdir}/libjs-jquery"

S = "${WORKDIR}"

do_install() {
    install -d -m 0755 ${D}${JQUERYDIR}
    install -m 0644 ${S}${JQUERYDIR}/jquery.js ${D}${JQUERYDIR}/
    install -m 0644 ${S}${JQUERYDIR}/jquery.min.js ${D}${JQUERYDIR}/

    ln -sf jquery.min.js ${D}${JQUERYDIR}/jquery.lite.js
    ln -sf jquery.min.js ${D}${JQUERYDIR}/jquery.pack.js

    install -d -m 0644 ${D}${JQUERYDOCDIR}
    install -m 0644 ${S}${JQUERYDOCDIR}/copyright ${D}${JQUERYDOCDIR}/
}

FILES:${PN} = "/usr/share/javascript/jquery"
