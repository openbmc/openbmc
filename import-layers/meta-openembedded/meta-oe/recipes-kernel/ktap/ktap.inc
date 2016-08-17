# Released under the MIT license (see COPYING.MIT for the terms)

HOMEPAGE = "http://www.ktap.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE-GPL;md5=eb723b61539feef013de476e68b5c50a"

PV = "0.4+gitr${SRCPV}"
SRCREV = "c8401e07c89cdec9a5f673da4ddb12b7982b522b"
SRC_URI = "git://github.com/ktap/ktap.git"

S = "${WORKDIR}/git"

# Package config is abused as a general compile time configuration tool.

# Only supports x86_64 for now!. Needs to be enabled for ktap-module too.
PACKAGECONFIG[ffi] = ""

# Needed to resolve symbols in DSO and for sdt
PACKAGECONFIG[libelf] = ",,elfutils"

PACKAGECONFIG ?= "libelf"

EXTRA_OEMAKE = " \
    ${@bb.utils.contains('PACKAGECONFIG', 'libelf', '', 'NO_LIBELF=1', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'ffi', 'FFI=1', '', d)} \
"
