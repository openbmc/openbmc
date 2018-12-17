FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

inherit systemd
inherit obmc-phosphor-systemd

SRC_URI += " \
    file://nginx.conf \
    file://nginx.service \
    file://gen-cert.sh \
    "

RDEPENDS_${PN} += " \
        openssl-bin \
        ${VIRTUAL-RUNTIME_base-utils} \
        "

EXTRA_OECONF =+ " --without-select_module --with-http_gunzip_module"

SSLCERTPATH = "/etc/ssl/certs/nginx/"


do_install_append() {

    install -m 644 ${WORKDIR}/nginx.conf ${D}${sysconfdir}/nginx
    install -m 0755 ${WORKDIR}/gen-cert.sh ${D}${sbindir}/gen-cert.sh

    install -d ${D}${SSLCERTPATH}
    chown -R www:www-data      ${D}${SSLCERTPATH}


    echo SSLCERTPATH
    echo ${SSLCERTPATH}
    sed -i 's,@CERTPATH@,${SSLCERTPATH},g' ${D}${sysconfdir}/nginx/nginx.conf
}

FILES_${PN} += " ${SSLCERTPATH} "

SYSTEMD_SERVICE_${PN} += " nginx.service"
