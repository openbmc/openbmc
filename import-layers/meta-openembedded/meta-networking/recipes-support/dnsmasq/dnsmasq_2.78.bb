require dnsmasq.inc

SRC_URI += "\
    file://lua.patch \
"

SRC_URI[dnsmasq-2.78.md5sum] = "3bb97f264c73853f802bf70610150788"
SRC_URI[dnsmasq-2.78.sha256sum] = "c92e5d78aa6353354d02aabf74590d08980bb1385d8a00b80ef9bc80430aa1dc"
