require dnsmasq.inc

SRC_URI[dnsmasq-2.84.md5sum] = "6bf24b5bcf9293db2941fbdb545c1133"
SRC_URI[dnsmasq-2.84.sha256sum] = "4caf385376f34fae5c55244a1f870dcf6f90e037bb7c4487210933dc497f9c36"
SRC_URI += "\
    file://lua.patch \
"

