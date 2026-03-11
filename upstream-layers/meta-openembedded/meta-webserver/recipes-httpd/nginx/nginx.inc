SUMMARY = "HTTP and reverse proxy server"

DESCRIPTION = "Nginx is a web server and a reverse proxy server for \
HTTP, SMTP, POP3 and IMAP protocols, with a strong focus on high  \
concurrency, performance and low memory usage."

HOMEPAGE = "http://nginx.org/"
LICENSE = "BSD-2-Clause"

SECTION = "net"

DEPENDS = "libpcre zlib"

SRC_URI = " \
    http://nginx.org/download/nginx-${PV}.tar.gz \
    file://nginx-cross.patch \
    file://0001-Allow-the-overriding-of-the-endianness-via-the-confi.patch \
    file://nginx.conf \
    file://default_server.site \
    file://proxy_params \
    file://nginx.init \
    file://nginx-volatile.conf \
    file://nginx.service \
    file://nginx-fix-pidfile.patch \
    file://0001-configure-libxslt-conf.patch \
"

inherit siteinfo update-rc.d useradd systemd

SYSTEMD_SERVICE:${PN} = "nginx.service"

CFLAGS:append = " -fPIE -pie"
CXXFLAGS:append = " -fPIE -pie"

NGINX_WWWDIR ?= "${localstatedir}/www/localhost"
NGINX_USER   ?= "www"

EXTRA_OECONF = ""
DISABLE_STATIC = ""

PACKAGECONFIG ??= "ssl fastcgi scgi uwsgi ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[gunzip] = "--with-http_gunzip_module,,"
PACKAGECONFIG[http2] = "--with-http_v2_module,,"
PACKAGECONFIG[ssl] = "--with-http_ssl_module,,openssl"
PACKAGECONFIG[http-auth-request] = "--with-http_auth_request_module,,"
PACKAGECONFIG[ipv6] = "--with-ipv6,,"
PACKAGECONFIG[webdav] = "--with-http_dav_module,,"
PACKAGECONFIG[stream] = "--with-stream,,"
PACKAGECONFIG[http-sub-module] = "--with-http_sub_module,,"
PACKAGECONFIG[fastcgi] = ",--without-http_fastcgi_module,"
PACKAGECONFIG[scgi] = ",--without-http_scgi_module,"
PACKAGECONFIG[uwsgi] = ",--without-http_uwsgi_module,"
PACKAGECONFIG[xslt] = "--with-http_xslt_module,,libxslt"

do_configure () {
    if [ "${SITEINFO_BITS}" = "64" ]; then
        PTRSIZE=8
    else
        PTRSIZE=4
    fi

    echo $CFLAGS
    echo $LDFLAGS

    # Add the LDFLAGS to the main nginx link to avoid issues with missing GNU_HASH
    echo "MAIN_LINK=\"\${MAIN_LINK} ${LDFLAGS}\"" >> auto/cc/conf

    ./configure \
    --crossbuild=Linux:${TUNE_ARCH} \
    --with-endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
    --with-int=4 \
    --with-long=${PTRSIZE} \
    --with-long-long=8 \
    --with-ptr-size=${PTRSIZE} \
    --with-sig-atomic-t=${PTRSIZE} \
    --with-size-t=${PTRSIZE} \
    --with-off-t=8 \
    --with-time-t=${PTRSIZE} \
    --with-sys-nerr=132 \
    --conf-path=${sysconfdir}/nginx/nginx.conf \
    --http-log-path=${localstatedir}/log/nginx/access.log \
    --error-log-path=${localstatedir}/log/nginx/error.log \
    --http-client-body-temp-path=/run/nginx/client_body_temp \
    --http-proxy-temp-path=/run/nginx/proxy_temp \
    ${@bb.utils.contains('PACKAGECONFIG', 'fastcgi', '--http-fastcgi-temp-path=/run/nginx/fastcgi_temp', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'scgi', '--http-scgi-temp-path=/run/nginx/scgi_temp', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'uwsgi', '--http-uwsgi-temp-path=/run/nginx/uwsgi_temp', '', d)} \
    --pid-path=/run/nginx/nginx.pid \
    --prefix=${prefix} \
    --with-threads \
    --with-http_gzip_static_module \
    ${EXTRA_OECONF} ${PACKAGECONFIG_CONFARGS}
}

