PR = "${INC_PR}.0"

MLX_IPMID_BRANCH = "openbmc"

SRC_URI = "git://github.com/mellanoxbmc/ipmi.git;protocol=git;branch=${MLX_IPMID_BRANCH}; \
           file://makefile.patch \
           file://makefile_glib.patch \
           file://makefile_cmdlang.patch \
           file://makefile_tcl.patch \
           file://mlx_ipmid.service \
"
SRC_URI[md5sum] = "9a4e1f6bb073379c494839201ea10aee"
SRC_URI[sha256sum] = "4988900043c35fcfa9b2bf275d6593904f6429221befb770ba6ecb5502108e55"

SRCREV = "${AUTOREV}"

require openipmi.inc
