DESCRIPTION = "Monit is a free open source utility for managing and monitoring, \
processes, programs, files, directories and filesystems on a UNIX system. \
Monit conducts automatic maintenance and repair and can execute meaningful \
causal actions in error situations."

HOMEPAGE = "http://mmonit.com/monit/"

LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=79ca9f26f1ecb1e377e90383109ede64 \
                    file://libmonit/COPYING;md5=44aaa0a664332e9568add09f1ddb01cb \
                    "

SRC_URI = " \
	https://mmonit.com/monit/dist/monit-${PV}.tar.gz \
	file://monit \
	file://monitrc \
"

SRC_URI[sha256sum] = "4dfef54329e63d9772a9e1c36ac99bc41173b79963dc0d8235f2c32f4b9e078f"

DEPENDS = "zlib bison-native libnsl2 flex-native openssl virtual/crypt"

inherit autotools-brokensep systemd update-rc.d
# brokensep because | ../../monit-5.34.4/libmonit/src/util/Str.c:26:10: fatal error: Config.h: No such file or directory

EXTRA_AUTORECONF += "-I config"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

EXTRA_OECONF = "\
    libmonit_cv_setjmp_available=no \
    libmonit_cv_vsnprintf_c99_conformant=no \
    --with-ssl-lib-dir=${STAGING_LIBDIR} \
    --with-ssl-incl-dir=${STAGING_INCDIR} \
"

SYSTEMD_SERVICE:${PN} = "monit.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "monit"
INITSCRIPT_PARAMS:${PN} = "defaults 89"

do_install:append() {

    # Configuration file
    install -Dm 0600 ${UNPACKDIR}/monitrc ${D}${sysconfdir}/monitrc

    # SystemD
    install -Dm 0644 ${S}/system/startup/monit.service.in ${D}${systemd_system_unitdir}/monit.service
    sed -i -e 's,@prefix@,${exec_prefix},g' ${D}${systemd_unitdir}/system/monit.service

    # SysV
    install -Dm 0755 ${UNPACKDIR}/monit ${D}${sysconfdir}/init.d/monit
}
