SUMMARY = "CPU burn app that loads the NEON coprocessor fully"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://burn.S;md5=823abc72c2cd448e87df9bc5355a4456"

DL_DIR_append = "/${PN}-${PV}"

# Ensure to make this available for machine which has neon
COMPATIBLE_MACHINE = "(${@bb.utils.contains("TUNE_FEATURES", "neon", "${MACHINE}", "Invalid!", d)})"

SRC_URI = "http://hardwarebug.org/files/burn.S;name=mru \
           https://raw.githubusercontent.com/ssvb/cpuburn-arm/dd5c5ba58d2b0b23cfab4a286f9d3f5510000f20/cpuburn-a8.S;name=ssvb"

SRC_URI[mru.md5sum] = "823abc72c2cd448e87df9bc5355a4456"
SRC_URI[mru.sha256sum] = "01d9fc04f83740c513c25401dcc89c11b2a5a6013e70bfca42b7b02129f88cd2"
SRC_URI[ssvb.md5sum] = "ba0ef2939a3b3b487523448c67544e94"
SRC_URI[ssvb.sha256sum] = "ce42ebdc71c876a33d9f7534355ef76cefa0d00ddb19ad69cf05a266c861d08d"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} burn.S -o burn
    ${CC} ${CFLAGS} ${LDFLAGS} cpuburn-a8.S -o burn-neona8
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/burn ${D}${bindir}/burn-neon
    install -m 0755 ${S}/burn-neona8 ${D}${bindir}/
}

