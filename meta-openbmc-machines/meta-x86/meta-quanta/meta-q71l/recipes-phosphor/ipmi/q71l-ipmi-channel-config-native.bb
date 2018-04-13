SUMMARY = "Quanta Q71l IPMI Channel to if_name mapping."
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-ipmi-channel-config"

SRC_URI += "file://channel.yaml"

S = "${WORKDIR}"

do_install() {
	DEST=${D}${sensor_datadir}
        install -d ${DEST}
	install channel.yaml ${DEST}/channel.yaml
}
