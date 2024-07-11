SUMMARY = "TrouSerS - An open-source TCG Software Stack implementation."
LICENSE = "BSD-3-Clause"
HOMEPAGE = "http://sourceforge.net/projects/trousers/"
LIC_FILES_CHKSUM = "file://README;startline=3;endline=4;md5=2af28fbed0832e4d83a9e6dd68bb4413"
SECTION = "security/tpm"

DEPENDS = "openssl"

SRCREV = "94144b0a1dcef6e31845d6c319e9bd7357208eb9"
PV = "0.3.15+git${SRCPV}"

SRC_URI = " \
	git://git.code.sf.net/p/trousers/trousers;branch=master \
    	file://trousers.init.sh \
    	file://trousers-udev.rules \
    	file://tcsd.service \
        file://get-user-ps-path-use-POSIX-getpwent-instead-of-getpwe.patch \
        file://0001-build-don-t-override-localstatedir-mandir-sysconfdir.patch \
    	"

S = "${WORKDIR}/git"

inherit autotools pkgconfig useradd update-rc.d ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','', d)}

PACKAGECONFIG ?= "gmp "
PACKAGECONFIG[gmp] = "--with-gmp, --with-gmp=no, gmp"
PACKAGECONFIG[gtk] = "--with-gui=gtk, --with-gui=none, gtk+"

do_install () {
    oe_runmake DESTDIR=${D} install
}

do_install:append() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/trousers.init.sh ${D}${sysconfdir}/init.d/trousers
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/trousers-udev.rules ${D}${sysconfdir}/udev/rules.d/45-trousers.rules

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/tcsd.service ${D}${systemd_unitdir}/system/
        sed -i -e 's#@SBINDIR@#${sbindir}#g' ${D}${systemd_unitdir}/system/tcsd.service
    fi        
}

CONFFILES:${PN} += "${sysconfig}/tcsd.conf"

PROVIDES = "${PACKAGES}"
PACKAGES = " \
	libtspi \
	libtspi-dbg \
	libtspi-dev \
	libtspi-doc \
	libtspi-staticdev \
	trousers \
	trousers-dbg \
	trousers-doc \
	"

# libtspi needs tcsd for most (all?) operations, so suggest to
# install that.
RRECOMMENDS:libtspi = "${PN}"

FILES:libtspi = " \
	${libdir}/*.so.1 \
	${libdir}/*.so.1.2.0 \
	"
FILES:libtspi-dbg = " \
	${libdir}/.debug \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/tspi \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/trspi \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/include/*.h \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/include/tss \
	"
FILES:libtspi-dev = " \
	${includedir} \
	${libdir}/*.so \
	"
FILES:libtspi-doc = " \
	${mandir}/man3 \
	"
FILES:libtspi-staticdev = " \
	${libdir}/*.la \
	${libdir}/*.a \
	"
FILES:${PN} = " \
	${sbindir}/tcsd \
	${sysconfdir} \
	${localstatedir} \
	"

FILES:${PN}-dev += "${libdir}/trousers"

FILES:${PN}-dbg = " \
	${sbindir}/.debug \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/tcs \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/tcsd \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/tddl \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/trousers \
	${prefix}/src/debug/${PN}/${PV}-${PR}/git/src/include/trousers \
	"
FILES:${PN}-doc = " \
	${mandir}/man5 \
	${mandir}/man8 \
	"

FILES:${PN} += "${systemd_unitdir}/*" 

INITSCRIPT_NAME = "trousers"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 19 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system tss"
USERADD_PARAM:${PN} = "--system -M -d /var/lib/tpm -s /bin/false -g tss tss"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "tcsd.service"
SYSTEMD_AUTO_ENABLE = "disable"

BBCLASSEXTEND = "native"
