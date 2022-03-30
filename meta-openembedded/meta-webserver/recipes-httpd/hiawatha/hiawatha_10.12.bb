SUMMARY = "Lightweight secure web server"
HOMEPAGE = "http://www.hiawatha-webserver.org"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "libxml2 libxslt virtual/crypt"

SECTION = "net"

SRC_URI = "http://hiawatha-webserver.org/files/hiawatha-10/${BP}.tar.gz \
           file://hiawatha-init \
           file://hiawatha.service "

SRC_URI[md5sum] = "d9e282be06ed456207726b7ac0df9d48"
SRC_URI[sha256sum] = "61bf41146c51244769984135529fcffd0f6cb92be18dc12d460effc42f19f50d"

INITSCRIPT_NAME = "hiawatha"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE:${PN} = "hiawatha.service"

inherit cmake update-rc.d systemd

EXTRA_OECMAKE = " -DENABLE_IPV6=OFF \
                  -DENABLE_CACHE=OFF \
                  -DENABLE_DEBUG=OFF \
                  -DENABLE_TLS=OFF \
                  -DENABLE_TOOLKIT=OFF \
                  -DENABLE_CHROOT=OFF \
                  -DENABLE_XSLT=ON \
                  -DENABLE_TOMAHAWK=OFF \
                  -DCMAKE_INSTALL_MANDIR=${mandir} \
                  -DCMAKE_INSTALL_BINDIR=${bindir} \
                  -DCMAKE_INSTALL_SBINDIR=${sbindir} \
                  -DCMAKE_INSTALL_SYSCONFDIR=${sysconfdir} \
                  -DCMAKE_INSTALL_LIBDIR=${libdir} \
                  -DCMAKE_INSTALL_FULL_LOCALSTATEDIR=${localstatedir}"

do_install:append() {
    # Copy over init script and sed in the correct sbin path
    sed -i 's,sed_sbin_path,${sbindir},' ${WORKDIR}/hiawatha-init
    mkdir -p ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/hiawatha-init ${D}${sysconfdir}/init.d/hiawatha

    # configure php-fcgi to have a working configuration
    # by default if php is installed
    echo "Server = ${bindir}/php-cgi ; 2 ; 127.0.0.1:2005 ; nobody:nobody ; ${sysconfdir}/php/hiawatha-php5/php.ini" >> ${D}${sysconfdir}/hiawatha/php-fcgi.conf

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/hiawatha.service ${D}/${systemd_unitdir}/system
    fi

    # /var/log/hiawatha and /var/lib/hiawatha needs to be created in runtime.
    # Use rmdir to catch if upstream stops creating these dirs, or adds
    # something else in /var/log.
    rmdir ${D}${localstatedir}/log/${BPN} ${D}${localstatedir}/log
    rmdir ${D}${localstatedir}/run
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    # Create /var/log/hiawatha at runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN} - - - -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d root root 0755 ${localstatedir}/log/${BPN} none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi

}

CONFFILES:${PN} = " \
    ${sysconfdir}/hiawatha/cgi-wrapper.conf \
    ${sysconfdir}/hiawatha/hiawatha.conf \
    ${sysconfdir}/hiawatha/index.xslt \
    ${sysconfdir}/hiawatha/mimetype.conf \
    ${sysconfdir}/hiawatha/php-fcgi.conf \
"

FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"
FILES:${PN}-dev = "${libdir}/hiawatha/*${SOLIBSDEV}"
