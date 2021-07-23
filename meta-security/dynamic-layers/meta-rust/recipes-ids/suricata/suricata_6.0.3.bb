SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRC_URI = "http://www.openinfosecfoundation.org/download/suricata-${PV}.tar.gz"
SRC_URI[sha256sum] = "daf134bb2d7c980035e9ae60f7aaf313323a809340009f26e48110ccde81f602"

DEPENDS = "lz4 libhtp"

SRC_URI += " \
    file://volatiles.03_suricata \
    file://tmpfiles.suricata \
    file://suricata.yaml \
    file://suricata.service \
    file://run-ptest \
    file://fixup.patch \
    "

SRC_URI += " \
    crate://crates.io/autocfg/1.0.1 \
    crate://crates.io/semver-parser/0.7.0 \
    crate://crates.io/arrayvec/0.4.12 \
    crate://crates.io/ryu/1.0.5 \
    crate://crates.io/libc/0.2.86 \
    crate://crates.io/bitflags/1.2.1 \
    crate://crates.io/version_check/0.9.2 \
    crate://crates.io/memchr/2.3.4 \
    crate://crates.io/nodrop/0.1.14 \
    crate://crates.io/cfg-if/0.1.9 \
    crate://crates.io/static_assertions/0.3.4 \
    crate://crates.io/getrandom/0.1.16 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/siphasher/0.3.3 \
    crate://crates.io/ppv-lite86/0.2.10 \
    crate://crates.io/proc-macro-hack/0.5.19 \
    crate://crates.io/proc-macro2/0.4.30 \
    crate://crates.io/unicode-xid/0.1.0 \
    crate://crates.io/syn/0.15.44 \
    crate://crates.io/build_const/0.2.1 \
    crate://crates.io/num-derive/0.2.5 \
    crate://crates.io/base64/0.11.0 \
    crate://crates.io/widestring/0.4.3 \
    crate://crates.io/md5/0.7.0 \
    crate://crates.io/uuid/0.8.2 \
    crate://crates.io/byteorder/1.4.2 \
    crate://crates.io/semver/0.9.0 \
    crate://crates.io/nom/5.1.1 \
    crate://crates.io/num-traits/0.2.14 \
    crate://crates.io/num-integer/0.1.44 \
    crate://crates.io/num-bigint/0.2.6 \
    crate://crates.io/num-bigint/0.3.1 \
    crate://crates.io/num-rational/0.2.4 \
    crate://crates.io/num-complex/0.2.4 \
    crate://crates.io/num-iter/0.1.42 \
    crate://crates.io/phf_shared/0.8.0 \
    crate://crates.io/crc/1.8.1 \
    crate://crates.io/rustc_version/0.2.3 \
    crate://crates.io/phf/0.8.0 \
    crate://crates.io/lexical-core/0.6.7 \
    crate://crates.io/time/0.1.44 \
    crate://crates.io/quote/0.6.13 \
    crate://crates.io/rand_core/0.5.1 \
    crate://crates.io/rand_chacha/0.2.2 \
    crate://crates.io/rand_pcg/0.2.1 \
    crate://crates.io/num-traits/0.1.43 \
    crate://crates.io/rand/0.7.3 \
    crate://crates.io/enum_primitive/0.1.1 \
    crate://crates.io/phf_generator/0.8.0 \
    crate://crates.io/phf_codegen/0.8.0 \
    crate://crates.io/tls-parser/0.9.4 \
    crate://crates.io/num/0.2.1 \
    crate://crates.io/rusticata-macros/2.1.0 \
    crate://crates.io/ntp-parser/0.4.0 \
    crate://crates.io/der-oid-macro/0.2.0 \
    crate://crates.io/der-parser/3.0.4 \
    crate://crates.io/ipsec-parser/0.5.0 \
    crate://crates.io/x509-parser/0.6.5 \
    crate://crates.io/der-parser/4.1.0 \
    crate://crates.io/snmp-parser/0.6.0 \
    crate://crates.io/kerberos-parser/0.5.0 \
    crate://crates.io/wasi/0.10.0+wasi-snapshot-preview1 \
    crate://crates.io/winapi/0.3.9 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/log/0.4.0 \
    crate://crates.io/rand_hc/0.2.0 \
    crate://crates.io/wasi/0.9.0+wasi-snapshot-preview1 \
    crate://crates.io/sawp/0.5.0 \
    crate://crates.io/sawp-modbus/0.5.0 \
    crate://crates.io/brotli/3.3.0 \
    crate://crates.io/flate2/1.0.20 \
    crate://crates.io/alloc-no-stdlib/2.0.1 \
    crate://crates.io/alloc-stdlib/0.2.1 \
    crate://crates.io/brotli-decompressor/2.3.1 \
    crate://crates.io/crc32fast/1.2.1 \
    crate://crates.io/miniz_oxide/0.4.4 \
    crate://crates.io/adler/1.0.2 \
    "

# test case support
SRC_URI += " \
    crate://crates.io/test-case/1.0.1 \
    crate://crates.io/proc-macro2/1.0.1 \
    crate://crates.io/quote/1.0.1 \
    crate://crates.io/syn/1.0.1 \
    crate://crates.io/unicode-xid/0.2.0 \
    "

inherit autotools pkgconfig python3native systemd ptest cargo

EXTRA_OECONF += " --disable-debug \
    --disable-gccmarch-native \
    --enable-non-bundled-htp \
    --disable-suricata-update \
    --with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR} \
    "

CARGO_SRC_DIR = "rust"

B = "${S}"

PACKAGECONFIG ??= "jansson file pcre yaml python pcap cap-ng net nfnetlink nss nspr "
PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'unittests', '', d)}"

PACKAGECONFIG[pcre] = "--with-libpcre-includes=${STAGING_INCDIR} --with-libpcre-libraries=${STAGING_LIBDIR}, ,libpcre ," 
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

do_configure_prepend () {
    oe_runconf
}

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
    install -m 0644 ${WORKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/03_suricata

    install -m 0644 ${S}/threshold.config ${D}${sysconfdir}/suricata
    install -m 0644 ${S}/suricata.yaml ${D}${sysconfdir}/suricata

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

    sed -i -e "s:#!.*$:#!${USRBINPATH}/env ${PYTHON_PN}:g" ${D}${bindir}/suricatasc
    sed -i -e "s:#!.*$:#!${USRBINPATH}/env ${PYTHON_PN}:g" ${D}${bindir}/suricatactl
}

pkg_postinst_ontarget_${PN} () {
if command -v systemd-tmpfiles >/dev/null; then
    systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/suricata.conf
elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
}

SYSTEMD_PACKAGES = "${PN}"

PACKAGES =+ "${PN}-python"
FILES_${PN} += "${systemd_unitdir} ${sysconfdir}/tmpfiles.d"
FILES_${PN}-python = "${bindir}/suricatasc ${PYTHON_SITEPACKAGES_DIR}"

CONFFILES_${PN} = "${sysconfdir}/suricata/suricata.yaml"
