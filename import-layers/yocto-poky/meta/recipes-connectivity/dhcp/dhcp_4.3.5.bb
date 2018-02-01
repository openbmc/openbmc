require dhcp.inc

SRC_URI += "file://dhcp-3.0.3-dhclient-dbus.patch;striplevel=0 \
            file://link-with-lcrypto.patch \
            file://fixsepbuild.patch \
            file://dhclient-script-drop-resolv.conf.dhclient.patch \
            file://replace-ifconfig-route.patch \
            file://0001-site.h-enable-gentle-shutdown.patch \
            file://libxml2-configure-argument.patch \
            file://tweak-to-support-external-bind.patch \
            file://remove-dhclient-script-bash-dependency.patch \
           "

SRC_URI[md5sum] = "2b5e5b2fa31c2e27e487039d86f83d3f"
SRC_URI[sha256sum] = "eb95936bf15d2393c55dd505bc527d1d4408289cec5a9fa8abb99f7577e7f954"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
