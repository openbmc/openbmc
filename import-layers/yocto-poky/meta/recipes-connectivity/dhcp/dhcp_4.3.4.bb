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

SRC_URI[md5sum] = "0138319fe2b788cf4bdf34fbeaf9ff54"
SRC_URI[sha256sum] = "f5115aee3dd3e6925de4ba47b80ab732ba48b481c8364b6ebade2d43698d607e"

PACKAGECONFIG ?= ""
PACKAGECONFIG[bind-httpstats] = "--with-libxml2,--without-libxml2,libxml2"
