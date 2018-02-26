DESCRIPTION = "The Apache HTTP Server is a powerful, efficient, and \
extensible web server."
SUMMARY = "Apache HTTP Server"
HOMEPAGE = "http://httpd.apache.org/"
DEPENDS = "libtool-native apache2-native openssl expat pcre apr apr-util"
SECTION = "net"
LICENSE = "Apache-2.0"

SRC_URI = "${APACHE_MIRROR}/httpd/httpd-${PV}.tar.bz2 \
           file://server-makefile.patch \
           file://httpd-2.4.1-corelimit.patch \
           file://httpd-2.4.4-export.patch \
           file://httpd-2.4.1-selinux.patch \
           file://apache-configure_perlbin.patch \
           file://replace-lynx-to-curl-in-apachectl-script.patch \
           file://apache-ssl-ltmain-rpath.patch \
           file://httpd-2.4.3-fix-race-issue-of-dir-install.patch \
           file://0001-configure-use-pkg-config-for-PCRE-detection.patch \
           file://configure-allow-to-disable-selinux-support.patch \
           file://init \
           file://apache2-volatile.conf \
           file://apache2.service \
           file://volatiles.04_apache2 \
          "

LIC_FILES_CHKSUM = "file://LICENSE;md5=dbff5a2b542fa58854455bf1a0b94b83"
SRC_URI[md5sum] = "97b6bbfa83c866dbe20ef317e3afd108"
SRC_URI[sha256sum] = "71fcc128238a690515bd8174d5330a5309161ef314a326ae45c7c15ed139c13a"

S = "${WORKDIR}/httpd-${PV}"

inherit autotools update-rc.d pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "apache2.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

SSTATE_SCAN_FILES += "apxs config_vars.mk config.nice"

CFLAGS_append = " -DPATH_MAX=4096"
CFLAGS_prepend = "-I${STAGING_INCDIR}/openssl "
EXTRA_OECONF = "--enable-ssl \
    --with-ssl=${STAGING_LIBDIR}/.. \
    --with-expat=${STAGING_LIBDIR}/.. \
    --with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
    --with-apr-util=${STAGING_BINDIR_CROSS}/apu-1-config \
    --enable-info \
    --enable-rewrite \
    --with-dbm=sdbm \
    --with-berkeley-db=no \
    --localstatedir=/var/${BPN} \
    --with-gdbm=no \
    --with-ndbm=no \
    --includedir=${includedir}/${BPN} \
    --datadir=${datadir}/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --libexecdir=${libdir}/${BPN}/modules \
    ap_cv_void_ptr_lt_long=no \
    --enable-mpms-shared \
    ac_cv_have_threadsafe_pollset=no"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "--enable-selinux --enable-layout=Debian --prefix=${base_prefix}/,--disable-selinux,libselinux,libselinux"
PACKAGECONFIG[openldap] = "--enable-ldap --enable-authnz-ldap,--disable-ldap --disable-authnz-ldap,openldap"

do_configure_prepend() {
        sed -i -e 's:$''{prefix}/usr/lib/cgi-bin:$''{libdir}/cgi-bin:g' ${S}/config.layout
}

do_install_append() {
    install -d ${D}/${sysconfdir}/init.d
    cat ${WORKDIR}/init | \
        sed -e 's,/usr/sbin/,${sbindir}/,g' \
            -e 's,/usr/bin/,${bindir}/,g' \
            -e 's,/usr/lib,${libdir}/,g' \
            -e 's,/etc/,${sysconfdir}/,g' \
            -e 's,/usr/,${prefix}/,g' > ${D}/${sysconfdir}/init.d/${BPN}
    chmod 755 ${D}/${sysconfdir}/init.d/${BPN}
    # remove the goofy original files...
    rm -rf ${D}/${sysconfdir}/${BPN}/original
    # Expat should be found in the staging area via DEPENDS...
    rm -f ${D}/${libdir}/libexpat.*

    install -d ${D}${sysconfdir}/${BPN}/conf.d
    install -d ${D}${sysconfdir}/${BPN}/modules.d

    # Ensure configuration file pulls in conf.d and modules.d
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/conf.d/*.conf" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/modules.d/*.load" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/modules.d/*.conf\n\n" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    # match with that is in init script
    printf "\nPidFile /run/httpd.pid" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    # Set 'ServerName' to fix error messages when restart apache service
    sed -i 's/^#ServerName www.example.com/ServerName localhost/' ${D}/${sysconfdir}/${BPN}/httpd.conf

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${WORKDIR}/apache2-volatile.conf ${D}${sysconfdir}/tmpfiles.d/
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${WORKDIR}/volatiles.04_apache2 ${D}${sysconfdir}/default/volatiles/04_apache2
    fi

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/apache2.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/apache2.service
    sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' ${D}${systemd_unitdir}/system/apache2.service

    chown -R root:root ${D}
}

do_install_append_class-target() {
    sed -i -e 's,${STAGING_DIR_HOST},,g' \
           -e 's,APU_INCLUDEDIR = .*,APU_INCLUDEDIR = ,g' \
           -e 's,APU_CONFIG = .*,APU_CONFIG = ,g' ${D}${datadir}/apache2/build/config_vars.mk

    sed -i -e 's,${STAGING_DIR_HOST},,g' \
           -e 's,".*/configure","configure",g' ${D}${datadir}/apache2/build/config.nice
    rm -rf ${D}${localstatedir}/run
}

