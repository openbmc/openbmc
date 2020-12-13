SUMMARY = "ifupdown: basic ifup and ifdown used by initscripts"
DESCRIPTION = "High level tools to configure network interfaces \
This package provides the tools ifup and ifdown which may be used to \
configure (or, respectively, deconfigure) network interfaces, based on \
the file /etc/network/interfaces."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://salsa.debian.org/debian/ifupdown.git;protocol=https \
           file://defn2-c-man-don-t-rely-on-dpkg-architecture-to-set-a.patch \
           file://99_network \
           file://0001-Define-FNM_EXTMATCH-for-musl.patch \
           file://0001-Makefile-do-not-use-dpkg-for-determining-OS-type.patch \
           file://run-ptest \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'file://tweak-ptest-script.patch', '', d)} \
           "
SRCREV = "c73226073e2b13970ca613b20a13b9c0253bf9da"

S = "${WORKDIR}/git"


inherit ptest update-alternatives

do_compile () {
	chmod a+rx *.pl *.sh
	oe_runmake 'CC=${CC}' "CFLAGS=${CFLAGS} -Wall -W -D'IFUPDOWN_VERSION=\"${PV}\"'"
}

do_install () {
	install -d ${D}${mandir}/man8 \
		  ${D}${mandir}/man5 \
		  ${D}${base_sbindir}

	# If volatiles are used, then we'll also need /run/network there too.
	install -d ${D}/etc/default/volatiles
	install -m 0644 ${WORKDIR}/99_network ${D}/etc/default/volatiles

	install -m 0755 ifup ${D}${base_sbindir}/
	ln ${D}${base_sbindir}/ifup ${D}${base_sbindir}/ifdown
	install -m 0644 ifup.8 ${D}${mandir}/man8
	install -m 0644 interfaces.5 ${D}${mandir}/man5
	cd ${D}${mandir}/man8 && ln -s ifup.8 ifdown.8
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    cp -r ${S}/tests/testbuild-linux ${D}${PTEST_PATH}/tests/
    cp -r ${S}/tests/linux ${D}${PTEST_PATH}/tests/
}

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "ifup ifdown"

ALTERNATIVE_LINK_NAME[ifup] = "${base_sbindir}/ifup"
ALTERNATIVE_LINK_NAME[ifdown] = "${base_sbindir}/ifdown"
