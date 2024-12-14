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
LICENSE = "GPL-3.0-only & LGPL-3.0-only & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=51d9635e646fb75e1b74c074f788e973 \
                    file://COPYING.LESSER;md5=cb7903f1e5c39ae838209e130dca270a \
                    file://COPYING.ASL20;md5=052f8a09206615ab07326ff8ce2d9d32\
"

SRC_URI = "https://www.rsyslog.com/files/download/rsyslog/${BPN}-${PV}.tar.gz \
           file://initscript \
           file://rsyslog.conf \
           file://rsyslog.logrotate \
           file://rsyslog.service \
           file://use-pkgconfig-to-check-libgcrypt.patch \
           file://run-ptest \
           file://0001-tests-disable-the-check-for-inotify.patch \
           file://0001-tests-tcpflood.c-Pass-correct-parameter-type-to-send.patch \
"

SRC_URI:append:libc-musl = " \
    file://0001-Include-sys-time-h.patch \
    file://disable-omfile-outchannel.patch \
"
SRC_URI[sha256sum] = "8bb2f15f9bf9bb7e635182e3d3e370bfc39d08bf35a367dce9714e186f787206"

UPSTREAM_CHECK_URI = "https://github.com/rsyslog/rsyslog/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

CVE_PRODUCT = "rsyslog:rsyslog"

inherit autotools pkgconfig systemd update-rc.d ptest

EXTRA_OECONF += "--enable-imfile-tests ${ATOMICS}"
ATOMICS = "ap_cv_atomic_builtins_64=yes ap_cv_atomic_builtins=yes"
ATOMICS:mipsarch = ""
ATOMICS:powerpc = ""
ATOMICS:riscv32 = ""
ATOMICS:armv5 = ""

# first line is default yes in configure
PACKAGECONFIG ??= " \
    rsyslogd rsyslogrt klog inet regexp uuid libcap-ng libgcrypt \
    fmhttp imdiag openssl imfile \
    ${@bb.utils.filter('DISTRO_FEATURES', 'snmp systemd', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'testbench relp ${VALGRIND}', '', d)} \
"

# default yes in configure
PACKAGECONFIG[relp] = "--enable-relp,--disable-relp,librelp,"
PACKAGECONFIG[rsyslogd] = "--enable-rsyslogd,--disable-rsyslogd,,"
PACKAGECONFIG[rsyslogrt] = "--enable-rsyslogrt,--disable-rsyslogrt,,"
PACKAGECONFIG[fmhttp] = "--enable-fmhttp,--disable-fmhttp,curl,"
PACKAGECONFIG[inet] = "--enable-inet,--disable-inet,,"
PACKAGECONFIG[klog] = "--enable-klog,--disable-klog,,"
PACKAGECONFIG[libcap-ng] = "--enable-libcap-ng,--disable-libcap-ng,libcap-ng,"
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
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl,"
PACKAGECONFIG[systemd] = "--enable-libsystemd,--disable-libsystemd,systemd,"
PACKAGECONFIG[imjournal] = "--enable-imjournal,--disable-imjournal,"
PACKAGECONFIG[mmjsonparse] = "--enable-mmjsonparse,--disable-mmjsonparse,"
PACKAGECONFIG[mysql] = "--enable-mysql,--disable-mysql,mysql5,"
PACKAGECONFIG[postgresql] = "--enable-pgsql,--disable-pgsql,postgresql,"
PACKAGECONFIG[libdbi] = "--enable-libdbi,--disable-libdbi,libdbi,"
PACKAGECONFIG[mail] = "--enable-mail,--disable-mail,,"
PACKAGECONFIG[valgrind] = ",--without-valgrind-testbench,valgrind,"
PACKAGECONFIG[imhttp] = "--enable-imhttp,--disable-imhttp,civetweb,"


