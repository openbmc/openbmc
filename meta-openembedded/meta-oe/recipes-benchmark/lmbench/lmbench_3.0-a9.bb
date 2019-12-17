SUMMARY = "Tools for performance analysis"
HOMEPAGE = "http://lmbench.sourceforge.net/"
SECTION = "console/utils"
LICENSE = "GPLv2 & GPL-2.0-with-lmbench-restriction"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://COPYING-2;md5=8e9aee2ccc75d61d107e43794a25cdf9"

inherit autotools-brokensep

DEPENDS += "libtirpc"
CFLAGS += "-I${STAGING_INCDIR}/tirpc"
LDLIBS += " -ltirpc "

PR = "r2"

SRC_URI = "${SOURCEFORGE_MIRROR}/lmbench/lmbench-${PV}.tgz \
           file://lmbench-run \
           file://rename-line-binary.patch \
           file://update-results-script.patch \
           file://obey-ranlib.patch \
           file://update-config-script.patch \
           file://lmbench_result_html_report.patch \
           file://fix-lmbench-memory-check-failure.patch \
           file://0001-avoid-gcc-optimize-away-the-loops.patch \
           file://0001-lat_http.c-Add-printf-format.patch \
           file://0001-Check-for-musl-define-guard-before-redefining-sockle.patch \
           file://0002-build-Adjust-CFLAGS-LDFLAGS-to-append-values-passed-.patch \
           file://0001-src-Makefile-use-libdir-instead-of-hardcoded-lib.patch \
           "
SRC_URI[md5sum] = "b3351a3294db66a72e2864a199d37cbf"
SRC_URI[sha256sum] = "cbd5777d15f44eab7666dcac418054c3c09df99826961a397d9acf43d8a2a551"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/lmbench/files/development/"
UPSTREAM_CHECK_REGEX = "lmbench-(?P<pver>\d+(\.\d+)+-[a-z]+\d+)"

EXTRA_OEMAKE = 'CC="${CC}" AR="${AR}" RANLIB="${RANLIB}" CFLAGS="${CFLAGS}" \
                LDFLAGS="${LDFLAGS}" LDLIBS="${LDLIBS}" LD="${LD}" OS="${TARGET_SYS}" \
                TARGET="${TARGET_OS}" BASE="${prefix}" MANDIR="${mandir}"'

do_configure() {
    :
}

do_compile () {
    for CONFIG_SITE_ITEM in $CONFIG_SITE; do
        . $CONFIG_SITE_ITEM
    done
    if [ X"$ac_cv_uint" = X"yes" ]; then
        CFLAGS="${CFLAGS} -DHAVE_uint"
    fi
    install -d ${S}/bin/${TARGET_SYS}
    oe_runmake -C src
}

do_install () {
    install -d ${D}${sysconfdir}/default/volatiles \
           ${D}${bindir} ${D}${mandir} \
           ${D}${datadir}/lmbench/scripts

    echo "d root root 0755 ${localstatedir}/run/${BPN} none" \
           > ${D}${sysconfdir}/default/volatiles/99_lmbench
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/${BPN} - - - -" \
              > ${D}${sysconfdir}/tmpfiles.d/lmbench.conf
    fi

    oe_runmake BASE="${D}${prefix}" MANDIR="${D}${mandir}" \
            DESTDIR="${D}" \
            -C src install
    mv ${D}${bindir}/line ${D}${bindir}/lm_line
    install -m 0755 ${WORKDIR}/lmbench-run ${D}${bindir}/
    sed -i -e 's,^SHAREDIR=.*$,SHAREDIR=${datadir}/${BPN},;' \
           -e 's,^CONFIG=.*$,CONFIG=`$SCRIPTSDIR/config`,;' \
           ${D}${bindir}/lmbench-run
    install -m 0755 ${S}/scripts/lmbench ${D}${bindir}
    install -m 0755 ${S}/scripts/* ${D}${datadir}/lmbench/scripts
}

pkg_postinst_${PN} () {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/lmbench.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

RDEPENDS_${PN} = "perl"
FILES_${PN} += "${datadir}/lmbench"
