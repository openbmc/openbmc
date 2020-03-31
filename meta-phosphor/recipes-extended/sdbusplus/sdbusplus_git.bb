SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."

inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools

include sdbusplus-rev.inc

DEPENDS += " \
        autoconf-archive-native \
        ${PYTHON_PN}-inflection-native \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        "
RDEPENDS_sdbus++ += " \
        ${PYTHON_PN} \
        ${PYTHON_PN}-inflection \
        ${PYTHON_PN}-mako \
        ${PYTHON_PN}-pyyaml \
        "

# sdbus++ has a handful of runtime dependencies on other python packages.
# Bitbake doesn't do anything with RDEPENDS in native context because
# native context doesn't have packages.
#
# While technically sdbus++ doesn't require its runtime dependencies to be
# installed to build, work around the above native context behavior
# by adding a build dependency so that clients don't have to DEPEND
# on sdbus++ runtime dependencies manually.

DEPENDS_append_class-native = " \
        ${PYTHON_PN}-inflection-native \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        "

PACKAGECONFIG ??= "libsdbusplus transaction"
PACKAGECONFIG[libsdbusplus] = "--enable-libsdbusplus,--disable-libsdbusplus,systemd,libsystemd"
PACKAGECONFIG[transaction] = "--enable-transaction,--disable-transaction"

PROVIDES_prepend = "sdbus++ "
PACKAGE_BEFORE_PN = "sdbus++"
FILES_sdbus++_append = " ${bindir}/sdbus++"
PYTHON_AUTOTOOLS_PACKAGE = "sdbus++"

PACKAGECONFIG_remove_class-native = "libsdbusplus"
PACKAGECONFIG_remove_class-nativesdk = "libsdbusplus"

BBCLASSEXTEND += "native nativesdk"
