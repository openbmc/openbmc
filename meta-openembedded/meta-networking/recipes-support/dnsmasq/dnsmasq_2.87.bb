require dnsmasq.inc

SRC_URI[dnsmasq-2.87.sha256sum] = "ae39bffde9c37e4d64849b528afeb060be6bad6d1044a3bd94a49fce41357284"
SRC_URI += "\
    file://lua.patch \
"

