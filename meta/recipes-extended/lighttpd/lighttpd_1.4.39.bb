SUMMARY = "Lightweight high-performance web server"
HOMEPAGE = "http://www.lighttpd.net/"
BUGTRACKER = "http://redmine.lighttpd.net/projects/lighttpd/issues"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SECTION = "net"
DEPENDS = "zlib libpcre"
RDEPENDS_${PN} += " \
               lighttpd-module-access \
               lighttpd-module-accesslog \
               lighttpd-module-indexfile \
               lighttpd-module-dirlisting \
               lighttpd-module-staticfile \
"

SRC_URI = "http://download.lighttpd.net/lighttpd/releases-1.4.x/lighttpd-${PV}.tar.xz \
        file://index.html.lighttpd \
        file://lighttpd.conf \
        file://lighttpd \
        file://lighttpd.service \
        file://pkgconfig.patch \
        "

SRC_URI[md5sum] = "63c7563be1c7a7a9819a51f07f1af8b2"
SRC_URI[sha256sum] = "7eb9a1853c3d6dd5851682b0733a729ba4158d6bdff80974d5ef5f1f6887365b"

PACKAGECONFIG ??= "openssl"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"

EXTRA_OECONF = " \
             --without-bzip2 \
             --without-ldap \
             --without-lua \
             --without-memcache \
             --with-pcre \
             --without-webdav-props \
             --without-webdav-locks \
             --disable-static \
"

inherit autotools pkgconfig update-rc.d gettext systemd

INITSCRIPT_NAME = "lighttpd"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE_${PN} = "lighttpd.service"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/lighttpd.d ${D}/www/pages/dav
	install -m 0755 ${WORKDIR}/lighttpd ${D}${sysconfdir}/init.d
	install -m 0644 ${WORKDIR}/lighttpd.conf ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/index.html.lighttpd ${D}/www/pages/index.html

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/lighttpd.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@SYSCONFDIR@,${sysconfdir},g' \
		-e 's,@BASE_BINDIR@,${base_bindir},g' \
		${D}${systemd_unitdir}/system/lighttpd.service
	#For FHS compliance, create symbolic links to /var/log and /var/tmp for logs and temporary data
	ln -sf ${localstatedir}/log ${D}/www/logs
	ln -sf ${localstatedir}/tmp ${D}/www/var
}

FILES_${PN} += "${sysconfdir} /www"

CONFFILES_${PN} = "${sysconfdir}/lighttpd.conf"

PACKAGES_DYNAMIC += "^lighttpd-module-.*"

python populate_packages_prepend () {
    lighttpd_libdir = d.expand('${libdir}')
    do_split_packages(d, lighttpd_libdir, '^mod_(.*)\.so$', 'lighttpd-module-%s', 'Lighttpd module for %s', extra_depends='')
}
