SUMMARY = "Fortinet SSLVPN support for NetworkManager"
SECTION = "net/misc"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "glib-2.0-native libxml2-native networkmanager ppp"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase gettext useradd

SRC_URI = " \
    ${GNOME_MIRROR}/NetworkManager-fortisslvpn/${@gnome_verdir("${PV}")}/NetworkManager-fortisslvpn-${PV}.tar.xz \
    file://0001-fix-ppp-2.5.0-build.patch \
    file://0002-fix-ppp-2.5.0-build.patch \
"
SRC_URI[sha256sum] = "b055e26349b516b23585798ab3ef57b436b014800e92a8ac732cfc8e76c5dafa"

S = "${WORKDIR}/NetworkManager-fortisslvpn-${PV}"

# meta-gnome in layers is required using gnome:
PACKAGECONFIG[gnome] = "--with-gnome,--without-gnome,gtk+3 libnma libsecret,"
PACKAGECONFIG[gtk4] = "--with-gtk4,--without-gtk4,gtk4,"

EXTRA_OECONF = "--with-pppd-plugin-dir=${libdir}/pppd/${@get_ppp_version(d)}"

def get_ppp_version(d):
    import re

    pppd_plugin = d.expand('${STAGING_LIBDIR}/pppd')
    if not os.path.isdir(pppd_plugin):
        return None

    bb.debug(1, "pppd plugin dir %s" % pppd_plugin)
    r = re.compile(r"\d*\.\d*\.\d*")
    for f in os.listdir(pppd_plugin):
        if os.path.isdir(os.path.join(pppd_plugin, f)):
            ma = r.match(f)
            if ma:
                bb.debug(1, "pppd version dir %s" % f)
                return f
            else:
                bb.debug(1, "under pppd plugin dir %s" % f)

    return None

# gdbus-codegen requires target directories to exist
do_configure:append() {
    mkdir -p ${B}/properties
    mkdir -p ${B}/src
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system nm-fortisslvpn"

FILES:${PN} += " \
    ${libdir}/NetworkManager/*.so \
    ${libdir}/pppd/*/*.so \
    ${nonarch_libdir}/NetworkManager/VPN/nm-fortisslvpn-service.name \
"

FILES:${PN}-staticdev += " \
    ${libdir}/NetworkManager/*.a \
    ${libdir}/pppd/*/*.a \
"

RDEPENDS:${PN} = " \
    networkmanager \
    openfortivpn \
    ppp \
"
