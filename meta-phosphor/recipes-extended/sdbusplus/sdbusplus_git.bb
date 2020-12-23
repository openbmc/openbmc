SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."

inherit meson
inherit python3native
include sdbusplus-rev.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

S = "${WORKDIR}/git"

DEPENDS += " \
        ${PYTHON_PN}-inflection-native \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        boost \
        googletest \
        systemd \
        "

EXTRA_OEMESON += " \
        -Dtests=disabled \
        -Dexamples=disabled \
        "
