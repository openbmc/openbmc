SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"

require suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRC_URI = "http://www.openinfosecfoundation.org/download/suricata-${PV}.tar.gz"
SRC_URI[sha256sum] = "bbc94cf0a297f4560c64569ed72867c799287defdaf6e6572ce769f48dd2559b"

DEPENDS = "jansson lz4 libhtp"

SRC_URI += " \
    file://volatiles.03_suricata \
    file://tmpfiles.suricata \
    file://suricata.yaml \
    file://suricata.service \
    file://run-ptest \
    file://0001-Skip-pkg-Makefile-from-using-its-own-rust-steps.patch \
    "

inherit autotools pkgconfig python3native systemd ptest cargo cargo-update-recipe-crates

require  ${BPN}-crates.inc

EXTRA_OECONF += " --disable-debug \
    --disable-gccmarch-native \
    --enable-non-bundled-htp \
    --disable-suricata-update \
    --with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR} \
    --with-libjansson-includes=${STAGING_INCDIR} --with-libjansson-libraries=${STAGING_LIBDIR} \
    "

CARGO_SRC_DIR = "rust"

CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_BUILD_FLAGS:append = " --offline"

B = "${S}"

# nfnetlink has a dependancy to meta-networking
PACKAGECONFIG ??= "file \
                   pcre2 \
                   yaml \
                   python \
                   pcap \
                   cap-ng \
                   net \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'seccomp', d)} \
                   "
PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'unittests', '', d)}"

PACKAGECONFIG[pcre2] = "--with-libpcre2-includes=${STAGING_INCDIR} --with-libpcre2-libraries=${STAGING_LIBDIR}, ,libpcre2 ,"
PACKAGECONFIG[yaml] = "--with-libyaml-includes=${STAGING_INCDIR} --with-libyaml-libraries=${STAGING_LIBDIR}, ,libyaml ,"
PACKAGECONFIG[pcap] = "--with-libpcap-includes=${STAGING_INCDIR} --with-libpcap-libraries=${STAGING_LIBDIR}, ,libpcap"
PACKAGECONFIG[cap-ng] = "--with-libcap_ng-includes=${STAGING_INCDIR} --with-libcap_ng-libraries=${STAGING_LIBDIR}, ,libcap-ng , "
PACKAGECONFIG[net] = "--with-libnet-includes=${STAGING_INCDIR} --with-libnet-libraries=${STAGING_LIBDIR}, , libnet,"
PACKAGECONFIG[nfnetlink] = "--with-libnfnetlink-includes=${STAGING_INCDIR} --with-libnfnetlink-libraries=${STAGING_LIBDIR}, ,libnfnetlink ,"
PACKAGECONFIG[nfq] = "--enable-nfqueue, --disable-nfqueue,libnetfilter-queue,"

PACKAGECONFIG[file] = ",,file, file"
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3, python3-core"
PACKAGECONFIG[seccomp] = ""
PACKAGECONFIG[unittests] = "--enable-unittests, --disable-unittests,"

export logdir = "${localstatedir}/log"

CACHED_CONFIGUREVARS = "ac_cv_func_malloc_0_nonnull=yes ac_cv_func_realloc_0_nonnull=yes"

do_configure:prepend () {
    # use host for RUST_SURICATA_LIB_XC_DIR
    sed -i -e 's,\${host_alias},${RUST_HOST_SYS},' ${S}/configure.ac
    sed -i -e 's,libsuricata_rust.a,libsuricata.a,' ${S}/configure.ac
    # Address build configuration written to src/build-info.h
    sed -i -e 's,\(| sed -e '\''s/^/"/'\''\)\( |\),\1 -e '\''s#${WORKDIR}#\\.#g'\''\2,' ${S}/configure.ac
    autotools_do_configure
}

CFLAGS += "-Wno-error=incompatible-pointer-types"

# Commit 7a2b9acef2 cargo: pass PACKAGECONFIG_CONFARGS to cargo build
# breaks building this recipe. Providing a copy of the original function
# Armin 2025/04/01
#
oe_cargo_build () {
    export RUSTFLAGS="${RUSTFLAGS}"
    bbnote "Using rust targets from ${RUST_TARGET_PATH}"
    bbnote "cargo = $(which ${CARGO})"
    bbnote "${CARGO} build ${CARGO_BUILD_FLAGS}$@"
    "${CARGO}" build ${CARGO_BUILD_FLAGS}"$@"
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
    install -m 0644 ${UNPACKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/03_suricata

    install -m 0644 ${S}/etc/classification.config ${D}${sysconfdir}/suricata
    install -m 0644 ${S}/etc/reference.config ${D}${sysconfdir}/suricata
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

        if ${@bb.utils.contains('PACKAGECONFIG', 'seccomp', 'true', 'false', d)}; then
            sed -i -e 's/^MemoryDenyWriteExecute=no$/MemoryDenyWriteExecute=yes/' ${D}${systemd_unitdir}/system/suricata.service
        fi
    fi

    # Remove /var/run as it is created on startup
    rm -rf ${D}${localstatedir}/run

    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${bindir}/suricatasc
    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${bindir}/suricatactl
    sed -i -e "s:#!.*$:#!${USRBINPATH}/env python3:g" ${D}${libdir}/suricata/python/suricata/sc/suricatasc.py
}

pkg_postinst_ontarget:${PN} () {
if [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
    ${sysconfdir}/init.d/populate-volatile.sh update
fi
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${BPN}.service"

PACKAGES =+ "${PN}-python"
FILES:${PN} += "${systemd_unitdir} ${sysconfdir}/tmpfiles.d"
FILES:${PN}-python = "${bindir}/suricatasc ${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS:${PN} += "jansson"

CONFFILES:${PN} = "${sysconfdir}/suricata/suricata.yaml"
