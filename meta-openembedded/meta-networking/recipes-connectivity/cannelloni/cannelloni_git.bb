SUMMARY = "a SocketCAN over Ethernet tunnel"
HOMEPAGE = "https://github.com/mguentner/cannelloni"
LICENSE = "GPLv2"

SRC_URI = "git://github.com/mguentner/cannelloni.git;protocol=https"
SRCREV = "44080bb021d1a143e6906f2ec4610513c4e1cece"

PV = "20160414+${SRCPV}"

LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "lksctp-tools"
PACKAGECONFIG[lksctp-tools] = "-DSCTP_SUPPORT=true, -DSCTP_SUPPORT=false, lksctp-tools"
