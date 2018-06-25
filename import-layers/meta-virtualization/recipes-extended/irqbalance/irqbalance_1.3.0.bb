#
# Copyright (C) 2015 Wind River Systems, Inc.
#

require irqbalance.inc

SRC_URI[md5sum] = "53ee393adcfbc8e5ab23cbbd920df687"
SRC_URI[sha256sum] = "41c2c0842d8fb24240d8069b389cd8d1669625a40009a17ad886967845dc6e43"

SRC_URI = "https://github.com/Irqbalance/irqbalance/archive/v${PV}.tar.gz;downloadfilename=irqbalance-${PV}.tar.gz \
           file://add-initscript.patch \
           file://irqbalance-Add-status-and-reload-commands.patch \
           file://fix-configure-libcap-ng.patch \
           file://irqbalanced.service \
          "
