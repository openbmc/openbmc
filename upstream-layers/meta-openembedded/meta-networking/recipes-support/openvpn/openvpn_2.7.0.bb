SUMMARY = "A full-featured SSL VPN solution via tun device."
HOMEPAGE = "https://openvpn.net/"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=924af2c382c415a0a68d0d9e7b483d23"
DEPENDS = "lzo lz4 openssl libcap-ng ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} ${@bb.utils.contains('PTEST_ENABLED', '1', 'cmocka', '', d)} "

inherit autotools systemd update-rc.d pkgconfig ptest

SRC_URI = "http://swupdate.openvpn.org/community/releases/${BP}.tar.gz \
           file://0001-configure.ac-eliminate-build-path-from-openvpn-versi.patch \
           file://0001-tests-skip-test-execution-when-cross-compiling.patch \
           file://openvpn \
           file://run-ptest \
          "

UPSTREAM_CHECK_URI = "https://openvpn.net/community-downloads"

SRC_URI[sha256sum] = "2f0e10eb272be61e8fb25fe1cfa20875ff30ac857ef1418000c02290bd6dfa45"

CVE_STATUS[CVE-2020-27569] = "not-applicable-config: Applies only Aviatrix OpenVPN client, not openvpn"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "openvpn"
INITSCRIPT_PARAMS:${PN} = "start 10 2 3 4 5 . stop 70 0 1 6 ."

CFLAGS += "-fno-inline"

# I want openvpn to be able to read password from file (hrw)
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '', '--disable-plugin-auth-pam', d)}"

# Explicitly specify IPROUTE to bypass the configure-time check for /sbin/ip on the host.
EXTRA_OECONF += "IPROUTE=${base_sbindir}/ip"

EXTRA_OECONF += "SYSTEMD_UNIT_DIR=${systemd_system_unitdir} \
                 TMPFILES_DIR=${nonarch_libdir}/tmpfiles.d \
                "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
                   iproute2 \
                  "

# dco and iproute2 are mutually incompatible
PACKAGECONFIG[dco] = ",--disable-dco,libnl"
PACKAGECONFIG[iproute2] = "--enable-iproute2,,iproute2,iproute2-ip"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

RDEPENDS:${PN}:append = " bash"
RDEPENDS:${PN}-ptest:append = " bash"

do_install:append() {
    install -d ${D}/${sysconfdir}/init.d
    install -m 755 ${UNPACKDIR}/openvpn ${D}/${sysconfdir}/init.d

    install -d ${D}/${sysconfdir}/openvpn
    install -d ${D}/${sysconfdir}/openvpn/server
    install -d ${D}/${sysconfdir}/openvpn/client

    install -d ${D}/${sysconfdir}/openvpn/sample
    install -m 644 ${S}/sample/sample-config-files/loopback-server  ${D}${sysconfdir}/openvpn/sample/loopback-server.conf
    install -m 644 ${S}/sample/sample-config-files/loopback-client  ${D}${sysconfdir}/openvpn/sample/loopback-client.conf
    install -dm 755 ${D}${sysconfdir}/openvpn/sample/sample-config-files
    install -dm 755 ${D}${sysconfdir}/openvpn/sample/sample-keys
    install -dm 755 ${D}${sysconfdir}/openvpn/sample/sample-scripts
    install -m 644 ${S}/sample/sample-config-files/* ${D}${sysconfdir}/openvpn/sample/sample-config-files
    install -m 644 ${S}/sample/sample-keys/* ${D}${sysconfdir}/openvpn/sample/sample-keys
    install -m 644 ${S}/sample/sample-scripts/* ${D}${sysconfdir}/openvpn/sample/sample-scripts

    install -d -m 710 ${D}/${localstatedir}/lib/openvpn
}

do_compile_ptest () {
    for x in `find ${B}/tests/unit_tests -name Makefile -exec grep -l check_PROGRAMS {} \;`; do
        dir=`dirname ${x}`
        case $dir in
            *example*)   
                echo "Skipping directory: $dir"
                ;;
            *)           
                oe_runmake -C ${dir} check-am
                ;;
        esac
    done
}

do_install_ptest() {
    for x in $(find ${B}/tests/unit_tests -name Makefile -exec grep -l check_PROGRAMS {} \;); do
        dir=$(dirname ${x})

        if [[ "$dir" == *example* ]]; then
            continue
        fi

        target_dir="${D}/${PTEST_PATH}/unit_tests/$(basename ${dir})"
        mkdir -p ${target_dir}

        for testfile in $(find ${dir} -name "*testdriver" -type f -executable); do
            cp -rf ${testfile} ${target_dir}/
        done
    done

    # Install test input data files needed by user_pass and misc tests
    cp -rf ${S}/tests/unit_tests/openvpn/input ${D}/${PTEST_PATH}/unit_tests/openvpn/

    # Install COPYRIGHT.GPL needed by test_list
    # test_list references srcdir/../../../COPYRIGHT.GPL
    # srcdir=./unit_tests/openvpn -> resolves to ../COPYRIGHT.GPL from ptest cwd
    # which is ${libdir}/openvpn/COPYRIGHT.GPL
    cp -f ${S}/COPYRIGHT.GPL ${D}/${libdir}/openvpn/

    sed -i 's|${top_builddir}/src/openvpn|${sbindir}|g' ${S}/tests/t_lpback.sh
    cp -f ${S}/tests/t_lpback.sh ${D}/${PTEST_PATH}
}

PACKAGES =+ " ${PN}-sample "

RRECOMMENDS:${PN} = "kernel-module-tun"

FILES:${PN}-dbg += "${libdir}/openvpn/plugins/.debug"
FILES:${PN}-ptest += "${libdir}/openvpn/COPYRIGHT.GPL"
FILES:${PN} += "${systemd_system_unitdir}/openvpn-server@.service \
                ${systemd_system_unitdir}/openvpn-client@.service \
                ${nonarch_libdir}/tmpfiles.d \
               "
FILES:${PN}-sample = "${sysconfdir}/openvpn/sample/ \
                     "
