SUMMARY = "JavaScript library for dynamic web applications"
HOMEPAGE = "https://jquery.com/"
LICENSE = "MIT | BSD | GPL-2"
LIC_FILES_CHKSUM = "file://usr/share/doc/libjs-jquery/copyright;md5=5d1ec6f95e0a91d38e2f71de93ddb00e"

SRC_URI = "http://kr.archive.ubuntu.com/ubuntu/pool/main/j/jquery/${BPN}_${PV}+debian-1ubuntu1~ubuntu12.04.1_all.deb;subdir=${BP}"
SRC_URI[md5sum] = "fa511ab67f6e960c5b6d39a4d665e47f"
SRC_URI[sha256sum] = "190ca18a71e35c8ab2ba73fe5be3c7cc601fe20b45709d801110818f1b602cc1"

JQUERYDIR = "${datadir}/javascript/jquery"
JQUERYDOCDIR = "${docdir}/libjs-jquery"

do_install() {
    install -d -m 0755 ${D}${JQUERYDIR}
    install -m 0644 ${S}${JQUERYDIR}/jquery.js ${D}${JQUERYDIR}/
    install -m 0644 ${S}${JQUERYDIR}/jquery.min.js ${D}${JQUERYDIR}/

    ln -sf jquery.min.js ${D}${JQUERYDIR}/jquery.lite.js
    ln -sf jquery.min.js ${D}${JQUERYDIR}/jquery.pack.js

    install -d -m 0644 ${D}${JQUERYDOCDIR}
    install -m 0644 ${S}${JQUERYDOCDIR}/copyright ${D}${JQUERYDOCDIR}/
}

FILES_${PN} = "/usr/share/javascript/jquery"
