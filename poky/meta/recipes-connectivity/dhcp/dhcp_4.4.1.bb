require dhcp.inc

SRC_URI += "file://0001-define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
            file://0002-dhclient-dbus.patch \
            file://0003-link-with-lcrypto.patch \
            file://0004-Fix-out-of-tree-builds.patch \
            file://0005-dhcp-client-fix-invoke-dhclient-script-failed-on-Rea.patch \
            file://0006-site.h-enable-gentle-shutdown.patch \
            file://0007-Add-configure-argument-to-make-the-libxml2-dependenc.patch \
            file://0009-remove-dhclient-script-bash-dependency.patch \
            file://0012-dhcp-correct-the-intention-for-xml2-lib-search.patch \
            file://0013-fixup_use_libbind.patch \
"

SRC_URI[md5sum] = "18c7f4dcbb0a63df25098216d47b1ede"
SRC_URI[sha256sum] = "2a22508922ab367b4af4664a0472dc220cc9603482cf3c16d9aff14f3a76b608"

LDFLAGS_append = " -pthread"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
