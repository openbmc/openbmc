SUMMARY = "An open source implementation of the diameter protocol"
DESCRIPTION = "\
freeDiameter is an open source Diameter protocol implementation \
(RFC3588). It provides an extensible platform for deploying a \
Diameter network for your Authentication, Authorization and \
Accounting needs."

HOMEPAGE = "http://www.freediameter.net"

DEPENDS = "flex bison cmake-native libgcrypt gnutls libidn lksctp-tools virtual/kernel bison-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

fd_pkgname = "freeDiameter"

SRC_URI = "\
    http://www.freediameter.net/hg/${fd_pkgname}/archive/${PV}.tar.gz;downloadfilename=${fd_pkgname}-${PV}.tar.gz \
    file://Replace-murmurhash-algorithm-with-Robert-Jenkin-s-ha.patch \
    file://freediameter.service \
    file://freediameter.init \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'file://install_test.patch file://run-ptest file://pass-ptest-env.patch', '', d)} \
    file://freeDiameter.conf \
    file://0001-libfdcore-sctp.c-update-the-old-sctp-api-check.patch \
    file://0001-Fix-testcnx-expired-CA-data.patch \
    "

SRC_URI[md5sum] = "73ce230b4789f9f28fff77cbc83c65af"
SRC_URI[sha256sum] = "ce05b4bf2a04cd2f472e77ba4b86fbfca690bfc83e51da8ce0e575804b763eda"

S = "${WORKDIR}/${fd_pkgname}-${PV}"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=69bdc1d97648a2d35914563fcbbb361a"

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

do_install_append() {
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

}

do_install_ptest() {
    sed -i "s#\(EXTENSIONS_DIR=\).*\$#\1${libdir}/${fd_pkgname}/#" ${D}${PTEST_PATH}/run-ptest
    mv ${D}${PTEST_PATH}-tests/* ${D}${PTEST_PATH}/
    rmdir ${D}${PTEST_PATH}-tests
    install -m 0644 ${B}/tests/CTestTestfile.cmake ${D}${PTEST_PATH}/
}

FILES_${PN}-dbg += "${libdir}/${fd_pkgname}/.debug/*"

# include the extensions in main package
FILES_${PN} += "${libdir}/${fd_pkgname}/*"

RDEPENDS_${PN}  = "glib-2.0 gnutls libidn"
RDEPENDS_${PN} += "openssl openssl-conf openssl-engines"
RRECOMMENDS_${PN} += "kernel-module-tipc kernel-module-sctp" 
RRECOMMENDS_${PN} += "kernel-module-udp-tunnel kernel-module-ipip"
RDEPENDS_${PN}-ptest = "cmake"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME_${PN} = "${BPN}"
INITSCRIPT_PARAMS$_${PN} = "start 30 . stop 70 0 1 2 3 4 5 6 ."

SYSTEMD_SERVICE_${PN} = "freediameter.service"
SYSTEMD_AUTO_ENABLE = "disable"

CONFFILES_${PN} = "${sysconfdir}/freediameter.conf"

