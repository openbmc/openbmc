require dnsmasq.inc

SRC_URI[dnsmasq-2.86.sha256sum] = "ef15f608a83ee2b1d1d2c1f11d089a7e0ac401ffb0991de73fc01ce5f290e512"
SRC_URI += "\
    file://lua.patch \
    file://CVE-2022-0934.patch \
"

