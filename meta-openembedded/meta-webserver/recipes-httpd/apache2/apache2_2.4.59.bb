DESCRIPTION = "The Apache HTTP Server is a powerful, efficient, and \
extensible web server."
SUMMARY = "Apache HTTP Server"
HOMEPAGE = "http://httpd.apache.org/"
SECTION = "net"
LICENSE = "Apache-2.0"

SRC_URI = "${APACHE_MIRROR}/httpd/httpd-${PV}.tar.bz2 \
           file://0001-configure-use-pkg-config-for-PCRE-detection.patch \
           file://0002-apache2-bump-up-the-core-size-limit-if-CoreDumpDirec.patch \
           file://0003-apache2-do-not-export-apr-apr-util-symbols-when-usin.patch \
           file://0004-apache2-log-the-SELinux-context-at-startup.patch \
           file://0005-replace-lynx-to-curl-in-apachectl-script.patch \
           file://0006-apache2-fix-the-race-issue-of-parallel-installation.patch \
           file://0007-apache2-allow-to-disable-selinux-support.patch \
           file://0008-Fix-perl-install-directory-to-usr-bin.patch \
           file://0009-support-apxs.in-force-destdir-to-be-empty-string.patch \
           file://0001-make_exports.awk-not-expose-the-path.patch \
          "

SRC_URI:append:class-target = " \
           file://0010-apache2-do-not-use-relative-path-for-gen_test_char.patch \
           file://init \
           file://apache2-volatile.conf \
           file://apache2.service \
           file://volatiles.04_apache2 \
           "

LIC_FILES_CHKSUM = "file://LICENSE;md5=bddeddfac80b2c9a882241d008bb41c3"
SRC_URI[sha256sum] = "ec51501ec480284ff52f637258135d333230a7d229c3afa6f6c2f9040e321323"

S = "${WORKDIR}/httpd-${PV}"

inherit autotools update-rc.d pkgconfig systemd multilib_script multilib_header

DEPENDS = "openssl expat pcre apr apr-util apache2-native "

CVE_PRODUCT = "apache:http_server"

SSTATE_SCAN_FILES += "apxs config_vars.mk config.nice"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux,libselinux"
PACKAGECONFIG[openldap] = "--enable-ldap --enable-authnz-ldap,--disable-ldap --disable-authnz-ldap,openldap"
PACKAGECONFIG[zlib] = "--enable-deflate,,zlib,zlib"

CFLAGS:append = " -DPATH_MAX=4096"

EXTRA_OECONF:class-target = "\
    --enable-layout=Debian \
    --prefix=${base_prefix} \
    --exec_prefix=${exec_prefix} \
    --includedir=${includedir}/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --datadir=${datadir}/${BPN} \
    --libdir=${libdir} \
    --libexecdir=${libexecdir}/${BPN}/modules \
    --localstatedir=${localstatedir} \
    --enable-ssl \
    --with-dbm=sdbm \
    --with-gdbm=no \
    --with-ndbm=no \
    --with-berkeley-db=no \
    --enable-info \
    --enable-rewrite \
    --with-mpm=prefork \
    --enable-mpms-shared \
    ap_cv_void_ptr_lt_long=no \
    ac_cv_have_threadsafe_pollset=no \
    "

EXTRA_OECONF:class-native = "\
    --prefix=${prefix} \
    --includedir=${includedir}/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --datadir=${datadir}/${BPN} \
    --libdir=${libdir} \
    --libexecdir=${libdir}/${BPN}/modules \
    --localstatedir=${localstatedir} \
    "

do_configure:prepend() {
    sed -i -e 's:$''{prefix}/usr/lib/cgi-bin:$''{libexecdir}/cgi-bin:g' \
           -e 's#\(installbuilddir:\s*\).*#\1${libexecdir}/${PN}/build#' \
           ${S}/config.layout
}

