SUMMARY = "Lightweight high-performance web server"
HOMEPAGE = "http://www.lighttpd.net/"
DESCRIPTION = "Lightweight high-performance web server is designed and optimized for high performance environments. With a small memory footprint compared to other web-servers, effective management of the cpu-load, and advanced feature set (FastCGI, SCGI, Auth, Output-Compression, URL-Rewriting and many more)"
BUGTRACKER = "http://redmine.lighttpd.net/projects/lighttpd/issues"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SECTION = "net"
RDEPENDS:${PN} = "lighttpd-module-dirlisting"
RRECOMMENDS:${PN} = "lighttpd-module-accesslog"

SRC_URI = "http://download.lighttpd.net/lighttpd/releases-1.4.x/lighttpd-${PV}.tar.xz \
           file://index.html.lighttpd \
           file://lighttpd.conf \
           file://lighttpd \
           "

SRC_URI[sha256sum] = "abfe74391f9cbd66ab154ea07e64f194dbe7e906ef4ed47eb3b0f3b46246c962"

DEPENDS = "virtual/crypt"

PACKAGECONFIG ??= "openssl pcre zlib \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"

PACKAGECONFIG[mysql] = "-Dwith_mysql=enabled,-Dwith_mysql=disabled,mariadb"
PACKAGECONFIG[ldap] = "-Dwith_ldap=enabled,-Dwith_ldap=disabled,openldap"
PACKAGECONFIG[attr] = "-Dwith_xattr=true,-Dwith_xattr=false,attr"
PACKAGECONFIG[openssl] = "-Dwith_openssl=true,-Dwith_openssl=false,openssl"
PACKAGECONFIG[krb5] = "-Dwith_krb5=enabled,-Dwith_krb5=disabled,krb5"
PACKAGECONFIG[pcre] = "-Dwith_pcre=pcre2,-Dwith_pcre=disabled,libpcre2"
PACKAGECONFIG[zlib] = "-Dwith_zlib=enabled,-Dwith_zlib=disabled,zlib"
PACKAGECONFIG[bzip2] = "-Dwith_bzip=enabled,-Dwith_bzip=disabled,bzip2"
PACKAGECONFIG[webdav-props] = "-Dwith_webdav_props=enabled,-Dwith_webdav_props=disabled,libxml2 sqlite3"
PACKAGECONFIG[webdav-locks] = "-Dwith_webdav_locks=enabled,-Dwith_webdav_locks=disabled,util-linux"
PACKAGECONFIG[lua] = "-Dwith_lua=true,-Dwith_lua=false,lua"
PACKAGECONFIG[zstd] = "-Dwith_zstd=enabled,-Dwith_zstd=disabled,zstd"

inherit meson pkgconfig update-rc.d gettext systemd

INITSCRIPT_NAME = "lighttpd"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE:${PN} = "lighttpd.service"

do_install:append() {
	install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/lighttpd ${D}${sysconfdir}/lighttpd.d ${D}/www/pages/dav
	install -m 0755 ${UNPACKDIR}/lighttpd ${D}${sysconfdir}/init.d
	install -m 0644 ${UNPACKDIR}/lighttpd.conf ${D}${sysconfdir}/lighttpd
	install -m 0644 ${UNPACKDIR}/index.html.lighttpd ${D}/www/pages/index.html

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/doc/systemd/lighttpd.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' \
		-e 's,@SYSCONFDIR@,${sysconfdir},g' \
		-e 's,@BASE_BINDIR@,${base_bindir},g' \
		${D}${systemd_system_unitdir}/lighttpd.service
	#For FHS compliance, create symbolic links to /var/log and /var/tmp for logs and temporary data
	ln -sf ${localstatedir}/log ${D}/www/logs
	ln -sf ${localstatedir}/tmp ${D}/www/var
}

# bitbake.conf sets ${libdir}/${BPN}/* in FILES, which messes up the module split.
# So we re-do the variable.
FILES:${PN} = "${sysconfdir} /www ${sbindir}"

CONFFILES:${PN} = "${sysconfdir}/lighttpd/lighttpd.conf"

PACKAGES_DYNAMIC += "^lighttpd-module-.*"

python populate_packages:prepend () {
    lighttpd_libdir = d.expand('${prefix}/lib/lighttpd')
    do_split_packages(d, lighttpd_libdir, r'^mod_(.*)\.so$', 'lighttpd-module-%s', 'Lighttpd module for %s', extra_depends='')
}
