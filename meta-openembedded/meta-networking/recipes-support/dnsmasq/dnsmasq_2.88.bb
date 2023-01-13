require dnsmasq.inc

SRC_URI[dnsmasq-2.88.sha256sum] = "da9d26aa3f3fc15f3b58b94edbb9ddf744cbce487194ea480bd8e7381b3ca028"
SRC_URI += "\
    file://lua.patch \
"

