SUMMARY = "a SocketCAN over Ethernet tunnel"
HOMEPAGE = "https://github.com/mguentner/cannelloni"
LICENSE = "GPL-2.0-only"

SRC_URI = "git://github.com/mguentner/cannelloni.git;protocol=https;branch=master \
           file://0001-include-bits-stdc-.h-only-when-using-libstdc.patch \
          "
SRCREV = "3d4fb8c8b07f6d7c62b2bdad7e5a94de61c9a29b"

LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "lksctp-tools"
PACKAGECONFIG[lksctp-tools] = "-DSCTP_SUPPORT=true, -DSCTP_SUPPORT=false, lksctp-tools"
