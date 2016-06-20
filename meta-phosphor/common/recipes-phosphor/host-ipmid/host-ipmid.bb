SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

RRECOMMENDS_${PN} += "virtual/obmc-phosphor-host-ipmi-hw"

RRECOMMENDS_${PN} += "${VIRTUAL-RUNTIME_obmc-phosphor-ipmi-parsers}"


inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

TARGET_CFLAGS   += "-fpic"

RDEPENDS_${PN} += "clear-once"
RDEPENDS_${PN} += "settings"
RDEPENDS_${PN} += "network"
SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid \
            file://host-ipmid-whitelist.conf \
            file://ipmi-fru-parser-whitelist.conf \
            file://openpower-host-ipmi-oem-whitelist.conf"

SRCREV = "b7bcda57ee39616e8937194d281e2476e6ea8df2"

S = "${WORKDIR}/git"
INSTALL_NAME = "ipmid"

do_install() {
        install -m 0755 -d ${D}${libdir}/host-ipmid
        install -m 0755 ${S}/*.so ${D}${libdir}/host-ipmid/

        install -m 0755 -d ${D}${includedir}/host-ipmid
        install -m 0644 ${S}/ipmid-api.h ${D}${includedir}/host-ipmid/
}

def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.conf'):
            sources_list.append(s)

    return sources_list

do_configure () {
        ${S}/generate_whitelist.sh ${@" ".join(find_cfgs(d))}  > ipmiwhitelist.C
}