do_install:append:class-target() {
    install -d ${D}/${sysconfdir}/init.d

    cat ${UNPACKDIR}/init | \
        sed -e 's,/usr/sbin/,${sbindir}/,g' \
            -e 's,/usr/bin/,${bindir}/,g' \
            -e 's,/usr/lib/,${libdir}/,g' \
            -e 's,/etc/,${sysconfdir}/,g' \
            -e 's,/usr/,${prefix}/,g' > ${D}/${sysconfdir}/init.d/${BPN}

    chmod 755 ${D}/${sysconfdir}/init.d/${BPN}

    # Remove the goofy original files...
    rm -rf ${D}/${sysconfdir}/${BPN}/original

    install -d ${D}${sysconfdir}/${BPN}/conf.d
    install -d ${D}${sysconfdir}/${BPN}/modules.d

    # Ensure configuration file pulls in conf.d and modules.d
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/conf.d/*.conf" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/modules.d/*.load" >> ${D}/${sysconfdir}/${BPN}/httpd.conf
    printf "\nIncludeOptional ${sysconfdir}/${BPN}/modules.d/*.conf\n\n" >> ${D}/${sysconfdir}/${BPN}/httpd.conf

    # Match with that is in init script
    printf "\nPidFile /run/httpd.pid" >> ${D}/${sysconfdir}/${BPN}/httpd.conf

    # Set 'ServerName' to fix error messages when restart apache service
    sed -i 's/^#ServerName www.example.com/ServerName localhost/' ${D}/${sysconfdir}/${BPN}/httpd.conf

    sed -i 's/^ServerRoot/#ServerRoot/' ${D}/${sysconfdir}/${BPN}/httpd.conf

    sed -i -e 's,${STAGING_DIR_TARGET},,g' \
           -e 's,${DEBUG_PREFIX_MAP},,g' \
           -e 's,-fdebug-prefix-map[^ ]*,,g; s,-fmacro-prefix-map[^ ]*,,g; s,-ffile-prefix-map[^ ]*,,g' \
           -e 's,${HOSTTOOLS_DIR}/,,g' \
           -e 's,APU_INCLUDEDIR = .*,APU_INCLUDEDIR = ,g' \
           -e 's,APU_CONFIG = .*,APU_CONFIG = ,g' ${D}${libexecdir}/${PN}/build/config_vars.mk

    sed -i -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
           -e 's,${DEBUG_PREFIX_MAP},,g' \
           -e 's,${RECIPE_SYSROOT},,g' \
           -e 's,-fdebug-prefix-map[^ ]*,,g; s,-fmacro-prefix-map[^ ]*,,g; s,-fmacro-prefix-map[^ ]*,,g' \
           -e 's,APU_INCLUDEDIR = .*,APU_INCLUDEDIR = ,g' \
           -e 's,${WORKDIR}/recipe-sysroot/,,g' \
           -e 's,".*/configure","configure",g' ${D}${libexecdir}/${PN}/build/config.nice

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d/
        install -m 0644 ${UNPACKDIR}/apache2-volatile.conf ${D}${sysconfdir}/tmpfiles.d/

        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/apache2.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/apache2.service
        sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' ${D}${systemd_unitdir}/system/apache2.service
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/default/volatiles
        install -m 0644 ${UNPACKDIR}/volatiles.04_apache2 ${D}${sysconfdir}/default/volatiles/04_apache2
    fi

    rm -rf ${D}${localstatedir} ${D}${sbindir}/envvars*
    chown -R root:root ${D}

    oe_multilib_header apache2/ap_config_layout.h
}

do_install:append:class-native() {
    install -d ${D}${bindir} ${D}${libdir}
    install -m 755 server/gen_test_char ${D}${bindir}
}

SYSROOT_PREPROCESS_FUNCS:append:class-target = " apache_sysroot_preprocess"

SYSROOT_DIRS += "${libexecdir}/${PN}/build"

