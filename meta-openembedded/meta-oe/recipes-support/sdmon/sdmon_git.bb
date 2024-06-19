SUMMARY = "Get SD card health data"
HOMEPAGE = "https://github.com/Ognian/sdmon"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Ognian/sdmon;protocol=https;branch=master"

PV = "0.9.0"
SRCREV = "4dff9b690e8d4454fada6abfbb6b32fcb794968c"

S = "${WORKDIR}/git"

do_compile() {
	oe_runmake -C ${S}/src CC="${CC}" CFLAGS="${CFLAGS} -D_REENTRANT -DVERSION=\"\\\"${PV}\\\"\"" LDFLAGS="${LDFLAGS}"
}

do_install() {
	install -Dm 0755 ${S}/src/sdmon ${D}${bindir}/sdmon
}
