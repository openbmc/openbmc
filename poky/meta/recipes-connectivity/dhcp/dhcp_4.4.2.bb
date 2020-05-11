require dhcp.inc

SRC_URI += "file://0001-define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
            file://0002-dhclient-dbus.patch \
            file://0003-link-with-lcrypto.patch \
            file://0004-Fix-out-of-tree-builds.patch \
            file://0005-dhcp-client-fix-invoke-dhclient-script-failed-on-Rea.patch \
            file://0007-Add-configure-argument-to-make-the-libxml2-dependenc.patch \
            file://0009-remove-dhclient-script-bash-dependency.patch \
            file://0012-dhcp-correct-the-intention-for-xml2-lib-search.patch \
            file://0013-fixup_use_libbind.patch \
            file://0001-workaround-busybox-limitation-in-linux-dhclient-script.patch \
"

SRC_URI[md5sum] = "2afdaf8498dc1edaf3012efdd589b3e1"
SRC_URI[sha256sum] = "1a7ccd64a16e5e68f7b5e0f527fd07240a2892ea53fe245620f4f5f607004521"

LDFLAGS_append = " -pthread"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
