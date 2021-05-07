require dnsmasq.inc

SRC_URI[dnsmasq-2.85.md5sum] = "4079e1e6e1065e4bd14ded268cdd7bd7"
SRC_URI[dnsmasq-2.85.sha256sum] = "f36b93ecac9397c15f461de9b1689ee5a2ed6b5135db0085916233053ff3f886"
SRC_URI += "\
    file://lua.patch \
"

