SUMMARY = "C++ bindings for systemd dbus APIs"
DESCRIPTION = "C++ bindings for systemd dbus APIs."
HOMEPAGE = "http://github.com/openbmc/sdbusplus"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-python-autotools

DEPENDS += " \
        autoconf-archive-native \
        python-inflection-native \
        python-mako-native \
        python-pyyaml-native \
        "
RDEPENDS_sdbus++ += " \
        python-inflection \
        python-mako \
        python-pyyaml \
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
        python-inflection-native \
        python-mako-native \
        python-pyyaml-native \
        "

SRC_URI += "git://github.com/openbmc/sdbusplus"
SRCREV = "6b4fb2969cd0c853ff6aa7f9bdd3ddaa0081c204"

PACKAGECONFIG ??= "libsdbusplus transaction"
PACKAGECONFIG[libsdbusplus] = "--enable-libsdbusplus,--disable-libsdbusplus,systemd,libsystemd"
PACKAGECONFIG[transaction] = "--enable-transaction,--disable-transaction"

S = "${WORKDIR}/git"

PROVIDES_prepend = "sdbus++ "
PACKAGE_BEFORE_PN = "sdbus++"
FILES_sdbus++_append = " ${bindir}/sdbus++"
PYTHON_AUTOTOOLS_PACKAGE = "sdbus++"

PACKAGECONFIG_remove_class-native = "libsdbusplus"
PACKAGECONFIG_remove_class-nativesdk = "libsdbusplus"

BBCLASSEXTEND += "native nativesdk"
