require dnsmasq.inc

SRC_URI[dnsmasq-2.79.md5sum] = "5d7120a46d0c16a334f46757d7e2ba55"
SRC_URI[dnsmasq-2.79.sha256sum] = "77512dd6f31ffd96718e8dcbbf54f02c083f051d4cca709bd32540aea269f789"
SRC_URI += "\
    file://lua.patch \
"

