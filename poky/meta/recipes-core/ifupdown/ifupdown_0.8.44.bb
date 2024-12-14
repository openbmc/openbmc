SUMMARY = "ifupdown: basic ifup and ifdown used by initscripts"
HOMEPAGE = "https://salsa.debian.org/debian/ifupdown"
DESCRIPTION = "High level tools to configure network interfaces \
This package provides the tools ifup and ifdown which may be used to \
configure (or, respectively, deconfigure) network interfaces, based on \
the file /etc/network/interfaces."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://salsa.debian.org/debian/ifupdown.git;protocol=https;branch=master \
           file://99_network \
           file://run-ptest \
           file://0001-Define-FNM_EXTMATCH-for-musl.patch \
           file://0001-Makefile-do-not-use-dpkg-for-determining-OS-type.patch \
           file://0001-ifupdown-skip-wrong-test-case.patch \
           file://defn2-c-man-don-t-rely-on-dpkg-architecture-to-set-a.patch \
           file://tweak-ptest-script.patch \
           "
SRCREV = "7d44f9ce6717a4a496fd46f28c52e12dbf5bafdd"

S = "${WORKDIR}/git"


inherit ptest update-alternatives

do_compile () {
	chmod a+rx *.pl *.sh
	oe_runmake 'CC=${CC}' "-Wall -W -D'IFUPDOWN_VERSION=\"${PV}\"'"
}

do_install () {
	install -d ${D}${mandir}/man8 \
		  ${D}${mandir}/man5 \
		  ${D}${base_sbindir}

	# If volatiles are used, then we'll also need /run/network there too.
	install -d ${D}/etc/default/volatiles
	install -m 0644 ${UNPACKDIR}/99_network ${D}/etc/default/volatiles

	install -m 0755 ifup ${D}${base_sbindir}/
	ln ${D}${base_sbindir}/ifup ${D}${base_sbindir}/ifdown
	install -m 0644 ifup.8 ${D}${mandir}/man8
	install -m 0644 interfaces.5 ${D}${mandir}/man5
	cd ${D}${mandir}/man8 && ln -s ifup.8 ifdown.8

	install -d ${D}${sysconfdir}/network/if-pre-up.d
	install -d ${D}${sysconfdir}/network/if-up.d
	install -d ${D}${sysconfdir}/network/if-down.d
	install -d ${D}${sysconfdir}/network/if-post-down.d
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${S}/tests/testbuild-linux ${D}${PTEST_PATH}/tests/
    cp -r ${S}/tests/linux ${D}${PTEST_PATH}/tests/
}

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "ifup ifdown"

ALTERNATIVE_LINK_NAME[ifup] = "${base_sbindir}/ifup"
ALTERNATIVE_LINK_NAME[ifdown] = "${base_sbindir}/ifdown"
