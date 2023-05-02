require nginx.inc

SRC_URI += "file://CVE-2021-3618.patch \
            file://CVE-2022-41741-CVE-2022-41742.patch \
           "

LIC_FILES_CHKSUM = "file://LICENSE;md5=206629dc7c7b3e87acb31162363ae505"

SRC_URI[md5sum] = "8ca6edd5076bdfad30a69c9c9b41cc68"
SRC_URI[sha256sum] = "e462e11533d5c30baa05df7652160ff5979591d291736cfa5edb9fd2edb48c49"

