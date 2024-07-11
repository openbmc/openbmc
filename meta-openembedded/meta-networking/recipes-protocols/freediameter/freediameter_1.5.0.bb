SUMMARY = "An open source implementation of the diameter protocol"
DESCRIPTION = "\
freeDiameter is an open source Diameter protocol implementation \
(RFC3588). It provides an extensible platform for deploying a \
Diameter network for your Authentication, Authorization and \
Accounting needs."

HOMEPAGE = "http://www.freediameter.net"

DEPENDS = "flex bison cmake-native libgcrypt gnutls libidn2 lksctp-tools virtual/kernel bison-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

fd_pkgname = "freeDiameter"

PV .= "+git"
SRCREV = "f9f1e464e6c675d222b3be4cab9c13408d544c83"
SRC_URI = "git://github.com/freeDiameter/freeDiameter;protocol=https;branch=master \
    file://Replace-murmurhash-algorithm-with-Robert-Jenkin-s-ha.patch \
    file://run-ptest \
    file://freediameter.service \
    file://freediameter.init \
    file://freeDiameter.conf \
    file://install_test.patch \
    file://0001-tests-use-EXTENSIONS_DIR.patch \
    "

S = "${WORKDIR}/git"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=868c059b6147748b1d621e500feeac4f"

PTEST_PATH = "${libdir}/${fd_pkgname}/ptest"

inherit cmake pkgconfig update-rc.d ptest systemd

EXTRA_OECMAKE = " \
    -DDEFAULT_CONF_PATH:PATH=${sysconfdir}/${fd_pkgname} \
    -DBUILD_DBG_MONITOR:BOOL=ON  \
    -DBUILD_TEST_APP:BOOL=ON \
    -DBUILD_TESTING:BOOL=ON \
    -DBUILD_APP_RADGW:BOOL=ON \
    -DBUILD_APP_REDIRECT:BOOL=ON \
    -DBUILD_TEST_ACCT:BOOL=ON \
    -DBUILD_TEST_NETEMUL:BOOL=ON \
    -DBUILD_TEST_RT_ANY:BOOL=ON \
    -DINSTALL_LIBRARY_SUFFIX:PATH=${baselib} \
    -DINSTALL_EXTENSIONS_SUFFIX:PATH=${baselib}/${fd_pkgname} \
    -DEXTENSIONS_DIR:PATH=${libdir}/${fd_pkgname} \
    -DINSTALL_TEST_SUFFIX:PATH=${PTEST_PATH}-tests \
    -DCMAKE_SKIP_RPATH:BOOL=ON \
"
# INSTALL_LIBRARY_SUFFIX is relative to CMAKE_INSTALL_PREFIX
# specify it on cmd line will fix the SET bug in CMakeList.txt

# -DBUILD_APP_ACCT:BOOL=ON This needs POSTGRESQL support

# -DBUILD_APP_DIAMEAP:BOOL=ON  -DBUILD_APP_SIP:BOOL=ON -DBUILD_TEST_SIP:BOOL=ON
# These need MySQL support

# -DBUILD_DBG_INTERACTIVE:BOOL=ON This needs SWIG support

# -DALL_EXTENSIONS=ON will enable all

FD_KEY ?="${BPN}.key"
FD_PEM ?= "${BPN}.pem"
FD_CA ?= "${BPN}.pem"
FD_DH_PEM ?= "${BPN}-dh.pem"
FD_HOSTNAME ?= "${MACHINE}"
FD_REALM ?= "openembedded.org"

do_install:append() {
    # install the sample configuration files
    install -d -m 0755 ${D}${sysconfdir}/${fd_pkgname}
    for i in ${S}/doc/*.conf.sample; do
        install -m 0644 $i ${D}${sysconfdir}/${fd_pkgname}/
    done
    mv ${D}${sysconfdir}/${fd_pkgname}/freediameter.conf.sample \
       ${D}${sysconfdir}/${fd_pkgname}/freeDiameter.conf.sample
    install -d ${D}${sysconfdir}/freeDiameter
	 install ${WORKDIR}/freeDiameter.conf ${D}${sysconfdir}/${fd_pkgname}/freeDiameter.conf

    # install daemon init related files
    install -d -m 0755 ${D}${sysconfdir}/default
    install -d -m 0755 ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/contrib/debian/freediameter-daemon.default \
      ${D}${sysconfdir}/default/${BPN}
    install -m 0755 ${WORKDIR}/freediameter.init ${D}${sysconfdir}/init.d/${BPN}

    # install for systemd
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/freediameter.service ${D}${systemd_system_unitdir}
    sed -i -e 's,@BINDIR@,${bindir},g' ${D}${systemd_system_unitdir}/*.service

    cat >> ${D}${sysconfdir}/freeDiameter/freeDiameter.conf <<EOF
## OE specific ##
#Identity="${FD_HOSTNAME}";
Identity = "${FD_HOSTNAME}.${FD_REALM}";
Realm = "${FD_REALM}";
Port = 30868;
SecPort = 30869;
TLS_Cred = "/etc/freeDiameter/${FD_PEM}" , "/etc/freeDiameter/${FD_KEY}";
TLS_CA = "/etc/freeDiameter/${FD_CA}";
TLS_DH_File = "/etc/freeDiameter/${FD_DH_PEM}";
EOF

    # create self cert
    openssl req -x509 -config ${STAGING_DIR_NATIVE}/etc/ssl/openssl.cnf -newkey rsa:4096 -sha256 -nodes -out ${D}${sysconfdir}/freeDiameter/${FD_PEM} -keyout ${D}${sysconfdir}/freeDiameter/${FD_KEY} -days 3650 -subj '/CN=${FD_HOSTNAME}.${FD_REALM}'
    openssl dhparam -out ${D}${sysconfdir}/freeDiameter/${FD_DH_PEM} 1024

    find ${B} \( -name "*.c" -o -name "*.h" \) -exec sed -i -e 's#${WORKDIR}##g' {} \;
}

do_install_ptest() {
    mv ${D}${PTEST_PATH}-tests/* ${D}${PTEST_PATH}/
    rmdir ${D}${PTEST_PATH}-tests
    install -m 0644 ${B}/tests/CTestTestfile.cmake ${D}${PTEST_PATH}/
    sed -i -e 's#${WORKDIR}##g' ${D}${PTEST_PATH}/CTestTestfile.cmake
    sed -i "/^set_tests_properties/d" ${D}${PTEST_PATH}/CTestTestfile.cmake
}

FILES:${PN}-dbg += "${libdir}/${fd_pkgname}/.debug/*"

# include the extensions in main package
FILES:${PN} += "${libdir}/${fd_pkgname}/*"

RDEPENDS:${PN}  = "glib-2.0 gnutls libidn"
RDEPENDS:${PN} += "openssl openssl-conf openssl-engines"
RRECOMMENDS:${PN} += "kernel-module-tipc kernel-module-sctp" 
RRECOMMENDS:${PN} += "kernel-module-udp-tunnel kernel-module-ipip"
RDEPENDS:${PN}-ptest = "cmake"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "${BPN}"
INITSCRIPT_PARAMS:${PN} = "start 30 . stop 70 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE:${PN} = "freediameter.service"
SYSTEMD_AUTO_ENABLE = "disable"

CONFFILES:${PN} = "${sysconfdir}/freediameter.conf"