do_install () {
    oe_runmake 'DESTDIR=${D}' install
    rm -fr ${D}${localstatedir}/run ${D}/run
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/${BPN} - - - -" \
            > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
        echo "d ${localstatedir}/log/${BPN} 0755 root root -" \
            >> ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi
    install -d ${D}${sysconfdir}/${BPN}
    ln -rs ${D}${localstatedir}/run/${BPN} ${D}${sysconfdir}/${BPN}/run
    install -d ${D}${NGINX_WWWDIR}
    mv ${D}${exec_prefix}/html ${D}${NGINX_WWWDIR}/
    chown ${NGINX_USER}:www-data -R ${D}${NGINX_WWWDIR}

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${UNPACKDIR}/nginx.init ${D}${sysconfdir}/init.d/nginx
    sed -i 's,/usr/sbin/,${sbindir}/,g' ${D}${sysconfdir}/init.d/nginx
    sed -i 's,/etc/,${sysconfdir}/,g'  ${D}${sysconfdir}/init.d/nginx

    install -d ${D}${sysconfdir}/nginx
    install -m 0644 ${UNPACKDIR}/nginx.conf ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's,/etc/,${sysconfdir}/,g' ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's/^user.*/user ${NGINX_USER};/g' ${D}${sysconfdir}/nginx/nginx.conf
    install -Dm 0644 ${UNPACKDIR}/default_server.site ${D}${sysconfdir}/nginx/sites-available/default_server
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/nginx/sites-available/default_server
    install -d ${D}${sysconfdir}/nginx/sites-enabled
    ln -s ../sites-available/default_server ${D}${sysconfdir}/nginx/sites-enabled/

    install -m 0644 ${UNPACKDIR}/proxy_params ${D}${sysconfdir}/nginx/proxy_params

    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${UNPACKDIR}/nginx-volatile.conf ${D}${sysconfdir}/default/volatiles/99_nginx
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/default/volatiles/99_nginx
    sed -i 's,@NGINX_USER@,${NGINX_USER},g' ${D}${sysconfdir}/default/volatiles/99_nginx

    # cleanup configuration folder
    rm ${D}${sysconfdir}/nginx/*.default

    # remove config files based on packageconfig
    if ! [ "${@bb.utils.filter('PACKAGECONFIG', 'fastcgi', d)}" ]; then
        rm -f ${D}${sysconfdir}/nginx/fastcgi.conf
        rm -f ${D}${sysconfdir}/nginx/fastcgi_params
    fi
    if ! [ "${@bb.utils.filter('PACKAGECONFIG', 'scgi', d)}" ]; then
        rm -f ${D}${sysconfdir}/nginx/scgi_params
    fi
    if ! [ "${@bb.utils.filter('PACKAGECONFIG', 'uwsgi', d)}" ]; then
        rm -f ${D}${sysconfdir}/nginx/uwsgi_params
    fi

    # add additional configuration folders
    install -d ${D}${sysconfdir}/nginx/modules-available
    install -d ${D}${sysconfdir}/nginx/modules-enabled
    install -d ${D}${sysconfdir}/nginx/server-conf.d
    install -d ${D}${sysconfdir}/nginx/conf.d

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/nginx.service ${D}${systemd_unitdir}/system/
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@BASE_BINDIR@,${base_bindir},g' \
            ${D}${systemd_unitdir}/system/nginx.service
    fi

    rm -rf ${D}${localstatedir}/log/
}

pkg_postinst:${PN} () {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null 2>&1; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

FILES:${PN} += " \
    ${localstatedir}/ \
    ${systemd_unitdir}/system/nginx.service \
"

CONFFILES:${PN} = " \
    ${sysconfdir}/nginx/nginx.conf \
    ${sysconfdir}/nginx/fastcgi.conf \
    ${sysconfdir}/nginx/fastcgi_params \
    ${sysconfdir}/nginx/koi-utf \
    ${sysconfdir}/nginx/koi-win \
    ${sysconfdir}/nginx/mime.types \
    ${sysconfdir}/nginx/scgi_params \
    ${sysconfdir}/nginx/uwsgi_params \
    ${sysconfdir}/nginx/win-utf \
"

INITSCRIPT_NAME = "nginx"
INITSCRIPT_PARAMS = "defaults 92 20"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = " \
    --system --no-create-home \
    --home ${NGINX_WWWDIR} \
    --groups www-data \
    --shell ${base_sbindir}/nologin \
    --user-group ${NGINX_USER}"
