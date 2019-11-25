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

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRCREV = "d8eba6cb6682b59d84ca1da67a523520b879ade6"

SRC_URI = "git://github.com/Openwsman/openwsman.git \
           file://libssl-is-required-if-eventint-supported.patch \
           file://openwsmand.service \
           file://0001-lock.c-Define-PTHREAD_MUTEX_RECURSIVE_NP-if-undefine.patch \
           "

S = "${WORKDIR}/git"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4f53d4c6cf73b9d43186ce3be6dd0ba"

inherit systemd cmake pkgconfig python3native perlnative

SYSTEMD_SERVICE_${PN} = "openwsmand.service"
SYSTEMD_AUTO_ENABLE = "disable"

LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', " -fuse-ld=bfd ", '', d)}"

EXTRA_OECMAKE = "-DBUILD_BINDINGS=NO \
                 -DBUILD_LIBCIM=NO \
                 -DBUILD_PERL=YES \
                 -DBUILD_PYTHON3=YES \
                 -DBUILD_PYTHON=NO \
                 -DCMAKE_INSTALL_PREFIX=${prefix} \
                 -DLIB=${baselib} \
                "

do_configure_prepend() {
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
RDEPENDS_${PN} = "ruby"
