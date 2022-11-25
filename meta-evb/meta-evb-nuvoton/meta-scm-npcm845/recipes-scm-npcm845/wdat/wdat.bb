SUMMARY = "WDAT service"
DESCRIPTION = "WDAT service"
PR = "r1"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
DEPENDS = "sdbusplus \
           phosphor-logging \
           nlohmann-json"

FILESEXTRAPATHS:append := "${THISDIR}/files:"

inherit cmake pkgconfig systemd

S = "${WORKDIR}"
SRC_URI = "file://CMakeLists.txt \
		  file://wdat.cpp \
		  file://wdat@.service \
		  file://COPYING.MIT \
		  file://action.json \
		  "

FILES:${PN} += "/lib/systemd/system"
FILES:${PN} += " ${datadir}/wdat/action.json \"

do_install() {
    install -d ${D}${bindir}
    install -Dm755 ${S}/build/wdat ${D}/${bindir}/wdat
    install -d ${D}/lib/systemd/system
    install -Dm755 ${S}/wdat@.service ${D}/lib/systemd/system
    install -d ${D}${datadir}/wdat
    install -m 0644 -D ${WORKDIR}/action.json \
        ${D}${datadir}/wdat/action.json
}
