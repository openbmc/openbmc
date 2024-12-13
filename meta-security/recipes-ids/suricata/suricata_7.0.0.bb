SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRC_URI = "http://www.openinfosecfoundation.org/download/suricata-${PV}.tar.gz"
SRC_URI[sha256sum] = "7bcd1313118366451465dc3f8385a3f6aadd084ffe44dd257dda8105863bb769"

DEPENDS = "lz4 libhtp"

SRC_URI += " \
    file://volatiles.03_suricata \
    file://tmpfiles.suricata \
    file://suricata.yaml \
    file://suricata.service \
    file://run-ptest \
    file://fixup.patch \
    file://CVE-2024-37151.patch \
    file://CVE-2024-38534.patch \
    file://CVE-2024-38535_pre.patch \
    file://CVE-2024-38535.patch \
    file://CVE-2024-38536.patch \
    "

inherit autotools pkgconfig python3native systemd ptest cargo cargo-update-recipe-crates

require  ${BPN}-crates.inc

EXTRA_OECONF += " --disable-debug \
    --disable-gccmarch-native \
    --enable-non-bundled-htp \
    --disable-suricata-update \
    --with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR} \
    "

CARGO_SRC_DIR = "rust"

CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_BUILD_FLAGS:append = " --offline"

B = "${S}"

# nfnetlink has a dependancy to meta-networking
PACKAGECONFIG ??= "jansson file pcre2 yaml python pcap cap-ng net nss nspr "
PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'unittests', '', d)}"

PACKAGECONFIG[pcre2] = "--with-libpcre2-includes=${STAGING_INCDIR} --with-libpcre2-libraries=${STAGING_LIBDIR}, ,libpcre2 ," 
PACKAGECONFIG[yaml] = "--with-libyaml-includes=${STAGING_INCDIR} --with-libyaml-libraries=${STAGING_LIBDIR}, ,libyaml ,"
PACKAGECONFIG[pcap] = "--with-libpcap-includes=${STAGING_INCDIR} --with-libpcap-libraries=${STAGING_LIBDIR}, ,libpcap" 
PACKAGECONFIG[cap-ng] = "--with-libcap_ng-includes=${STAGING_INCDIR} --with-libcap_ng-libraries=${STAGING_LIBDIR}, ,libcap-ng , "
PACKAGECONFIG[net] = "--with-libnet-includes=${STAGING_INCDIR} --with-libnet-libraries=${STAGING_LIBDIR}, , libnet," 
PACKAGECONFIG[nfnetlink] = "--with-libnfnetlink-includes=${STAGING_INCDIR} --with-libnfnetlink-libraries=${STAGING_LIBDIR}, ,libnfnetlink ,"
PACKAGECONFIG[nfq] = "--enable-nfqueue, --disable-nfqueue,libnetfilter-queue,"

PACKAGECONFIG[jansson] = "--with-libjansson-includes=${STAGING_INCDIR} --with-libjansson-libraries=${STAGING_LIBDIR},,jansson, jansson"
PACKAGECONFIG[file] = ",,file, file"
PACKAGECONFIG[nss] = "--with-libnss-includes=${STAGING_INCDIR} --with-libnss-libraries=${STAGING_LIBDIR}, nss, nss," 
PACKAGECONFIG[nspr] = "--with-libnspr-includes=${STAGING_INCDIR} --with-libnspr-libraries=${STAGING_LIBDIR}, nspr, nspr," 
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3, python3-core" 
PACKAGECONFIG[unittests] = "--enable-unittests, --disable-unittests," 

export logdir = "${localstatedir}/log"

CACHED_CONFIGUREVARS = "ac_cv_func_malloc_0_nonnull=yes ac_cv_func_realloc_0_nonnull=yes"

do_configure:prepend () {
    # use host for RUST_SURICATA_LIB_XC_DIR
    sed -i -e 's,\${host_alias},${RUST_HOST_SYS},' ${S}/configure.ac
    sed -i -e 's,libsuricata_rust.a,libsuricata.a,' ${S}/configure.ac
    autotools_do_configure
}

CFLAGS += "-Wno-error=incompatible-pointer-types"

do_compile () {
    # we do this to bypass the make provided by this pkg 
    # patches Makefile to skip the subdir
    cargo_do_compile

    # Finish building
    cd ${S}
    make
}

do_install () {
    install -d ${D}${sysconfdir}/suricata

    oe_runmake install DESTDIR=${D}

    install -d ${D}${sysconfdir}/suricata ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${UNPACKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/03_suricata

    install -m 0644 ${S}/threshold.config ${D}${sysconfdir}/suricata
    install -m 0644 ${S}/suricata.yaml ${D}${sysconfdir}/suricata

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        install -m 0644 ${UNPACKDIR}/tmpfiles.suricata ${D}${sysconfdir}/tmpfiles.d/suricata.conf

        install -d ${D}${systemd_unitdir}/system
        sed  -e s:/etc:${sysconfdir}:g \
             -e s:/var/run:/run:g \
             -e s:/var:${localstatedir}:g \
             -e s:/usr/bin:${bindir}:g \
             -e s:/bin/kill:${base_bindir}/kill:g \
             -e s:/usr/lib:${libdir}:g \
             ${UNPACKDIR}/suricata.service > ${D}${systemd_unitdir}/system/suricata.service
    fi

    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${bindir}/suricatasc
    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${bindir}/suricatactl
    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${libdir}/suricata/python/suricata/sc/suricatasc.py
    # The build process dumps config logs into the binary, remove them.
    sed -i -e 's#${RECIPE_SYSROOT}##g' ${D}${bindir}/suricata
    sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' ${D}${bindir}/suricata
    sed -i -e 's#CFLAGS.*##g' ${D}${bindir}/suricata
}

pkg_postinst_ontarget:${PN} () {
if command -v systemd-tmpfiles >/dev/null; then
    systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/suricata.conf
elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
}

SYSTEMD_PACKAGES = "${PN}"

PACKAGES =+ "${PN}-python"
FILES:${PN} += "${systemd_unitdir} ${sysconfdir}/tmpfiles.d"
FILES:${PN}-python = "${bindir}/suricatasc ${PYTHON_SITEPACKAGES_DIR}"

CONFFILES:${PN} = "${sysconfdir}/suricata/suricata.yaml"
INSANE_SKIP:${PN} = "already-stripped"
