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
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e3c772a32386888ccb5ae1c0ba95f1a4"

DEPENDS = "net-snmp libxml2 ncurses openssl glib-2.0 popt e2fsprogs \
           autoconf-archive-native os-release"

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
           file://openhpi-fix-function-saHpiSensorThresholds.patch \
           file://openhpi-add-libnetsnmp-when-link.patch \
           file://openhpi-invalide-session.patch \
           file://openhpi-use-serial-tests-config-needed-by-ptest.patch \
           file://openhpi-fix-alignment-issue.patch \
           file://c++11.patch \
           file://clang-c++11.patch \
           file://fix-narrowing-warning.patch \
           file://0001-session-close-socket.patch \
           file://openhpi-3.6.1-ssl.patch \
           file://0001-Do-not-poke-at-build-host-s-etc-os-release.patch \
           file://cross_899198.patch \
           file://no-md2.patch \
           file://0001-include-iostream-for-cout.patch \
           "
SRC_URI[md5sum] = "fffda3deea8a0d3671a72eea9d13a4df"
SRC_URI[sha256sum] = "c94332a29160dd75cb799c027e614690c00263b0fabed87417707bec04c38723"

inherit autotools pkgconfig ptest update-rc.d systemd

PACKAGES =+ "${PN}-libs"

FILES:${PN}-libs = "${libdir}/${BPN}/*.so /usr/lib/${BPN}/*.so"

INSANE_SKIP:${PN}-libs = "dev-so"
RDEPENDS:${PN} += "${PN}-libs"
RDEPENDS:${PN}-ptest += "packagegroup-core-buildessential"

PACKAGECONFIG ??= "libgcrypt non32bit snmp-bc"
PACKAGECONFIG[sysfs] = "--enable-sysfs,--disable-sysfs,sysfsutils,"
PACKAGECONFIG[libgcrypt] = "--enable-encryption,--disable-encryption,libgcrypt,"
PACKAGECONFIG[non32bit] = "--enable-non32bit-int,--disable-non32bit-int,,"
PACKAGECONFIG[snmp-bc] = "--enable-snmp_bc,--disable-snmp_bc"

export DISTRO

do_install:append () {
    install -m 0755 -d ${D}${sysconfdir}/${BPN}
    install -m 0644 ${S}/openhpiclient.conf.example ${D}${sysconfdir}/${BPN}/openhpiclient.conf
    install -m 0600 ${S}/openhpi.conf.example ${D}${sysconfdir}/${BPN}/openhpi.conf
    install -m 0644 ${S}/simulation.data.example ${D}${sysconfdir}/${BPN}/simulation.data
    install -m 0644 ${S}/test_agent.data.example ${D}${sysconfdir}/${BPN}/test_agent.data
    install -m 0755 ${UNPACKDIR}/openhpi.init ${D}${sysconfdir}/init.d/openhpid

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/openhpid.service ${D}${systemd_unitdir}/system
    sed -i -e "s,@SBINDIR@,${sbindir},g" -e "s,@SYSCONFDIR@,${sysconfdir},g" \
        ${D}${systemd_unitdir}/system/openhpid.service
}

do_compile_ptest () {
    for x in `find ${B} -name Makefile -exec grep -l buildtest-TESTS {} \;`; do
        dir=`dirname ${x}`
        case $dir in
            *cpp/t)      ;;
            *snmp_bc/t)  if ${@bb.utils.contains('PACKAGECONFIG','snmp-bc','true','false',d)}
                         then
                           oe_runmake -C ${dir} buildtest-TESTS
                         fi
                         ;;
            *)           oe_runmake -C ${dir} buildtest-TESTS ;;
        esac
    done
}

ack_do_compile_ptest () {
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

    install -m 644 ${S}/openhpid/t/ohpi/openhpi.conf ${D}${PTEST_PATH}/openhpid/t/ohpi/
    sed -i "s:OPENHPI_CONF=[^ ]*:OPENHPI_CONF=./openhpi.conf:g" ${D}${PTEST_PATH}/openhpid/t/ohpi/Makefile

    mkdir -p ${D}${PTEST_PATH}/plugins/watchdog/
    cp -L ${D}/${libdir}/${BPN}/libwatchdog.so ${D}${PTEST_PATH}/plugins/watchdog/
    cp -L ${D}/${libdir}/${BPN}/libsimulator.so ${D}${PTEST_PATH}/plugins/watchdog/
    find ${D}${PTEST_PATH}/ -name "*.c" -exec rm {} \;
    find ${D}${PTEST_PATH}/ -name "*.o" -exec rm {} \;
    find ${D}${PTEST_PATH}/ -name "*.h" -exec rm {} \;
}

INITSCRIPT_NAME = "openhpid"
INITSCRIPT_PARAMS = "start 30 . stop 70 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE:${PN} = "openhpid.service"
SYSTEMD_AUTO_ENABLE = "disable"
