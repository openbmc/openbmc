#
# Copyright (C) 2015 Wind River Systems, Inc.
#

require irqbalance.inc

SRC_URI[md5sum] = "b15d975336080bcac4be0c1752d43cf3"
SRC_URI[sha256sum] = "91506e638b03bf27cf5da7dc250d58a753ce8a0288a20265fc7ff0266040706b"

SRC_URI = "https://github.com/Irqbalance/irqbalance/archive/v${PV}.tar.gz;downloadfilename=irqbalance-${PV}.tar.gz \
           file://add-initscript.patch \
           file://irqbalance-Add-status-and-reload-commands.patch \
           file://fix-configure-libcap-ng.patch \
           file://irqbalanced.service \
          "
