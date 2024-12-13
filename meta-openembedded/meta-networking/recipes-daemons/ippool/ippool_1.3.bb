SUMMARY = "An IP address pool manager"
DESCRIPTION = "IpPool is implemented as a separate server daemon \
to allow any application to use its address pools. This makes it possible \
to define address pools that are shared by PPP, L2TP, PPTP etc. It may be \
useful in some VPN server setups. IpPool comes with a command line \
management application, ippoolconfig to manage and query address pool \
status. A pppd plugin is supplied which allows pppd to request IP \
addresses from ippoold. \
"
HOMEPAGE = "http://www.openl2tp.org/"
SECTION = "console/network"
LICENSE = "GPL-2.0-or-later"

SRC_URI = "https://sourceforge.net/projects/openl2tp/files/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://runtest.sh \
           file://ippool.service \
           file://ippool_usl_timer.patch \
           file://ippool_parallel_make_and_pic.patch \
           file://ippool_init.d.patch \
           file://always_syslog.patch \
           file://makefile-add-ldflags.patch \
           file://0001-usl_timer-Check-for-return-value-of-write-API.patch \
           file://0001-Respect-flags-from-env.patch \
           file://0001-read-returns-ssize_t.patch \
           file://0002-Mark-first-element-of-a-string-as-null.patch \
           file://0003-cli-Mark-return-of-strtol-as-long-int.patch \
           file://0002-link-with-libtirpc.patch \
           file://0003-musl-fixes.patch \
           file://strncpy-truncation.patch \
           file://0001-pppd-ippool.c-Fix-type-casting-issues-between-in_add.patch \
           file://0002-ippool_rpc_server.c-Add-missing-prototype-for-ippool.patch \
           file://0001-Use-unsigned-int-type-for-1-bit-integer-bitfield.patch \
           file://0001-ippool-Port-to-ppp-2.5-APIs.patch \
           "

LIC_FILES_CHKSUM = "file://LICENSE;md5=4c59283b82fc2b166455e0fc23c71c6f"
SRC_URI[md5sum] = "e2401e65db26a3764585b97212888fae"
SRC_URI[sha256sum] = "d3eab7d6cad5da8ccc9d1e31d5303e27a39622c07bdb8fa3618eea314412075b"

inherit systemd

DEPENDS = "readline ppp ncurses gzip-native rpcsvc-proto-native libtirpc"
RDEPENDS:${PN} = "rpcbind"

EXTRA_OEMAKE = "CC='${CC} ${CFLAGS}' AS='${AS}' LD='${LD} ${LDFLAGS}' AR='${AR}' NM='${NM}' STRIP='${STRIP}'"
EXTRA_OEMAKE += "PPPD_VERSION=${PPPD_VERSION} SYS_LIBDIR=${libdir}"
# enable self tests
EXTRA_OEMAKE += "IPPOOL_TEST=y"

CPPFLAGS += "${SELECTED_OPTIMIZATION} -I${STAGING_INCDIR}/tirpc"

SYSTEMD_SERVICE:${PN} = "ippool.service"

do_compile:prepend() {
    sed -i -e "s:-I/usr/include/pppd:-I=/usr/include/pppd:" ${S}/pppd/Makefile
}


do_install() {
    oe_runmake DESTDIR=${D} install

    install -D -m 0755 ${S}/debian/init.d ${D}${sysconfdir}/init.d/ippoold
    install -D -m 0644 ${UNPACKDIR}/ippool.service ${D}${systemd_system_unitdir}/ippool.service
    sed -i -e 's:@SBINDIR@:${sbindir}:g' ${D}${systemd_system_unitdir}/ippool.service

    # install self test
    install -d ${D}/opt/${BPN}
    install ${S}/test/all.tcl  ${S}/test/ippool.test \
        ${S}/test/test_procs.tcl ${D}/opt/${BPN}
    install ${UNPACKDIR}/runtest.sh ${D}/opt/${BPN}
    # fix the ../ippoolconfig in test_procs.tcl
    sed -i -e "s:../ippoolconfig:ippoolconfig:" \
        ${D}/opt/${BPN}/test_procs.tcl
}


PACKAGES =+ "${PN}-test"

FILES:${PN} += "${libdir}/pppd/${PPPD_VERSION}/ippool.so"
FILES:${PN}-dbg += "${libdir}/pppd/${PPPD_VERSION}/.debug/ippool.so"
FILES:${PN}-test = "/opt/${BPN}"

# needs tcl to run tests
RDEPENDS:${PN}-test += "tcl ${BPN}"

PPPD_VERSION="${@get_ppp_version(d)}"

def get_ppp_version(d):
    import re

    pppd_plugin = d.expand('${STAGING_LIBDIR}/pppd')
    if not os.path.isdir(pppd_plugin):
        return None

    bb.debug(1, "pppd plugin dir %s" % pppd_plugin)
    r = re.compile(r"\d*\.\d*\.\d*")
    for f in os.listdir(pppd_plugin):
        if os.path.isdir(os.path.join(pppd_plugin, f)):
            ma = r.match(f)
            if ma:
                bb.debug(1, "pppd version dir %s" % f)
                return f
            else:
                bb.debug(1, "under pppd plugin dir %s" % f)

    return None

