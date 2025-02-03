SUMMARY = "A 802.1ab implementation (LLDP) to help you locate neighbors of all your equipments"
SECTION = "net/misc"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/ISC;md5=f3b90e78ea0cffb20bf5cca7947a896d"

DEPENDS = "libbsd libevent"

SRC_URI = "\
    http://media.luffy.cx/files/${BPN}/${BP}.tar.gz \
    file://lldpd.init.d \
    file://lldpd.default \
    file://run-ptest \
    "

SRC_URI[sha256sum] = "f87df3163d5e5138da901d055b384009785d1eb50fdb17a2343910fcf30a997f"

inherit autotools update-rc.d useradd systemd pkgconfig bash-completion github-releases ptest

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -g lldpd --shell /bin/false lldpd"
GROUPADD_PARAM:${PN} = "--system lldpd"

EXTRA_OECONF += "--without-embedded-libevent \
                 --disable-oldies \
                 --with-privsep-user=lldpd \
                 --with-privsep-group=lldpd \
                 --with-systemdsystemunitdir=${systemd_system_unitdir} \
                 --without-sysusersdir \
"

PACKAGECONFIG ??= "cdp fdp edp sonmp lldpmed dot1 dot3"
PACKAGECONFIG[xml] = "--with-xml,--without-xml,libxm2"
PACKAGECONFIG[snmp] = "--with-snmp,--without-snmp,net-snmp"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[seccomp] = "--with-seccomp,--without-seccomp,libseccomp"
PACKAGECONFIG[cdp] = "--enable-cdp,--disable-cdp"
PACKAGECONFIG[fdp] = "--enable-fdp,--disable-fdp"
PACKAGECONFIG[edp] = "--enable-edp,--disable-edp"
PACKAGECONFIG[sonmp] = "--enable-sonmp,--disable-sonmp"
PACKAGECONFIG[lldpmed] = "--enable-lldpmed,--disable-lldpmed"
PACKAGECONFIG[dot1] = "--enable-dot1,--disable-dot1"
PACKAGECONFIG[dot3] = "--enable-dot3,--disable-dot3"
PACKAGECONFIG[custom] = "--enable-custom,--disable-custom"

INITSCRIPT_NAME = "lldpd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE:${PN} = "lldpd.service"

do_install:append() {
    install -Dm 0755 ${UNPACKDIR}/lldpd.init.d ${D}${sysconfdir}/init.d/lldpd
    install -Dm 0644 ${UNPACKDIR}/lldpd.default ${D}${sysconfdir}/default/lldpd
    # Make an empty configuration file
    touch ${D}${sysconfdir}/lldpd.conf
}

PACKAGES =+ "${PN}-zsh-completion"

FILES:${PN} += "${libdir}/sysusers.d"
RDEPENDS:${PN} += "os-release"

FILES:${PN}-zsh-completion += "${datadir}/zsh/"
# FIXME: zsh is broken in meta-oe so this cannot be enabled for now
#RDEPENDS:${PN}-zsh-completion += "zsh"

RDEPENDS:${PN}-ptest = "libcheck"
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'libcheck', '', d)}"

TESTDIR = "tests"
do_compile_ptest () {
    # hack to remove the call to `make check-TESTS`
    sed -i 's/$(MAKE) $(AM_MAKEFLAGS) check-TESTS//g' ${TESTDIR}/Makefile
    oe_runmake check
}

do_install_ptest () {
    # install the tests
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}
    # remove the object files
    rm ${D}${PTEST_PATH}/${TESTDIR}/*.o
}
