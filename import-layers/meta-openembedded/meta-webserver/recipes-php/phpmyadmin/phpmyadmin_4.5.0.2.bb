SUMMARY = "Web-based MySQL administration interface"
HOMEPAGE = "http://www.phpmyadmin.net"
# Main code is GPLv2, libraries/tcpdf is under LGPLv3, js/jquery is under MIT
LICENSE = "GPLv2 & LGPLv3 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://libraries/tcpdf/LICENSE.TXT;md5=5c87b66a5358ebcc495b03e0afcd342c"

SRC_URI = "https://files.phpmyadmin.net/phpMyAdmin/4.5.0.2/phpMyAdmin-4.5.0.2-all-languages.tar.xz \
           file://Port-content-spoofing-fix-CVE-2015-7873.patch \
           file://apache.conf \
           file://phpmyadmin-CVE-2015-8669.patch \
"

SRC_URI[md5sum] = "2d08d2fcc8f70f88a11a14723e3ca275"
SRC_URI[sha256sum] = "d2e90ea486d90b4ebe5eb02d7ad349ad2916c12a8981f98553395ef78d22a8ec"

S = "${WORKDIR}/phpMyAdmin-${PV}-all-languages"

inherit allarch

do_install() {
    install -d ${D}${datadir}/${BPN}
    cp -R --no-dereference --preserve=mode,links -v * ${D}${datadir}/${BPN}
    chown -R root:root ${D}${datadir}/${BPN}
    # Don't install patches to target
    rm -rf ${D}${datadir}/${BPN}/patches

    install -d ${D}${sysconfdir}/apache2/conf.d
    install -m 0644 ${WORKDIR}/apache.conf ${D}${sysconfdir}/apache2/conf.d/phpmyadmin.conf

    # Remove a few scripts that explicitly require bash (!)
    rm -f ${D}${datadir}/phpmyadmin/libraries/transformations/*.sh
}

FILES_${PN} = "${datadir}/${BPN} \
               ${sysconfdir}/apache2/conf.d"

RDEPENDS_${PN} += "bash"