TESTDIR = "tests"
do_compile_ptest() {
    echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${TESTDIR}/Makefile
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    # install the tests
    cp -rf ${S}/${TESTDIR} ${D}${PTEST_PATH}
    cp -rf ${B}/${TESTDIR} ${D}${PTEST_PATH}

    # give permissions to all users
    # some tests need to write to this directory as user 'daemon'
    chmod 777 -R ${D}${PTEST_PATH}/tests

	sed -e '# do NOT need to rebuild Makefile itself' \
	    -e 's/^Makefile:.*$/Makefile:/' \
	    -e '# do NOT need to rebuild $(check_PROGRAMS)' \
	    -e 's/^check-TESTS:.*$/check-TESTS:/' \
	    -e '# fix the srcdir, top_srcdir' \
	    -e 's,^\(srcdir = \).*,\1${PTEST_PATH}/tests,' \
	    -e 's,^\(top_srcdir = \).*,\1${PTEST_PATH}/tests,' \
	    -e '# fix the abs_top_builddir' \
	    -e 's,^\(abs_top_builddir = \).*,\1${PTEST_PATH}/,' \
	    -e '# fix the path to test-driver' \
	    -e '/^\(SH_\)\?LOG_DRIVER/s/(top_srcdir)/(top_builddir)/' \
	    -i ${D}${PTEST_PATH}/${TESTDIR}/Makefile

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
    # fix the python3 path for tests/set-envar
    sed -i -e s:${HOSTTOOLS_DIR}:${bindir}:g ${D}${PTEST_PATH}/tests/set-envvars
}

do_install:append() {
    install -d "${D}${sysconfdir}/init.d"
    install -d "${D}${sysconfdir}/logrotate.d"
    install -m 755 ${UNPACKDIR}/initscript ${D}${sysconfdir}/init.d/syslog
    install -m 644 ${UNPACKDIR}/rsyslog.conf ${D}${sysconfdir}/rsyslog.conf
    install -m 644 ${UNPACKDIR}/rsyslog.logrotate ${D}${sysconfdir}/logrotate.d/logrotate.rsyslog
    sed -i -e "s#@BINDIR@#${bindir}#g" ${D}${sysconfdir}/logrotate.d/logrotate.rsyslog

    if ${@bb.utils.contains('PACKAGECONFIG', 'imjournal', 'true', 'false', d)}; then
        install -d 0755 ${D}${sysconfdir}/rsyslog.d
        echo '$ModLoad imjournal' >> ${D}${sysconfdir}/rsyslog.d/imjournal.conf
    fi
    if ${@bb.utils.contains('PACKAGECONFIG', 'mmjsonparse', 'true', 'false', d)}; then
        install -d 0755 ${D}${sysconfdir}/rsyslog.d
        echo '$ModLoad mmjsonparse' >> ${D}${sysconfdir}/rsyslog.d/mmjsonparse.conf
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 644 ${UNPACKDIR}/rsyslog.service ${D}${systemd_system_unitdir}
        sed -i -e "s,@sbindir@,${sbindir},g" ${D}${systemd_system_unitdir}/rsyslog.service
    fi
}

FILES:${PN} += "${bindir}"

INITSCRIPT_NAME = "syslog"
INITSCRIPT_PARAMS = "defaults"

CONFFILES:${PN} = "${sysconfdir}/rsyslog.conf"

RCONFLICTS:${PN} = "busybox-syslog sysklogd syslog-ng"

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "${BPN}.service"

RDEPENDS:${PN} += "logrotate"

# for rsyslog-ptest
VALGRIND = "valgrind"

# valgrind supports armv7 and above
VALGRIND:armv4 = ''
VALGRIND:armv5 = ''
VALGRIND:armv6 = ''

# X32 isn't supported by valgrind at this time
VALGRIND:linux-gnux32 = ''
VALGRIND:linux-muslx32 = ''

# Disable for some MIPS variants
VALGRIND:mipsarchr6 = ''
VALGRIND:linux-gnun32 = ''

# Disable for powerpc64 with musl
VALGRIND:libc-musl:powerpc64 = ''
VALGRIND:libc-musl:powerpc64le = ''

# RISC-V support for valgrind is not there yet
VALGRIND:riscv64 = ""
VALGRIND:riscv32 = ""

# util-linux: logger needs the -d option
RDEPENDS:${PN}-ptest += "\
  make diffutils gzip bash gawk coreutils procps \
  libgcc python3-core python3-io python3-json \
  curl util-linux shadow \
  "

RRECOMMENDS:${PN}-ptest += "${TCLIBC}-dbg ${VALGRIND}"
