require dnsmasq.inc

SRC_URI += "\
    file://lua.patch \
"

SRC_URI[dnsmasq-2.75.md5sum] = "d99ac126d4fe910c679d88430559669b"
SRC_URI[dnsmasq-2.75.sha256sum] = "f8252c0a0ba162c2cd45f81140c7c17cc40a5fca2b869d1a420835b74acad294"

