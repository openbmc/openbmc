SUMMARY = "Lightweight high-performance web server"
HOMEPAGE = "http://www.lighttpd.net/"
DESCRIPTION = "Lightweight high-performance web server is designed and optimized for high performance environments. With a small memory footprint compared to other web-servers, effective management of the cpu-load, and advanced feature set (FastCGI, SCGI, Auth, Output-Compression, URL-Rewriting and many more)"
BUGTRACKER = "http://redmine.lighttpd.net/projects/lighttpd/issues"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SECTION = "net"
RDEPENDS:${PN} = "lighttpd-module-dirlisting \
                  lighttpd-module-indexfile \
                  lighttpd-module-staticfile"
RRECOMMENDS:${PN} = "lighttpd-module-access \
                     lighttpd-module-accesslog"

SRC_URI = "http://download.lighttpd.net/lighttpd/releases-1.4.x/lighttpd-${PV}.tar.xz \
           file://index.html.lighttpd \
           file://lighttpd.conf \
           file://lighttpd \
           "

SRC_URI[sha256sum] = "7e04d767f51a8d824b32e2483ef2950982920d427d1272ef4667f49d6f89f358"

DEPENDS = "virtual/crypt"

PACKAGECONFIG ??= "openssl pcre zlib \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"

PACKAGECONFIG[libev] = "-Dwith_libev=true,-Dwith_libev=false,libev"
PACKAGECONFIG[mysql] = "-Dwith_mysql=true,-Dwith_mysql=false,mariadb"
PACKAGECONFIG[ldap] = "-Dwith_ldap=true,-Dwith_ldap=false,openldap"
PACKAGECONFIG[attr] = "-Dwith_xattr=true,-Dwith_xattr=false,attr"
PACKAGECONFIG[openssl] = "-Dwith_openssl=true,-Dwith_openssl=false,openssl"
PACKAGECONFIG[krb5] = "-Dwith_krb5=true,-Dwith_krb5=false,krb5"
PACKAGECONFIG[pcre] = "-Dwith_pcre=true,-Dwith_pcre=false,libpcre"
PACKAGECONFIG[zlib] = "-Dwith_zlib=true,-Dwith_zlib=false,zlib"
PACKAGECONFIG[bzip2] = "-Dwith_bzip=true,-Dwith_bzip=false,bzip2"
PACKAGECONFIG[webdav-props] = "-Dwith_webdav_props=true,-Dwith_webdav_props=false,libxml2 sqlite3"
PACKAGECONFIG[webdav-locks] = "-Dwith_webdav_locks=true,-Dwith_webdav_locks=false,util-linux"
PACKAGECONFIG[lua] = "-Dwith_lua=true,-Dwith_lua=false,lua"
PACKAGECONFIG[zstd] = "-Dwith_zstd=true,-Dwith_zstd=false,zstd"

inherit meson pkgconfig update-rc.d gettext systemd

INITSCRIPT_NAME = "lighttpd"
INITSCRIPT_PARAMS = "defaults 70"

SYSTEMD_SERVICE:${PN} = "lighttpd.service"

do_install:append() {
	install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/lighttpd ${D}${sysconfdir}/lighttpd.d ${D}/www/pages/dav
	install -m 0755 ${WORKDIR}/lighttpd ${D}${sysconfdir}/init.d
	install -m 0644 ${WORKDIR}/lighttpd.conf ${D}${sysconfdir}/lighttpd
	install -m 0644 ${WORKDIR}/index.html.lighttpd ${D}/www/pages/index.html

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
