require dnsmasq.inc

SRC_URI[dnsmasq-2.81.md5sum] = "e43808177a773014b5892ccba238f7a8"
SRC_URI[dnsmasq-2.81.sha256sum] = "3c28c68c6c2967c3a96e9b432c0c046a5df17a426d3a43cffe9e693cf05804d0"
SRC_URI += "\
    file://lua.patch \
    file://CVE-2020-25681.patch \
    file://CVE-2020-25684.patch \
    file://CVE-2020-25685-1.patch \
    file://CVE-2020-25685-2.patch \
    file://CVE-2020-25686-1.patch \
    file://CVE-2020-25686-2.patch \
    file://CVE-2021-3448.patch \
"
