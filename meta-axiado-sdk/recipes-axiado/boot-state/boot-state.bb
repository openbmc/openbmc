# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "Dual Boot State Management"
DESCRIPTION = "Service to mark boot as successful for dual boot system"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

inherit obmc-phosphor-systemd
RDEPENDS:${PN} = "bash libubootenv-bin"
