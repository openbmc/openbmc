require dhcp.inc

SRC_URI += "file://0001-define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
            file://0002-dhclient-dbus.patch \
            file://0003-link-with-lcrypto.patch \
            file://0004-Fix-out-of-tree-builds.patch \
            file://0005-dhcp-client-fix-invoke-dhclient-script-failed-on-Rea.patch \
            file://0006-site.h-enable-gentle-shutdown.patch \
            file://0007-Add-configure-argument-to-make-the-libxml2-dependenc.patch \
            file://0009-remove-dhclient-script-bash-dependency.patch \
            file://0010-build-shared-libs.patch \
            file://0011-Moved-the-call-to-isc_app_ctxstart-to-not-get-signal.patch \
            file://0012-dhcp-correct-the-intention-for-xml2-lib-search.patch \
            file://CVE-2017-3144.patch \
           "

# use internal libisc libraries which are based on bind 9.9.11 - there
# is a bug in bind 9.10.x (normally supplied by OE) that prevents
# dhcpd/dhclient from shutting down cleanly on sigterm and from running
# in the background
#
# [https://bugzilla.yoctoproject.org/show_bug.cgi?id=12744]
#
# remove "ext-bind" and
# also set PARALLEL_MAKE = "" 
# [ Yocto 12744 ]
#
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'ext-bind', 'file://0008-tweak-to-support-external-bind.patch', '', d)}"

SRC_URI[md5sum] = "afa6e9b3eb7539ea048421a82c668adc"
SRC_URI[sha256sum] = "a41eaf6364f1377fe065d35671d9cf82bbbc8f21207819b2b9f33f652aec6f1b"

PACKAGECONFIG ?= "ext-bind"
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
PACKAGECONFIG[ext-bind] = "--with-libbind=${STAGING_LIBDIR}, --without-libbind, bind"
