SUMMARY = "Publishes & browses available services on a link according to the Zeroconf / Bonjour protocol"
DESCRIPTION = "Bonjour, also known as zero-configuration networking, enables automatic discovery of computers, devices, and services on IP networks."
HOMEPAGE = "http://developer.apple.com/networking/bonjour/"
LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31c50371921e0fb731003bbc665f29bf"

DEPENDS:append:libc-musl = " musl-nscd"

RPROVIDES:${PN} += "libdns_sd.so"

# matches annotated tag mDNSResponder-1310.140.1
SRCREV = "1d1de95b98fba2077d34c9d78b839a96aa0e1c77"
BRANCH = "rel/mDNSResponder-1310"
SRC_URI = "git://github.com/apple-oss-distributions/mDNSResponder;protocol=https;branch=${BRANCH} \
           file://mdns.service \
           file://0001-mdns-include-stddef.h-for-NULL.patch \
           file://0002-mdns-cross-compilation-fixes-for-bitbake.patch \
           file://0001-Create-subroutine-for-cleaning-recent-interfaces.patch \
           file://0002-Create-subroutine-for-tearing-down-an-interface.patch \
           file://0003-Track-interface-socket-family.patch \
           file://0004-Use-list-for-changed-interfaces.patch \
           file://0006-Remove-unneeded-function.patch \
           file://0008-Mark-deleted-interfaces-as-being-changed.patch \
           file://0009-Fix-possible-NULL-dereference.patch \
           file://0010-Handle-errors-from-socket-calls.patch \
           file://0011-Change-a-dynamic-allocation-to-file-scope-variable.patch \
           file://0001-dns-sd-Include-missing-headers.patch \
           file://0006-make-Add-top-level-Makefile.patch \
           "

CVE_PRODUCT = "apple:mdnsresponder"

# CVE-2007-0613 is not applicable as it only affects Apple products
# i.e. ichat,mdnsresponder, instant message framework and MacOS.
# Also, https://www.exploit-db.com/exploits/3230 shows the part of code
# affected by CVE-2007-0613 which is not preset in upstream source code.
# Hence, CVE-2007-0613 does not affect other Yocto implementations and
# is not reported for other distros can be marked whitelisted.
# Links:
# https://vulmon.com/vulnerabilitydetails?qid=CVE-2007-0613
# https://www.incibe-cert.es/en/early-warning/vulnerabilities/cve-2007-0613
# https://security-tracker.debian.org/tracker/CVE-2007-0613
# https://vulmon.com/vulnerabilitydetails?qid=CVE-2007-0613
CVE_CHECK_IGNORE += "CVE-2007-0613"

PARALLEL_MAKE = ""

# We install a stub Makefile in the top directory so that the various checks
# in base.bbclass pass their tests for a Makefile, this ensures (that amongst
# other things) the sstate checks will clean the build directory when the
# task hashes changes.
#
# We can't use the approach of setting ${S} to mDNSPosix as we need
# DEBUG_PREFIX_MAP to cover files which come from the Clients directory too.
S = "${WORKDIR}/git"

EXTRA_OEMAKE += "os=linux DEBUG=0 'CC=${CC}' 'LD=${CCLD} ${LDFLAGS}'"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install () {
    cd mDNSPosix

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

pkg_postinst:${PN} () {
    sed -e '/^hosts:/s/\s*\<mdns\>//' \
        -e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 mdns\3\4\5/' \
        -i $D/etc/nsswitch.conf
}

pkg_prerm:${PN} () {
    sed -e '/^hosts:/s/\s*\<mdns\>//' \
        -e '/^hosts:/s/\s*mdns//' \
        -i $D/etc/nsswitch.conf
}

inherit systemd

SYSTEMD_SERVICE:${PN} = "mdns.service"

FILES:${PN} += "${systemd_unitdir}/system/mdns.service"
FILES:${PN} += "${libdir}/libdns_sd.so.1 \
                ${bindir}/dns-sd \
                ${libdir}/libnss_mdns-0.2.so \
                ${sysconfdir}/nss_mdns.conf"

FILES:${PN}-dev += "${libdir}/libdns_sd.so \
                    ${includedir}/dns_sd.h "

FILES:${PN}-man += "${mandir}/man8/mdnsd.8 \
                    ${mandir}/man5/nss_mdns.conf.5 \
                    ${mandir}/man8/libnss_mdns.8"

PACKAGES = "${PN} ${PN}-dev ${PN}-man ${PN}-dbg"