apache_sysroot_preprocess() {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}
    install -m 755 ${D}${bindir}/apxs ${SYSROOT_DESTDIR}${bindir_crossscripts}
    install -d ${SYSROOT_DESTDIR}${sbindir}
    install -m 755 ${D}${sbindir}/apachectl ${SYSROOT_DESTDIR}${sbindir}
    sed -i 's!\(my $installbuilddir = \)"\(.*\)"!\1"${STAGING_DIR_HOST}\2"!' ${SYSROOT_DESTDIR}${bindir_crossscripts}/apxs

    sed -i 's!^APR_CONFIG = .*!APR_CONFIG = ${STAGING_BINDIR_CROSS}/apr-1-config!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!^APU_CONFIG = .*!APU_CONFIG = ${STAGING_BINDIR_CROSS}/apu-1-config!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!^includedir = .*!includedir = ${STAGING_INCDIR}/apache2!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!^CFLAGS = -I[^ ]*!CFLAGS = -I${STAGING_INCDIR}/openssl!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!^EXTRA_LDFLAGS = .*!EXTRA_LDFLAGS = -L${STAGING_LIBDIR}!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!^EXTRA_INCLUDES = .*!EXTRA_INCLUDES = -I$(includedir) -I. -I${STAGING_INCDIR}!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
    sed -i 's!--sysroot=[^ ]*!--sysroot=${STAGING_DIR_HOST}!' ${SYSROOT_DESTDIR}${libexecdir}/${PN}/build/config_vars.mk
}

# Implications - used by update-rc.d scripts
INITSCRIPT_NAME = "apache2"
INITSCRIPT_PARAMS = "defaults 91 20"

SYSTEMD_SERVICE:${PN} = "apache2.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

ALTERNATIVE:${PN}-doc = "htpasswd.1"
ALTERNATIVE_LINK_NAME[htpasswd.1] = "${mandir}/man1/htpasswd.1"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/apxs"

PACKAGES = "${PN}-utils ${PN}-scripts ${PN}-doc ${PN}-dev ${PN}-dbg ${PN}"

CONFFILES:${PN} = "${sysconfdir}/${BPN}/httpd.conf \
                   ${sysconfdir}/${BPN}/magic \
                   ${sysconfdir}/${BPN}/mime.types \
                   ${sysconfdir}/${BPN}/extra/*"

FILES:${PN}-utils = "${bindir}/ab \
                     ${bindir}/htdbm \
                     ${bindir}/htdigest \
                     ${bindir}/htpasswd \
                     ${bindir}/logresolve \
                     ${bindir}/httxt2dbm \
                     ${sbindir}/htcacheclean \
                     ${sbindir}/fcgistarter \
                     ${sbindir}/checkgid \
                     ${sbindir}/rotatelogs \
                    "

# We override here rather than append so that .so links are
# included in the runtime package rather than here (-dev)
# and to get build, icons, error into the -dev package
FILES:${PN}-dev = "${libexecdir}/${PN}/build \
                   ${datadir}/${BPN}/icons \
                   ${datadir}/${BPN}/error \
                   ${includedir}/${BPN} \
                   ${bindir}/apxs \
                  "

# Add the manual to -doc
FILES:${PN}-doc += " ${datadir}/${BPN}/manual"

FILES:${PN}-scripts += "${bindir}/dbmmanage"

# Override this too - here is the default, less datadir
FILES:${PN} =  "${bindir} ${sbindir} ${libexecdir} ${libdir} \
                ${sysconfdir} ${libdir}/${BPN}"

# We want htdocs and cgi-bin to go with the binary
FILES:${PN} += "${datadir}/${BPN}/ ${libdir}/cgi-bin"

FILES:${PN}-dbg += "${libdir}/${BPN}/modules/.debug"

RDEPENDS:${PN} += "openssl libgcc ${PN}-utils"
RDEPENDS:${PN}-scripts += "perl ${PN}"
RDEPENDS:${PN}-dev = "perl"

BBCLASSEXTEND = "native"

pkg_postinst:${PN}() {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}
