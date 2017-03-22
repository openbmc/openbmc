SUMMARY = "Lightweight secure web server"
HOMEPAGE = "http://www.hiawatha-webserver.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "libxml2 libxslt"

SECTION = "net"

SRC_URI = "http://hiawatha-webserver.org/files/${BP}.tar.gz \
           file://hiawatha-init \
           file://hiawatha.service "

SRC_URI[md5sum] = "31567dfc18d435bd419be279f7970019"
SRC_URI[sha256sum] = "99d185fb16bb1ab706724494d3cac86464f1485ed4b0fff09a192eca6da5ff8e"

INITSCRIPT_NAME = "hiawatha"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE_${PN} = "hiawatha.service"

inherit cmake update-rc.d systemd

EXTRA_OECMAKE = " -DENABLE_IPV6=OFF \
                  -DENABLE_CACHE=OFF \
                  -DENABLE_DEBUG=OFF \
                  -DENABLE_SSL=OFF \
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

do_install_append() {
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

    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}" "${D}${localstatedir}/run"
}

CONFFILES_${PN} = " \
    ${sysconfdir}/hiawatha/cgi-wrapper.conf \
    ${sysconfdir}/hiawatha/hiawatha.conf \
    ${sysconfdir}/hiawatha/index.xslt \
    ${sysconfdir}/hiawatha/mimetype.conf \
    ${sysconfdir}/hiawatha/php-fcgi.conf \
"

FILES_${PN}-dev = "${libdir}/hiawatha/*${SOLIBSDEV}"
