require dnsmasq.inc

SRC_URI[dnsmasq-2.82.md5sum] = "3c710dee3edba510ed11a6e3d9e0d9cb"
SRC_URI[dnsmasq-2.82.sha256sum] = "62f33bfac1a1b4a5dab8461b4664e414f7d6ced1d2cf141e9cdf9c3c2a424f65"
SRC_URI += "\
    file://lua.patch \
"

