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
    file://CVE-2021-23017.patch \
    file://CVE-2021-3618.patch \
"

inherit siteinfo update-rc.d useradd systemd

SYSTEMD_SERVICE_${PN} = "nginx.service"

CFLAGS_append = " -fPIE -pie"
CXXFLAGS_append = " -fPIE -pie"

NGINX_WWWDIR ?= "${localstatedir}/www/localhost"
NGINX_USER   ?= "www"

EXTRA_OECONF = ""
DISABLE_STATIC = ""

PACKAGECONFIG ??= "ssl"

PACKAGECONFIG[http2] = "--with-http_v2_module,,"
PACKAGECONFIG[ssl] = "--with-http_ssl_module,,openssl"
PACKAGECONFIG[http-auth-request] = "--with-http_auth_request_module,,"

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
    --with-off-t=${PTRSIZE} \
    --with-time-t=${PTRSIZE} \
    --with-sys-nerr=132 \
    --conf-path=${sysconfdir}/nginx/nginx.conf \
    --http-log-path=${localstatedir}/log/nginx/access.log \
    --error-log-path=${localstatedir}/log/nginx/error.log \
    --http-client-body-temp-path=/run/nginx/client_body_temp \
    --http-proxy-temp-path=/run/nginx/proxy_temp \
    --http-fastcgi-temp-path=/run/nginx/fastcgi_temp \
    --http-uwsgi-temp-path=/run/nginx/uwsgi_temp \
    --http-scgi-temp-path=/run/nginx/scgi_temp \
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
    lnr ${D}${localstatedir}/run/${BPN} ${D}${sysconfdir}/${BPN}/run
    install -d ${D}${NGINX_WWWDIR}
    mv ${D}${exec_prefix}/html ${D}${NGINX_WWWDIR}/
    chown ${NGINX_USER}:www-data -R ${D}${NGINX_WWWDIR}

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/nginx.init ${D}${sysconfdir}/init.d/nginx
    sed -i 's,/usr/sbin/,${sbindir}/,g' ${D}${sysconfdir}/init.d/nginx
    sed -i 's,/etc/,${sysconfdir}/,g'  ${D}${sysconfdir}/init.d/nginx

    install -d ${D}${sysconfdir}/nginx
    install -m 0644 ${WORKDIR}/nginx.conf ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's,/etc/,${sysconfdir}/,g' ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/nginx/nginx.conf
    sed -i 's/^user.*/user ${NGINX_USER};/g' ${D}${sysconfdir}/nginx/nginx.conf
    install -Dm 0644 ${WORKDIR}/default_server.site ${D}${sysconfdir}/nginx/sites-available/default_server
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/nginx/sites-available/default_server
    install -d ${D}${sysconfdir}/nginx/sites-enabled
    ln -s ../sites-available/default_server ${D}${sysconfdir}/nginx/sites-enabled/

    install -m 0644 ${WORKDIR}/proxy_params ${D}${sysconfdir}/nginx/proxy_params

    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/nginx-volatile.conf ${D}${sysconfdir}/default/volatiles/99_nginx
    sed -i 's,/var/,${localstatedir}/,g' ${D}${sysconfdir}/default/volatiles/99_nginx
    sed -i 's,@NGINX_USER@,${NGINX_USER},g' ${D}${sysconfdir}/default/volatiles/99_nginx

    # cleanup configuration folder
    rm ${D}${sysconfdir}/nginx/*.default

    # add additional configuration folders
    install -d ${D}${sysconfdir}/nginx/modules-available
    install -d ${D}${sysconfdir}/nginx/modules-enabled
    install -d ${D}${sysconfdir}/nginx/server-conf.d
    install -d ${D}${sysconfdir}/nginx/conf.d

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/nginx.service ${D}${systemd_unitdir}/system/
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@BASE_BINDIR@,${base_bindir},g' \
            ${D}${systemd_unitdir}/system/nginx.service
    fi

    rm -rf ${D}${localstatedir}/log/
}

pkg_postinst_${PN} () {
    if [ -z "$D" ]; then
        if type systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

FILES_${PN} += " \
    ${localstatedir}/ \
    ${systemd_unitdir}/system/nginx.service \
"

CONFFILES_${PN} = " \
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
USERADD_PARAM_${PN} = " \
    --system --no-create-home \
    --home ${NGINX_WWWDIR} \
    --groups www-data \
    --user-group ${NGINX_USER}"
