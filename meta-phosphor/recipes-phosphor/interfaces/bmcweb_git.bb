LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
DEPENDS = " \
    boost \
    cli11 \
    gtest \
    libpam \
    libtinyxml2 \
    nghttp2 \
    nlohmann-json \
    openssl \
    sdbusplus \
    zlib \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
"
SRCREV = "dded61d1d52fd466cc7d7737eeb2b04229108fcd"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/bmcweb.git;branch=master;protocol=https"
SRC_URI += " \
    file://run-ptest \
"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "bmcweb.service bmcweb.socket"

inherit systemd
inherit useradd
inherit pkgconfig meson ptest

PACKAGECONFIG ??= " \
    http-zstd \
    kvm \
    mutual-tls-auth \
    redfish-bmc-journal \
"

PACKAGECONFIG[dbus-rest] = "-Drest=enabled,-Drest=disabled"
PACKAGECONFIG[http-zstd] = "-Dhttp-zstd=enabled,-Dhttp-zstd=disabled,zstd"
PACKAGECONFIG[insecure-redfish-expand] = "-Dinsecure-enable-redfish-query=enabled"
PACKAGECONFIG[kvm] = "-Dkvm=enabled,-Dkvm=disabled"
PACKAGECONFIG[mutual-tls-auth] = "-Dmutual-tls-auth=enabled,-Dmutual-tls-auth=disabled"
PACKAGECONFIG[redfish-allow-deprecated-power-thermal] = "-Dredfish-allow-deprecated-power-thermal=enabled,-Dredfish-allow-deprecated-power-thermal=disabled"
PACKAGECONFIG[redfish-bmc-journal] = "-Dredfish-bmc-journal=enabled,-Dredfish-bmc-journal=disabled"
PACKAGECONFIG[redfish-cpu-log] = "-Dredfish-cpu-log=enabled,-Dredfish-cpu-log=disabled"
PACKAGECONFIG[redfish-dbus-log] = "-Dredfish-dbus-log=enabled,-Dredfish-dbus-log=disabled"
PACKAGECONFIG[redfish-dump-log] = "-Dredfish-dump-log=enabled,-Dredfish-dump-log=disabled"
PACKAGECONFIG[redfish-host-logger] = "-Dredfish-host-logger=enabled,-Dredfish-host-logger=disabled"

MUTUAL_TLS_PARSING = "CommonName"

EXTRA_OEMESON = " \
    --buildtype=minsize \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'mutual-tls-auth', \
        '-Dmutual-tls-common-name-parsing-default=' + d.getVar('MUTUAL_TLS_PARSING', True), \
        '', d)} \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${B}/*_test ${D}${PTEST_PATH}/test/
}

RDEPENDS:${PN} += " \
    jsnbd \
    phosphor-objmgr \
"

FILES:${PN} += "${datadir}/** "

USERADD_PACKAGES = "${PN}"

# add a user called bmcweb for the server to assume
USERADD_PARAM:${PN} = "-r -s /sbin/nologin bmcweb"

GROUPADD_PARAM:${PN} = "web; redfish; hostconsole"
FULL_OPTIMIZATION:append = " -Os"
