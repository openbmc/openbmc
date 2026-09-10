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
    file://daemonize.cpp \
    file://daemonize.hpp \
    file://test-cpp-example.cpp \
    file://cpp-example.conf \
    file://config.h.in \
    file://cpp-example.service \
    file://cpp-example.init \
    file://.clang-format \
    file://.clang-tidy \
    file://run-ptest \
"

S = "${UNPACKDIR}"

inherit ptest useradd systemd update-rc.d

# Systemd and SysV init support
SYSTEMD_SERVICE:${PN} = "${BPN}.service"

INITSCRIPT_NAME = "${BPN}"
INITSCRIPT_PARAMS = "defaults 99"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

# Create cpp-example user and group
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${EX_SERVICE_USER}"
USERADD_PARAM:${PN} = "--system --home /var/lib/${EX_SERVICE_USER} --no-create-home --shell /bin/false --gid ${EX_SERVICE_USER} ${EX_SERVICE_USER}"

EX_SERVICE_USER ?= "${BPN}"
EX_BINARY_NAME ?= "${BPN}"
EX_TEST_BINARY_NAME ?= "test-${EX_BINARY_NAME}"

do_install_ptest() {
    sed -i -e 's|@TEST_BINARY_NAME@|${EX_TEST_BINARY_NAME}|g' ${D}${PTEST_PATH}/run-ptest
}

do_install:append() {
    # Install configuration file owned by the recipe's unprivileged user.
    # Note: this requires static UIDs/GIDs (USERADDEXTENSION = "useradd-staticids")
    # so that the UID embedded by pseudo during do_install matches the UID assigned
    # in the final image. devtool deploy-target is a raw file copy and does not run
    # pkg_postinst, so the UID in ${D} must already be correct.
    install -d ${D}${sysconfdir}
    install -m 0644 -g ${EX_SERVICE_USER} -o ${EX_SERVICE_USER} ${S}/cpp-example.conf ${D}${sysconfdir}/${BPN}.conf
    sed -i -e 's|@BINARY_NAME@|${BPN}|g' ${D}${sysconfdir}/${BPN}.conf

    # Install service files or init scripts and substitute placeholders in service files
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/cpp-example.service ${D}${systemd_system_unitdir}/${BPN}.service
        sed -i \
            -e 's|@BINDIR@|${bindir}|g' \
            -e 's|@BINARY_NAME@|${EX_BINARY_NAME}|g' \
            -e 's|@USER@|${EX_SERVICE_USER}|g' \
            -e 's|@GROUP@|${EX_SERVICE_USER}|g' \
            ${D}${systemd_system_unitdir}/${BPN}.service
    else
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${S}/cpp-example.init ${D}${sysconfdir}/init.d/${BPN}
        sed -i \
            -e 's|@BINDIR@|${bindir}|g' \
            -e 's|@BINARY_NAME@|${EX_BINARY_NAME}|g' \
            -e 's|@USER@|${EX_SERVICE_USER}|g' \
            -e 's|@GROUP@|${EX_SERVICE_USER}|g' \
            ${D}${sysconfdir}/init.d/${BPN}
    fi
}

FILES:${PN} += " \
    ${systemd_system_unitdir}/${BPN}.service \
    ${sysconfdir}/${BPN}.conf \
"
