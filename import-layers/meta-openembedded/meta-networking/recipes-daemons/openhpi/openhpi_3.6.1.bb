SUMMARY = "Hardware Platform Interface Library and Tools"

DESCRIPTION = "\
OpenHPI is an open source project created with the intent of providing an \
implementation of the SA Forum's Hardware Platform Interface (HPI). HPI \
provides an abstracted interface to managing computer hardware, typically for \
chassis and rack based servers. HPI includes resource modeling; access to and \
control over sensor, control, watchdog, and inventory data associated with \
resources; abstracted System Event Log interfaces; hardware events and alerts; \
and a managed hotswap interface. \
\
OpenHPI provides a modular mechanism for adding new hardware and device support \
easily. Many plugins exist in the OpenHPI source tree to provide access to \
various types of hardware. This includes, but is not limited to, IPMI based \
servers, Blade Center, and machines which export data via sysfs. \
"

HOMEPAGE = "http://openhpi.sourceforge.net/Home"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e3c772a32386888ccb5ae1c0ba95f1a4"

DEPENDS = "net-snmp libxml2 ncurses openssl glib-2.0 popt e2fsprogs autoconf-archive-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz \
           file://openhpi.init \
           file://openhpid.service \
           file://run-ptest \
           file://openhpi-netsnmp-cross-compile.patch \
           file://openhpi-sysfs-cross-compile.patch \
           file://openhpi-libxml2-cross-compile.patch \
           file://openhpi-glib-cross-compile.patch \
           file://openhpi-linkfix.patch \
           file://openhpi-fix-host-gcc.patch \
           file://openhpi-hpi-shell-thread-fix.patch \
           file://openhpi-fix-testfail-errors.patch \
           file://openhpi-add-libnetsnmp-when-link.patch \
           file://openhpi-invalide-session.patch \
           file://openhpi-use-serial-tests-config-needed-by-ptest.patch \
           file://openhpi-fix-alignment-issue.patch \
           file://0001-Fix-build-failures-with-gcc7.patch \
           file://c++11.patch \
           file://clang-c++11.patch \
           file://fix-narrowing-warning.patch \
           file://0001-plugins-Check-for-PTHREAD_RECURSIVE_MUTEX_INITIALIZE.patch \
           file://0001-ipmidirect-Replace-__STRING.patch \
           "

SRC_URI[md5sum] = "4718b16e0f749b5ad214a9b04f45dd23"
SRC_URI[sha256sum] = "e0a810cb401c4bdcfc9551f2e6afd5a8ca4b411f5ee3bc60c19f82fd6e84a3dc"

inherit autotools pkgconfig ptest update-rc.d systemd

PACKAGES =+ "${PN}-libs"

FILES_${PN}-libs = "${libdir}/${BPN}/*.so /usr/lib/${BPN}/*.so"

INSANE_SKIP_${PN}-libs = "dev-so"
RDEPENDS_${PN} += "${PN}-libs"

PACKAGECONFIG ??= "libgcrypt non32bit"
PACKAGECONFIG[sysfs] = "--enable-sysfs,--disable-sysfs,sysfsutils,"
PACKAGECONFIG[libgcrypt] = "--enable-encryption,--disable-encryption,libgcrypt,"
PACKAGECONFIG[non32bit] = "--enable-non32bit-int,--disable-non32bit-int,,"

do_install_append () {
    install -m 0755 -d ${D}${sysconfdir}/${BPN}
    install -m 0755 ${S}/openhpiclient.conf.example ${D}${sysconfdir}/${BPN}/openhpiclient.conf
    install -m 0700 ${S}/openhpi.conf.example ${D}${sysconfdir}/${BPN}/openhpi.conf
    install -m 0755 ${S}/simulation.data.example ${D}${sysconfdir}/${BPN}/simulation.data
    install -m 0755 ${S}/test_agent.data.example ${D}${sysconfdir}/${BPN}/test_agent.data
    install -m 0755 ${WORKDIR}/openhpi.init ${D}${sysconfdir}/init.d/openhpid

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/openhpid.service ${D}${systemd_unitdir}/system
    sed -i -e "s,@SBINDIR@,${sbindir},g" -e "s,@SYSCONFDIR@,${sysconfdir},g" \
        ${D}${systemd_unitdir}/system/openhpid.service
}

do_compile_ptest () {
    for x in `find ${B} -name Makefile -exec grep -l buildtest-TESTS {} \;`; do
        dir=`dirname ${x}`
        upper=`dirname ${dir}`
        if [ `basename ${upper}` != "cpp" ]; then
            oe_runmake -C ${dir} buildtest-TESTS
        fi
    done
}

do_install_ptest () {
    cp -rf ${B}/openhpid/t/ohpi/.libs/* ${B}/openhpid/t/ohpi/
    TESTS="utils marshal openhpid"
    for subtest in ${TESTS}; do
        mkdir -p ${D}${PTEST_PATH}/${subtest}/t
        cp -rf ${B}/${subtest}/t/* ${D}${PTEST_PATH}/${subtest}/t
    done

    for x in `find ${D}${PTEST_PATH} -name Makefile`; do
        sed -i "s:${S}:${PTEST_PATH}/:g" ${x};
        sed -i "s/^Makefile:/MM:/g" ${x};
    done;

    mkdir -p ${D}${PTEST_PATH}/plugins/watchdog/
    cp -L ${D}/${libdir}/${BPN}/libwatchdog.so ${D}${PTEST_PATH}/plugins/watchdog/
    cp -L ${D}/${libdir}/${BPN}/libsimulator.so ${D}${PTEST_PATH}/plugins/watchdog/
    find ${D}${PTEST_PATH}/ -name "*.c" -exec rm {} \;
    find ${D}${PTEST_PATH}/ -name "*.o" -exec rm {} \;
    find ${D}${PTEST_PATH}/ -name "*.h" -exec rm {} \;
}

INITSCRIPT_NAME = "openhpid"
INITSCRIPT_PARAMS = "start 30 . stop 70 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE_${PN} = "openhpid.service"
SYSTEMD_AUTO_ENABLE = "disable"
