SUMMARY = "ntop is network top"
DESCRIPTION = "ntop is a network traffic probe that shows the network usage, \
similar to what the popular top Unix command does."

SECTION = "console/network"

LICENSE = "GPLv2+ & GPLv3 & OpenSSL"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://LICENSE-OpenSSL.txt;md5=a409f902e447ddd889cffa0c70e7c7c2 \
                   "

SRC_URI = "${SOURCEFORGE_MIRROR}/ntop/ntop-${PV}.tar.gz \
           file://ntop_configure_in.patch \
           file://ntop_init.patch \
           file://ntop_webInterface.patch \
           file://ntop_configure_in_net_snmp_config_exist.patch \
           file://ntop.service \
           file://use-static-inline.patch \
           file://0001-nDPI-Include-sys-types.h.patch \
          "
SRC_URI[md5sum] = "01710b6925a8a5ffe1a41b8b512ebd69"
SRC_URI[sha256sum] = "7e8e84cb14d2173beaca4d4cb991a14d84a4bef84ec37b2276bc363f45c52ef8"

inherit autotools-brokensep useradd pythonnative pkgconfig systemd

DEPENDS = "geoip rrdtool python zlib libpcap gdbm"

PACKAGECONFIG ??= "openssl snmp plugins"
PACKAGECONFIG[openssl] = "--with-ssl, --without-ssl, openssl, openssl"
PACKAGECONFIG[snmp] = "--enable-snmp=yes NETSNMP=${STAGING_BINDIR_CROSS}/net-snmp-config, \
--disable-snmp,net-snmp,"
PACKAGECONFIG[plugins] = "--enable-plugins=yes, --disable-plugins, ,"

EXTRA_OECONF += "ac_cv_file_aclocal_m4=yes ac_cv_file_depcomp=no"

do_configure() {
    cp ${STAGING_DATADIR_NATIVE}/aclocal/libtool.m4 libtool.m4.in
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/nDPI
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/nDPI
    cat acinclude.m4.in acinclude.m4.ntop libtool.m4.in > acinclude.m4
    cp 3rd_party/* ./

    # config nDPI
    cd nDPI
    ./configure ${CONFIGUREOPTS} --with-pic
    cd ..

    sed -i -e 's:^CFG_DBFILE_DIR=$localstatedir/ntop:CFG_DBFILE_DIR=$localstatedir/lib/ntop:' ${S}/configure.in

    # fix the CFLAGS, CPPFLAGS, LDFLAGS, remove the host include
    sed -i \
        -e 's:\(^CFLAGS="\$.*\) -I/usr/local/include -I/opt/local/include":\1":' \
        -e 's:\(^CPPFLAGS="\$.*\) -I/usr/local/include -I/opt/local/include":\1":' \
        -e 's:\(^LDFLAGS="\$.*\) -L/usr/local/lib -L/opt/local/lib":\1":' \
        ${S}/configure.in

    # replace the DISTRO RELEASE in configure.in which are host's
    # with our release, although those doesn't affect functionality
    sed -i -e \
        '/DEFINEOS="LINUX"/{N;s/DISTRO=.*/DISTRO="${DISTRO}"/;N;s/RELEASE=.*/RELEASE="${DISTRO_VERSION}"/;}' \
        ${S}/configure.in

    # osName in original configure.in should be ${TARGET_SYS}
    # which will show in ntop's "show configuration"
    sed -i -e \
        's:^osName=.*:osName=${TARGET_SYS}:' \
        ${S}/configure.in

    # rename configureextra to configureextra_rename to avoid
    # configure.in to guess host OS and pull in more configure, non needed
    # which will cause some cross-compiling failure on specific host
    # e.g. SUSE(SLED...)
    test ! -f configureextra || mv -f configureextra configureextra_rename

    # make sure configure finds python includdirs/libs with these envs
    export BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR}

    autotools_do_configure
}

do_compile_prepend() {
    cd nDPI
    oe_runmake
    cd ..
}

do_install_append() {
    # remove the empty dirs
    rm -rf ${D}${libdir}/plugins

    install -D -m 0755 ${S}/packages/RedHat/ntop.init \
        ${D}${sysconfdir}/init.d/ntop
    install -D -m 0644 ${S}/packages/RedHat/ntop.conf.sample \
        ${D}${sysconfdir}/ntop.conf

    # change ntop dir in ntop.conf
    # don't use the -P as the ntop.init didn't support it
    sed -i -e "s:^--db-file-path /usr/share/ntop:--db-file-path /var/lib/ntop:" \
        -e "s:^#? -P /var/ntop:#? -P /var/lib/ntop:" \
        ${D}${sysconfdir}/ntop.conf

    # For systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0755 ${S}/packages/RedHat/ntop.init ${D}${libexecdir}/ntop-helper
        install -D -m 0644 ${WORKDIR}/ntop.service ${D}${systemd_system_unitdir}/ntop.service
        sed -i -e 's,@LIBEXECDIR@,${libexecdir},g' \
            -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${D}${systemd_system_unitdir}/ntop.service
    fi

    # Fix host-user-contaminated issue
    chown -R root:root ${D}

    chown -R ntop.ntop ${D}${datadir}/ntop
    chown -R ntop:ntop ${D}${localstatedir}/lib/ntop
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-M -g ntop -r -d ${localstatedir}/lib/ntop \
-s /usr/sbin/nologin -c 'ntop' ntop"
GROUPADD_PARAM_${PN} = "-r ntop"

SYSTEMD_SERVICE_${PN} = "ntop.service"
SYSTEMD_AUTO_ENABLE = "disable"

FILES_${PN}_append = "${libdir}/ntop/plugins ${libdir}/libntop-*.so \
                      ${libdir}/libntopreport-*.so ${libdir}/lib*-${PV}.so"
FILES_${PN}-dev = "${includedir} ${libdir}/libntop.so \
                   ${libdir}/libntopreport.so \
                   ${libdir}/libnetflowPlugin.so ${libdir}/libsflowPlugin.so \
                   ${libdir}/librrdPlugin.so \
                   ${libdir}/*.a ${libdir}/libntopreport.a ${libdir}/*.la"

