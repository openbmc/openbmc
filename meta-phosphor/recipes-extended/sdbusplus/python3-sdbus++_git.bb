SUMMARY = "sdbus++ dbus API / binding generator"
DESCRIPTION = "Generates bindings against sdbusplus for dbus APIs"

inherit autotools pkgconfig
inherit obmc-phosphor-python3-autotools

include sdbusplus-rev.inc

# Provide these aliases temporarily until everyone can move over to the
# new package name.
PROVIDES_class-native += "sdbusplus-native"
PROVIDES_class-nativesdk += "sdbusplus-nativesdk"

DEPENDS += " \
    autoconf-archive-native \
    ${PYTHON_PN}-inflection-native \
    ${PYTHON_PN}-mako-native \
    ${PYTHON_PN}-pyyaml-native \
    "

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-inflection \
    ${PYTHON_PN}-mako \
    ${PYTHON_PN}-pyyaml \
    "

PACKAGECONFIG ??= "transaction"
PACKAGECONFIG[libsdbusplus] = "--enable-libsdbusplus,--disable-libsdbusplus,systemd,libsystemd"
PACKAGECONFIG[transaction] = "--enable-transaction,--disable-transaction"

BBCLASSEXTEND += "native nativesdk"
