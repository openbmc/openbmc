SUMMARY = "Opensource Implementation of WS-Management"
DESCRIPTION = "Openwsman is a project intended to provide an open-source \
implementation of the Web Services Management specipication \
(WS-Management) and to expose system management information on the \
Linux operating system using the WS-Management protocol. WS-Management \
is based on a suite of web services specifications and usage \
requirements that exposes a set of operations focused on and covers \
all system management aspects. \
Openwsman Server and service libraries"
HOMEPAGE = "http://www.openwsman.org/"
SECTION = "Applications/System"

DEPENDS = "curl libxml2 openssl libpam"

SRCREV = "ed7a119e036c53078d70fd85936d94dc9b9b98be"
PV = "2.6.2"

SRC_URI = "git://github.com/Openwsman/openwsman.git;protocol=http \
           file://libssl-is-required-if-eventint-supported.patch \
           file://openwsmand.service"

S = "${WORKDIR}/git"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4f53d4c6cf73b9d43186ce3be6dd0ba"

inherit systemd cmake pkgconfig pythonnative perlnative

SYSTEMD_SERVICE_${PN} = "openwsmand.service"
SYSTEMD_AUTO_ENABLE = "disable"

LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', " -fuse-ld=bfd ", '', d)}"

EXTRA_OECMAKE = "-DBUILD_BINDINGS=NO \
                 -DBUILD_LIBCIM=NO \
                 -DBUILD_PERL=YES \
                 -DCMAKE_INSTALL_PREFIX=${prefix} \
                 -DLIB=${baselib} \
                "

do_configure_prepend() {
    export HOST_SYS=${HOST_SYS}
    export BUILD_SYS=${BUILD_SYS}
    export STAGING_INCDIR=${STAGING_INCDIR}
    export STAGING_LIBDIR=${STAGING_LIBDIR}
}

do_install_append() {
    install -d ${D}/${sysconfdir}/init.d
    install -m 755 ${B}/etc/init/openwsmand.sh ${D}/${sysconfdir}/init.d/openwsmand
    ln -sf ${sysconfdir}/init.d/openwsmand ${D}/${sbindir}/rcopenwsmand
    chmod 755 ${D}/${sysconfdir}/openwsman/owsmangencert.sh
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/openwsmand.service ${D}/${systemd_unitdir}/system

        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/openwsmand.service
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/openwsmand.service
        sed -i -e 's,@LOCALSTATEDIR@,${localstatedir},g' ${D}${systemd_unitdir}/system/openwsmand.service
    fi
}

FILES_${PN}-dbg += "${libdir}/openwsman/plugins/.debug/ \
                    ${libdir}/openwsman/authenticators/.debug/ \
                   "

INSANE_SKIP_${PN} = "dev-so"
