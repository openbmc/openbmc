require nginx.inc

# 1.9.x is the current mainline branches containing all new features
# 1.8.x branch is the current stable branch, the recommended default
DEFAULT_PREFERENCE = "-1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0bb58ed0dfd4f5dbece3b52aba79f023"

SRC_URI[md5sum] = "a25818039f34b5d54b017d44c76321c4"
SRC_URI[sha256sum] = "2b4893076d28e6b4384bba8c4fdebfca6de6f8f68ec48a1ca94b9b855ff457d2"

DISABLE_STATIC = ""
