#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "json-c"

PV = "1.0"

SRC_URI = "\
    file://cpp-example.cpp \
    file://cpp-example-lib.hpp \
    file://cpp-example-lib.cpp \
    file://test-cpp-example.cpp \
    file://cpp-example.conf \
    file://config.h.in \
    file://cpp-example.service \
    file://cpp-example.init \
    file://run-ptest \
"

S = "${UNPACKDIR}"

inherit ptest useradd systemd update-rc.d

# Systemd and SysV init support
SYSTEMD_SERVICE:${PN} = "${BPN}.service"

INITSCRIPT_NAME = "${BPN}"
INITSCRIPT_PARAMS = "defaults 99"

# Create cpp-example user and group
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${BPN}"
USERADD_PARAM:${PN} = "--system --home /var/lib/${BPN} --no-create-home --shell /bin/false --gid ${BPN} ${BPN}"

EX_BINARY_NAME ?= "${BPN}"

do_install:append() {
    # Install configuration file owned by unprivileged user
    install -d ${D}${sysconfdir}
    install -m 0644 -g ${BPN} -o ${BPN} ${S}/cpp-example.conf ${D}${sysconfdir}/${BPN}.conf
    sed -i -e 's|@BINARY_NAME@|${BPN}|g' ${D}${sysconfdir}/${BPN}.conf

    # Install service files or init scripts and substitute placeholders in service files
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/cpp-example.service ${D}${systemd_system_unitdir}/${BPN}.service
        sed -i \
            -e 's|@BINDIR@|${bindir}|g' \
            -e 's|@BINARY_NAME@|${EX_BINARY_NAME}|g' \
            -e 's|@USER@|${BPN}|g' \
            -e 's|@GROUP@|${BPN}|g' \
            ${D}${systemd_system_unitdir}/${BPN}.service
    else
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${S}/cpp-example.init ${D}${sysconfdir}/init.d/${BPN}
        sed -i \
            -e 's|@BINDIR@|${bindir}|g' \
            -e 's|@BINARY_NAME@|${EX_BINARY_NAME}|g' \
            -e 's|@USER@|${BPN}|g' \
            -e 's|@GROUP@|${BPN}|g' \
            ${D}${sysconfdir}/init.d/${BPN}
    fi
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/${BPN}.service \
    ${sysconfdir}/${BPN}.conf \
"
