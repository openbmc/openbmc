SUMMARY = "Publishes & browses available services on a link according to the Zeroconf / Bonjour protocol"
DESCRIPTION = "Bonjour, also known as zero-configuration networking, enables automatic discovery of computers, devices, and services on IP networks."
HOMEPAGE = "http://developer.apple.com/networking/bonjour/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=31c50371921e0fb731003bbc665f29bf"

RPROVIDES_${PN} += "libdns_sd.so"

SRC_URI = "http://opensource.apple.com/tarballs/mDNSResponder/mDNSResponder-${PV}.tar.gz \
           file://build.patch;patchdir=.. \
           file://mdns.service \
           file://0001-nss_mdns-Do-not-include-nss.h-when-libc-glibc.patch;patchdir=.. \
           "

SRC_URI[md5sum] = "4a6bc1628851002634ea3833a4dca317"
SRC_URI[sha256sum] = "de61dd0034357aa50c60806726fb1f70fa8e953ff9cb62eccfb73efad10dba0a"

PARALLEL_MAKE = ""

S = "${WORKDIR}/mDNSResponder-${PV}/mDNSPosix"

EXTRA_OEMAKE += "os=linux DEBUG=0 'CC=${CC}' 'LD=${CCLD} ${LDFLAGS}'"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 build/prod/mdnsd ${D}${sbindir}

    install -d ${D}${libdir}
    cp build/prod/libdns_sd.so ${D}${libdir}/libdns_sd.so.1
    chmod 0644 ${D}${libdir}/libdns_sd.so.1
    ln -s libdns_sd.so.1 ${D}${libdir}/libdns_sd.so

    install -d ${D}${includedir}
    install -m 0644 ../mDNSShared/dns_sd.h ${D}${includedir}

    install -d ${D}${mandir}/man8
    install -m 0644 ../mDNSShared/mDNSResponder.8 ${D}${mandir}/man8/mdnsd.8

    install -d ${D}${bindir}
    install -m 0755 ../Clients/build/dns-sd ${D}${bindir}

    install -d ${D}${libdir}
    oe_libinstall -C build/prod -so libnss_mdns-0.2 ${D}${libdir}
    ln -s libnss_mdns-0.2.so ${D}${libdir}/libnss_mdns.so.2

    install -d ${D}${sysconfdir}
    install -m 0644 nss_mdns.conf ${D}${sysconfdir}

    install -d ${D}${mandir}/man5
    install -m 0644 nss_mdns.conf.5 ${D}${mandir}/man5

    install -d ${D}${mandir}/man8
    install -m 0644 libnss_mdns.8 ${D}${mandir}/man8

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/mdns.service ${D}${systemd_unitdir}/system/
}

pkg_postinst_${PN} () {
    sed -e '/^hosts:/s/\s*\<mdns\>//' \
        -e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 mdns\3\4\5/' \
        -i $D/etc/nsswitch.conf
}

pkg_prerm_${PN} () {
    sed -e '/^hosts:/s/\s*\<mdns\>//' \
        -e '/^hosts:/s/\s*mdns//' \
        -i $D/etc/nsswitch.conf
}

inherit systemd

SYSTEMD_SERVICE_${PN} = "mdns.service"

FILES_${PN} += "${systemd_unitdir}/system/mdns.service"
FILES_${PN} += "${libdir}/libdns_sd.so.1 \
                ${bindir}/dns-sd \
                ${libdir}/libnss_mdns-0.2.so \
                ${sysconfdir}/nss_mdns.conf"

FILES_${PN}-dev += "${libdir}/libdns_sd.so \
                    ${includedir}/dns_sd.h "

FILES_${PN}-man += "${mandir}/man8/mdnsd.8 \
                    ${mandir}/man5/nss_mdns.conf.5 \
                    ${mandir}/man8/libnss_mdns.8"

PACKAGES = "${PN} ${PN}-dev ${PN}-man ${PN}-dbg"
