SUMMARY = "A full-featured SSL VPN solution via tun device."
HOMEPAGE = "https://openvpn.net/"
SECTION = "net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=89196bacc47ed37a5b242a535661a049"
DEPENDS = "lzo lz4 openssl iproute2 libcap-ng ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} ${@bb.utils.contains('PTEST_ENABLED', '1', 'cmocka', '', d)} "

inherit autotools systemd update-rc.d pkgconfig ptest

SRC_URI = "http://swupdate.openvpn.org/community/releases/${BP}.tar.gz \
           file://0001-configure.ac-eliminate-build-path-from-openvpn-versi.patch \
           file://openvpn \
           file://run-ptest \
          "

UPSTREAM_CHECK_URI = "https://openvpn.net/community-downloads"

SRC_URI[sha256sum] = "1c610fddeb686e34f1367c347e027e418e07523a10f4d8ce4a2c2af2f61a1929"

CVE_STATUS[CVE-2020-27569] = "not-applicable-config: Applies only Aviatrix OpenVPN client, not openvpn"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "openvpn"
INITSCRIPT_PARAMS:${PN} = "start 10 2 3 4 5 . stop 70 0 1 6 ."

CFLAGS += "-fno-inline"

# I want openvpn to be able to read password from file (hrw)
EXTRA_OECONF += "--enable-iproute2"
EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '', '--disable-plugin-auth-pam', d)}"

# Explicitly specify IPROUTE to bypass the configure-time check for /sbin/ip on the host.
EXTRA_OECONF += "IPROUTE=${base_sbindir}/ip"

EXTRA_OECONF += "SYSTEMD_UNIT_DIR=${systemd_system_unitdir} \
                 TMPFILES_DIR=${nonarch_libdir}/tmpfiles.d \
                "

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
                  "

PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

RDEPENDS:${PN}-ptest:append = " make bash"

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
    for x in `find ${B}/tests/unit_tests -name Makefile -exec grep -l buildtest-TESTS {} \;`; do
        dir=`dirname ${x}`
        case $dir in
            *example*)   
                echo "Skipping directory: $dir"
                ;;
            *)           
                oe_runmake -C ${dir} buildtest-TESTS
                ;;
        esac
    done
}

do_install_ptest() {
    for x in $(find ${B}/tests/unit_tests -name Makefile -exec grep -l buildtest-TESTS {} \;); do
        dir=$(dirname ${x})

        if [[ "$dir" == *example* ]]; then
            continue
        fi

        target_dir="${D}/${PTEST_PATH}/unit_tests/$(basename ${dir})"
        mkdir -p ${target_dir}
        cp -f ${dir}/Makefile ${target_dir}/        
        sed -i "s/^Makefile:/MM:/g" ${target_dir}/Makefile
        sed -i 's/^#TESTS = $(am__EXEEXT_4)/TESTS = $(am__EXEEXT_4)/' ${target_dir}/Makefile

        for testfile in $(find ${dir} -name "*testdriver"); do
            cp -rf ${testfile} ${target_dir}/
        done
    done
    sed -i 's|find ./|find ${PTEST_PATH}|g' ${D}${PTEST_PATH}/run-ptest
    sed -i 's|${top_builddir}/src/openvpn|${sbindir}|g' ${S}/tests/t_lpback.sh
    cp -f ${S}/tests/t_lpback.sh ${D}/${PTEST_PATH}
    cp -f ${B}/tests/Makefile ${D}/${PTEST_PATH}
    sed -i "s/^Makefile:/MM:/g" ${D}/${PTEST_PATH}/Makefile
    sed -i "s/^test_scripts = t_client.sh t_lpback.sh t_cltsrv.sh/test_scripts = t_lpback.sh/g" ${D}/${PTEST_PATH}/Makefile
    
}

PACKAGES =+ " ${PN}-sample "

RRECOMMENDS:${PN} = "kernel-module-tun"

FILES:${PN}-dbg += "${libdir}/openvpn/plugins/.debug"
FILES:${PN} += "${systemd_system_unitdir}/openvpn-server@.service \
                ${systemd_system_unitdir}/openvpn-client@.service \
                ${nonarch_libdir}/tmpfiles.d \
               "
FILES:${PN}-sample = "${sysconfdir}/openvpn/sample/ \
                     "