SYSROOT_PREPROCESS_FUNCS += "apache_sysroot_preprocess"

apache_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${D}${bindir}/apxs ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -d ${SYSROOT_DESTDIR}${sbindir}/
    install -m 755 ${D}${sbindir}/apachectl ${SYSROOT_DESTDIR}${sbindir}/
    sed -i 's!my $installbuilddir = .*!my $installbuilddir = "${STAGING_DIR_HOST}/${datadir}/${BPN}/build";!' ${SYSROOT_DESTDIR}${bindir_crossscripts}/apxs
    sed -i 's!my $libtool = .*!my $libtool = "${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool";!' ${SYSROOT_DESTDIR}${bindir_crossscripts}/apxs

    sed -i 's!^APR_CONFIG = .*!APR_CONFIG = ${STAGING_BINDIR_CROSS}/apr-1-config!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!^APU_CONFIG = .*!APU_CONFIG = ${STAGING_BINDIR_CROSS}/apu-1-config!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!^includedir = .*!includedir = ${STAGING_INCDIR}/apache2!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!^CFLAGS = -I[^ ]*!CFLAGS = -I${STAGING_INCDIR}/openssl!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!^EXTRA_LDFLAGS = .*!EXTRA_LDFLAGS = -L${STAGING_LIBDIR}!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!^EXTRA_INCLUDES = .*!EXTRA_INCLUDES = -I$(includedir) -I. -I${STAGING_INCDIR}!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
    sed -i 's!--sysroot=[^ ]*!--sysroot=${STAGING_DIR_HOST}!' ${SYSROOT_DESTDIR}${datadir}/${BPN}/build/config_vars.mk
}

#
# implications - used by update-rc.d scripts
#
INITSCRIPT_NAME = "apache2"
INITSCRIPT_PARAMS = "defaults 91 20"
LEAD_SONAME = "libapr-1.so.0"

PACKAGES = "${PN}-scripts ${PN}-doc ${PN}-dev ${PN}-dbg ${PN}"

CONFFILES_${PN} = "${sysconfdir}/${BPN}/httpd.conf \
                   ${sysconfdir}/${BPN}/magic \
                   ${sysconfdir}/${BPN}/mime.types \
                   ${sysconfdir}/init.d/${BPN} "

# we override here rather than append so that .so links are
# included in the runtime package rather than here (-dev)
# and to get build, icons, error into the -dev package
FILES_${PN}-dev = "${datadir}/${BPN}/build \
                   ${datadir}/${BPN}/icons \
                   ${datadir}/${BPN}/error \
                   ${bindir}/apr-config ${bindir}/apu-config \
                   ${libdir}/apr*.exp \
                   ${includedir}/${BPN} \
                   ${libdir}/*.la \
                   ${libdir}/*.a \
                   ${bindir}/apxs \
                "


# manual to manual
FILES_${PN}-doc += " ${datadir}/${BPN}/manual"

FILES_${PN}-scripts += "${bindir}/dbmmanage"

#
# override this too - here is the default, less datadir
#
FILES_${PN} =  "${bindir} ${sbindir} ${libexecdir} ${libdir}/lib*.so.* ${sysconfdir} \
                ${sharedstatedir} ${localstatedir} /bin /sbin /lib/*.so* \
                ${libdir}/${BPN}"

# we want htdocs and cgi-bin to go with the binary
FILES_${PN} += "${datadir}/${BPN}/htdocs ${datadir}/${BPN}/cgi-bin"

#make sure the lone .so links also get wrapped in the base package
FILES_${PN} += "${libdir}/lib*.so ${libdir}/pkgconfig/*"

FILES_${PN}-dbg += "${libdir}/${BPN}/modules/.debug"

RDEPENDS_${PN} += "openssl libgcc"
RDEPENDS_${PN}-scripts += "perl ${PN}"
RDEPENDS_${PN}-dev = "perl"

FILES_${PN} += "${libdir}/cgi-bin"
FILES_${PN} += "${datadir}/${BPN}/"
