SUMMARY = "Rsyslog is an enhanced multi-threaded syslogd"
DESCRIPTION = "\
Rsyslog is an enhanced syslogd supporting, among others, MySQL,\
 PostgreSQL, failover log destinations, syslog/tcp, fine grain\
 output format control, high precision timestamps, queued operations\
 and the ability to filter on any message part. It is quite\
 compatible to stock sysklogd and can be used as a drop-in replacement.\
 Its advanced features make it suitable for enterprise-class,\
 encryption protected syslog relay chains while at the same time being\
 very easy to setup for the novice user."

DEPENDS = "zlib libestr libfastjson bison-native flex-native liblogging"
HOMEPAGE = "http://www.rsyslog.com/"
LICENSE = "GPLv3 & LGPLv3 & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=51d9635e646fb75e1b74c074f788e973 \
                    file://COPYING.LESSER;md5=cb7903f1e5c39ae838209e130dca270a \
                    file://COPYING.ASL20;md5=052f8a09206615ab07326ff8ce2d9d32\
"

SRC_URI = "http://www.rsyslog.com/download/files/download/rsyslog/${BPN}-${PV}.tar.gz \
           file://initscript \
           file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://use-pkgconfig-to-check-libgcrypt.patch \
           file://run-ptest \
           file://rsyslog-fix-ptest-not-finish.patch \
"

SRC_URI_append_libc-musl = " \
    file://0001-Undefine-GLOB_BRACE.patch \
    file://0001-Include-sys-time-h.patch \
"

SRC_URI[md5sum] = "3805617f65a4b4bea34606487a5255a0"
SRC_URI[sha256sum] = "220ba30b5afb0f3ddb328613fea7aa3966b01e4d0c52d6de9ab27b0858f19738"

inherit autotools pkgconfig systemd update-rc.d ptest

EXTRA_OECONF += "--disable-generate-man-pages ap_cv_atomic_builtins=yes"

# first line is default yes in configure
PACKAGECONFIG ??= " \
    rsyslogd rsyslogrt klog inet regexp uuid libgcrypt \
    imdiag gnutls imfile \
    ${@bb.utils.filter('DISTRO_FEATURES', 'snmp systemd', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'testbench relp ${VALGRIND}', '', d)} \
"

# default yes in configure
PACKAGECONFIG[relp] = "--enable-relp,--disable-relp,librelp,"
PACKAGECONFIG[rsyslogd] = "--enable-rsyslogd,--disable-rsyslogd,,"
PACKAGECONFIG[rsyslogrt] = "--enable-rsyslogrt,--disable-rsyslogrt,,"
PACKAGECONFIG[inet] = "--enable-inet,--disable-inet,,"
PACKAGECONFIG[klog] = "--enable-klog,--disable-klog,,"
PACKAGECONFIG[regexp] = "--enable-regexp,--disable-regexp,,"
PACKAGECONFIG[uuid] = "--enable-uuid,--disable-uuid,util-linux,"
PACKAGECONFIG[libgcrypt] = "--enable-libgcrypt,--disable-libgcrypt,libgcrypt,"
PACKAGECONFIG[testbench] = "--enable-testbench --enable-omstdout,--disable-testbench --disable-omstdout,,"

# default no in configure
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug,,"
PACKAGECONFIG[imdiag] = "--enable-imdiag,--disable-imdiag,,"
PACKAGECONFIG[imfile] = "--enable-imfile,--disable-imfile,,"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp,net-snmp,"
PACKAGECONFIG[gnutls] = "--enable-gnutls,--disable-gnutls,gnutls,"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[mysql] = "--enable-mysql,--disable-mysql,mysql5,"
PACKAGECONFIG[postgresql] = "--enable-pgsql,--disable-pgsql,postgresql,"
PACKAGECONFIG[libdbi] = "--enable-libdbi,--disable-libdbi,libdbi,"
PACKAGECONFIG[mail] = "--enable-mail,--disable-mail,,"
PACKAGECONFIG[valgrind] = "--enable-valgrind,--disable-valgrind,valgrind,"

TESTDIR = "tests"
do_compile_ptest() {
    echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${TESTDIR}/Makefile
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    # install the tests
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}

    # do NOT need to rebuild Makefile itself
    sed -i 's/^Makefile:.*$/Makefile:/' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # fix the srcdir, top_srcdir
    sed -i 's,^\(srcdir = \).*,\1${PTEST_PATH}/tests,' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
    sed -i 's,^\(top_srcdir = \).*,\1${PTEST_PATH}/tests,' ${D}${PTEST_PATH}/${TESTDIR}/Makefile

    # valgrind is not compatible with arm and mips,
    # so remove related test cases if there is no valgrind.
    if [ x${VALGRIND} = x ]; then
        sed -i '/udp-msgreduc-/d' ${D}${PTEST_PATH}/${TESTDIR}/Makefile
    fi

    # install test-driver
    install -m 644 ${S}/test-driver ${D}${PTEST_PATH}

    # install necessary links
    install -d ${D}${PTEST_PATH}/tools
    ln -sf ${sbindir}/rsyslogd ${D}${PTEST_PATH}/tools/rsyslogd

    install -d ${D}${PTEST_PATH}/runtime
    install -d ${D}${PTEST_PATH}/runtime/.libs
    (
        cd ${D}/${libdir}/rsyslog
        allso="*.so"
        for i in $allso; do
            ln -sf ${libdir}/rsyslog/$i ${D}${PTEST_PATH}/runtime/.libs/$i
        done
    )

    # fix the module load path with runtime/.libs
    find ${D}${PTEST_PATH}/${TESTDIR} -name "*.conf" -o -name "*.sh" -o -name "*.c" | xargs \
        sed -i -e 's:../plugins/.*/.libs/:../runtime/.libs/:g'
}

do_install_append() {
    install -d "${D}${sysconfdir}/init.d"
    install -d "${D}${sysconfdir}/logrotate.d"
    install -m 755 ${WORKDIR}/initscript ${D}${sysconfdir}/init.d/syslog
    install -m 644 ${WORKDIR}/rsyslog.conf ${D}${sysconfdir}/rsyslog.conf
    install -m 644 ${WORKDIR}/rsyslog.logrotate ${D}${sysconfdir}/logrotate.d/logrotate.rsyslog
}

FILES_${PN} += "${bindir}"

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "defaults"

CONFFILES_${PN} = "${sysconfdir}/rsyslog.conf"

RCONFLICTS_${PN} = "busybox-syslog sysklogd syslog-ng"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "${BPN}.service"

RDEPENDS_${PN} += "logrotate"

# for rsyslog-ptest
VALGRIND = "valgrind"
VALGRIND_mips = ""
VALGRIND_mips64 = ""
VALGRIND_mips64n32 = ""
VALGRIND_arm = ""
VALGRIND_aarch64 = ""
RDEPENDS_${PN}-ptest += "make diffutils gzip bash gawk coreutils procps"
RRECOMMENDS_${PN}-ptest += "${TCLIBC}-dbg ${VALGRIND}"
