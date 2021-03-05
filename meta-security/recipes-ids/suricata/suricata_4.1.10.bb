SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRC_URI += " \
    file://volatiles.03_suricata \
    file://tmpfiles.suricata \
    file://suricata.yaml \
    file://suricata.service \
    file://run-ptest \
    "

inherit autotools-brokensep pkgconfig python3-dir systemd ptest

CFLAGS += "-D_DEFAULT_SOURCE -fcommon"

CACHED_CONFIGUREVARS = "ac_cv_header_htp_htp_h=yes ac_cv_lib_htp_htp_conn_create=yes \
                        ac_cv_path_HAVE_WGET=no ac_cv_path_HAVE_CURL=no "

EXTRA_OECONF += " --disable-debug \
    --enable-non-bundled-htp \
    --disable-gccmarch-native \
    --disable-suricata-update \
    "

PACKAGECONFIG ??= "htp jansson file pcre yaml pcap cap-ng net nfnetlink nss nspr"
PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'unittests', '', d)}"

PACKAGECONFIG[htp] = "--with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR}, ,libhtp,"
PACKAGECONFIG[pcre] = "--with-libpcre-includes=${STAGING_INCDIR} --with-libpcre-libraries=${STAGING_LIBDIR}, ,libpcre ," 
PACKAGECONFIG[yaml] = "--with-libyaml-includes=${STAGING_INCDIR} --with-libyaml-libraries=${STAGING_LIBDIR}, ,libyaml ,"
PACKAGECONFIG[pcap] = "--with-libpcap-includes=${STAGING_INCDIR} --with-libpcap-libraries=${STAGING_LIBDIR}, ,libpcap ," 
PACKAGECONFIG[cap-ng] = "--with-libcap_ng-includes=${STAGING_INCDIR} --with-libcap_ng-libraries=${STAGING_LIBDIR}, ,libcap-ng , "
PACKAGECONFIG[net] = "--with-libnet-includes=${STAGING_INCDIR} --with-libnet-libraries=${STAGING_LIBDIR}, , libnet," 
PACKAGECONFIG[nfnetlink] = "--with-libnfnetlink-includes=${STAGING_INCDIR} --with-libnfnetlink-libraries=${STAGING_LIBDIR}, ,libnfnetlink ,"
PACKAGECONFIG[nfq] = "--enable-nfqueue, --disable-nfqueue,libnetfilter-queue,"

PACKAGECONFIG[jansson] = "--with-libjansson-includes=${STAGING_INCDIR} --with-libjansson-libraries=${STAGING_LIBDIR},,jansson, jansson"
PACKAGECONFIG[file] = ",,file, file"
PACKAGECONFIG[nss] = "--with-libnss-includes=${STAGING_INCDIR} --with-libnss-libraries=${STAGING_LIBDIR}, nss, nss," 
PACKAGECONFIG[nspr] = "--with-libnspr-includes=${STAGING_INCDIR} --with-libnspr-libraries=${STAGING_LIBDIR}, nspr, nspr," 
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3, python3" 
PACKAGECONFIG[unittests] = "--enable-unittests, --disable-unittests," 

export logdir = "${localstatedir}/log"

do_install_append () {

    install -d ${D}${sysconfdir}/suricata

    oe_runmake install-conf DESTDIR=${D}

    oe_runmake install-rules DESTDIR=${D}

    install -d ${D}${sysconfdir}/suricata ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/03_suricata

    install -m 0644 ${S}/threshold.config ${D}${sysconfdir}/suricata

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${WORKDIR}/tmpfiles.suricata ${D}${sysconfdir}/tmpfiles.d/suricata.conf

        install -d ${D}${systemd_unitdir}/system
        sed  -e s:/etc:${sysconfdir}:g \
             -e s:/var/run:/run:g \
             -e s:/var:${localstatedir}:g \
             -e s:/usr/bin:${bindir}:g \
             -e s:/bin/kill:${base_bindir}/kill:g \
             -e s:/usr/lib:${libdir}:g \
             ${WORKDIR}/suricata.service > ${D}${systemd_unitdir}/system/suricata.service
    fi

    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

}

pkg_postinst_ontarget_${PN} () {
if command -v systemd-tmpfiles >/dev/null; then
    systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/suricata.conf
elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
}

SYSTEMD_PACKAGES = "${PN}"

PACKAGES =+ "${PN}-socketcontrol"
FILES_${PN} += "${systemd_unitdir} ${sysconfdir}/tmpfiles.d"
FILES_${PN}-socketcontrol = "${bindir}/suricatasc ${PYTHON_SITEPACKAGES_DIR}"

CONFFILES_${PN} = "${sysconfdir}/suricata/suricata.yaml"

RDEPENDS_${PN}-python = "python"
