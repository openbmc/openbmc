SUMMARY = "Publishes & browses available services on a link according to the Zeroconf / Bonjour protocol"
DESCRIPTION = "Bonjour, also known as zero-configuration networking, enables automatic discovery of computers, devices, and services on IP networks."
HOMEPAGE = "https://developer.apple.com/bonjour/"
LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=31c50371921e0fb731003bbc665f29bf"

DEPENDS:append:libc-musl = " musl-nscd"

SRC_URI = "git://github.com/apple-oss-distributions/mDNSResponder;protocol=https;branch=rel/mDNSResponder-2200 \
           file://0001-dns-sd-Include-missing-headers.patch \
           file://0002-make-Set-libdns_sd.so-soname-correctly.patch \
           file://0004-make-Separate-TLS-targets-from-libraries.patch \
           file://0005-mDNSCore-Fix-broken-debug-parameter.patch \
           file://0006-make-Add-top-level-Makefile.patch \
           file://0001-Create-subroutine-for-cleaning-recent-interfaces.patch \
           file://0002-Create-subroutine-for-tearing-down-an-interface.patch \
           file://0003-Track-interface-socket-family.patch \
           file://0004-Indicate-loopback-interface-to-mDNS-core.patch \
           file://0005-Use-list-for-changed-interfaces.patch \
           file://0006-Handle-noisy-netlink-sockets.patch \
           file://0007-Mark-deleted-interfaces-as-being-changed.patch \
           file://0008-Handle-errors-from-socket-calls.patch \
           file://0009-remove-unneeded-headers.patch \
           file://mdns.service \
           file://0015-Add-missing-limits.h.patch \
           file://0001-Handle-interface-without-ifa_addr.patch \
           "
SRCREV = "8acabead9ae56551011172d6835822a89c5298d6"

# We install a stub Makefile in the top directory so that the various checks
# in base.bbclass pass their tests for a Makefile, this ensures (that amongst
# other things) the sstate checks will clean the build directory when the
# task hashes changes.
#
# We can't use the approach of setting ${S} to mDNSPosix as we need
# DEBUG_PREFIX_MAP to cover files which come from the Clients directory too.
S = "${WORKDIR}/git"

inherit github-releases manpages systemd update-rc.d

PACKAGECONFIG ?= "tls \
		  ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[debug] = "DEBUG=1,DEBUG=0"
PACKAGECONFIG[ipv6] = "HAVE_IPV6=1,HAVE_IPV6=0"
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[tls] = ",tls=no,mbedtls"

CVE_PRODUCT = "apple:mdnsresponder"

CVE_STATUS[CVE-2007-0613] = "not-applicable-platform: Issue affects Apple products \
i.e. ichat,mdnsresponder, instant message framework and MacOS. Also, \
https://www.exploit-db.com/exploits/3230 shows the part of code \
affected by CVE-2007-0613 which is not preset in upstream source code. \
Hence, CVE-2007-0613 does not affect other Yocto implementations and \
is not reported for other distros can be marked whitelisted. \
Links: https://vulmon.com/vulnerabilitydetails?qid=CVE-2007-0613 \
https://www.incibe-cert.es/en/early-warning/vulnerabilities/cve-2007-0613 \
https://security-tracker.debian.org/tracker/CVE-2007-0613 \
https://vulmon.com/vulnerabilitydetails?qid=CVE-2007-0613"

PARALLEL_MAKE = ""

EXTRA_OEMAKE = "os=linux 'CC=${CCLD}' 'LD=${CCLD}' 'LINKOPTS=${LDFLAGS}' STRIP=: ${PACKAGECONFIG_CONFARGS}"

# MDNS_VERSIONSTR_NODTS disables __DATE__ and __TIME__ in the version string,
# which are fixed anyway for build reproducibility.
TARGET_CPPFLAGS += "-DMDNS_VERSIONSTR_NODTS"

TARGET_CC_ARCH += "${LDFLAGS}"

MDNS_BUILDDIR = "build/${@bb.utils.contains('PACKAGECONFIG','debug','debug','prod', d)}"

do_install () {
	cd mDNSPosix

	install -d ${D}${sbindir}
	install ${MDNS_BUILDDIR}/mdnsd ${D}${sbindir}

	install -d ${D}${libdir}
	install -m 0644 ${MDNS_BUILDDIR}/libdns_sd.so ${D}${libdir}/libdns_sd.so.1
	ln -s libdns_sd.so.1 ${D}${libdir}/libdns_sd.so

	install -d ${D}${includedir}
	install -m 0644 ../mDNSShared/dns_sd.h ${D}${includedir}

	install -d ${D}${mandir}/man8
	install -m 0644 ../mDNSShared/mDNSResponder.8 ${D}${mandir}/man8/mdnsd.8

	install -d ${D}${bindir}
	install -m 0755 ../Clients/build/dns-sd ${D}${bindir}

	install -d ${D}${libdir}
	oe_libinstall -C ${MDNS_BUILDDIR} -so libnss_mdns-0.2 ${D}${libdir}
	ln -s libnss_mdns-0.2.so ${D}${libdir}/libnss_mdns.so.2

	install -d ${D}${sysconfdir}
	install -m 0644 nss_mdns.conf ${D}${sysconfdir}

	install -d ${D}${mandir}/man5
	install -m 0644 nss_mdns.conf.5 ${D}${mandir}/man5

	install -d ${D}${mandir}/man8
	install -m 0644 libnss_mdns.8 ${D}${mandir}/man8

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/mdns.service ${D}${systemd_system_unitdir}

	install -d ${D}${INIT_D_DIR}
	install mdnsd.sh ${D}${INIT_D_DIR}/mdns
}

pkg_postinst:${PN} () {
	if [ -r $D${sysconfdir}/nsswitch.conf ]; then
		sed -e '/^hosts:/s/\s*\<mdns\>//' \
			-e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 mdns\3\4\5/' \
			-i $D${sysconfdir}/nsswitch.conf
	fi
}

pkg_prerm:${PN} () {
	if [ -r $D${sysconfdir}/nsswitch.conf ]; then
		sed -e '/^hosts:/s/\s*\<mdns\>//' \
			-e '/^hosts:/s/\s*mdns//' \
				-i $D${sysconfdir}/nsswitch.conf
	fi
}

SYSTEMD_SERVICE:${PN} = "mdns.service"
INITSCRIPT_NAME = "mdns"

FILES_SOLIBSDEV = "${libdir}/libdns_sd.so"
FILES:${PN} += "${libdir}/libnss_mdns-0.2.so"

RPROVIDES:${PN} += "libdns_sd.so"
