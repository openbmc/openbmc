require dhcp.inc

SRC_URI += "file://0001-define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
            file://0002-dhclient-dbus.patch \
            file://0003-link-with-lcrypto.patch \
            file://0004-Fix-out-of-tree-builds.patch \
            file://0005-dhcp-client-fix-invoke-dhclient-script-failed-on-Rea.patch \
            file://0006-site.h-enable-gentle-shutdown.patch \
            file://0007-Add-configure-argument-to-make-the-libxml2-dependenc.patch \
            file://0008-tweak-to-support-external-bind.patch \
            file://0009-remove-dhclient-script-bash-dependency.patch \
            file://0010-build-shared-libs.patch \
            file://0011-Moved-the-call-to-isc_app_ctxstart-to-not-get-signal.patch \
            file://0012-dhcp-correct-the-intention-for-xml2-lib-search.patch \
           "

SRC_URI[md5sum] = "afa6e9b3eb7539ea048421a82c668adc"
SRC_URI[sha256sum] = "a41eaf6364f1377fe065d35671d9cf82bbbc8f21207819b2b9f33f652aec6f1b"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
