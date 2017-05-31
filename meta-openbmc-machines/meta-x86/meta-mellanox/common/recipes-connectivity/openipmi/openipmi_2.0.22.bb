PR = "${INC_PR}.0"

SRC_URI = "git://github.com/mellanoxbmc/ipmi.git;protocol=git; \
           file://makefile.patch \
           file://makefile_glib.patch \
           file://makefile_cmdlang.patch \
           file://makefile_tcl.patch \
           file://mlx_ipmid.service \
"

SRCREV = "${AUTOREV}"

require openipmi.inc
