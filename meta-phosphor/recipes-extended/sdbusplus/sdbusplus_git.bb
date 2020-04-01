SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."

inherit autotools pkgconfig
inherit obmc-phosphor-python3-autotools

include sdbusplus-rev.inc

DEPENDS += " \
        autoconf-archive-native \
        ${PYTHON_PN}-inflection-native \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        "

PACKAGECONFIG ??= "libsdbusplus transaction"
PACKAGECONFIG[libsdbusplus] = "--enable-libsdbusplus,--disable-libsdbusplus,systemd,libsystemd"
PACKAGECONFIG[transaction] = "--enable-transaction,--disable-transaction"

# Remove unused sdbus++ contents (included in python3-sdbus++ package).
do_install_append() {
    rm ${D}/${bindir}/sdbus++
    rmdir ${D}/${bindir} || true
    rm -rf ${D}/${PYTHON_SITEPACKAGES_DIR}
    rmdir ${D}/${libdir}/${PYTHON_DIR} || true
}
