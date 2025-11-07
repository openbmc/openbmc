SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += " \
        ${PYTHON_PN}-inflection-native \
        ${PYTHON_PN}-jsonschema-native \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        boost \
        nlohmann-json \
        stdexec \
        systemd \
        "

S = "${WORKDIR}/git"

inherit pkgconfig meson
inherit python3native

EXTRA_OEMESON:append = " \
        -Dtests=disabled \
        -Dexamples=disabled \
        "

include sdbusplus-rev.inc
