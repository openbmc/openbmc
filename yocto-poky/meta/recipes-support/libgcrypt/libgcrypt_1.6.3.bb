require libgcrypt.inc

SRC_URI += "\
        file://CVE-2015-7511_1.patch \
        file://CVE-2015-7511_2.patch \
        "
SRC_URI[md5sum] = "de03b867d02fdf115a1bac8bb8b5c3a3"
SRC_URI[sha256sum] = "69e94e1a7084d94e1a6ca26d436068cb74862d10a7353cfae579a2d88674ff09"
