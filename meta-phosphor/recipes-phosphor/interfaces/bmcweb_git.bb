inherit systemd
inherit useradd

USERADD_PACKAGES = "${PN}"

# add a user called httpd for the server to assume
USERADD_PARAM:${PN} = "-r -s /usr/sbin/nologin bmcweb"
GROUPADD_PARAM:${PN} = "web; redfish"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "git://github.com/openbmc/bmcweb.git"

PV = "1.0+git${SRCPV}"
SRCREV = "3e8c7dfce4869711e22ccc76c90cebb55984d36c"

S = "${WORKDIR}/git"

inherit meson ptest

SRC_URI += " \
    file://run-ptest \
"

DEPENDS = " \
    openssl \
    zlib \
    boost \
    boost-url \
    libpam \
    sdbusplus \
    gtest \
    nlohmann-json \
    libtinyxml2 \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
"

RDEPENDS:${PN} += " \
    jsnbd \
    phosphor-mapper \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${B}/*_test ${D}${PTEST_PATH}/test/
}

FILES:${PN} += "${datadir}/** "


EXTRA_OEMESON = " \
    --buildtype=minsize \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
    -Dyocto-deps=enabled \
"

SYSTEMD_SERVICE:${PN} += "bmcweb.service bmcweb.socket"

FULL_OPTIMIZATION = "-Os "
