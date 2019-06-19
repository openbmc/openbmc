SUMMARY = "Small collection of benchmarks for storage I/O"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b529aaa6a0c50f15d29f89609b5c22f3"

SRCREV = "8d22ab8a4cc1484622c47ac9c5944fb9a61a5c0f"
PV = "3.4"
SRC_URI = "git://github.com/Algodev-github/S.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

# installing in /opt/S-suite since the package has
# dependencies to the directory structure.
do_install() {
    install -d ${D}/opt/S-suite
    for i in $(find ${S}/* -type d); do
        install -d ${D}/opt/S-suite/$(basename $i)
        install -m0755 -p ${S}/$(basename $i)/* ${D}/opt/S-suite/$(basename $i)
    done

    install -m0755 ${S}/def_config.sh ${D}/opt/S-suite
    install -m0755 ${S}/config_params.sh ${D}/opt/S-suite
    install -m0755 ${S}/process_config.sh ${D}/opt/S-suite
}

RDEPENDS_${PN} = "bash bc coreutils gawk g++ gcc fio libaio libaio-dev sysstat \
		  git"

FILES_${PN} = "/opt/S-suite/"

# added to INSANE_SKIP since s-suite have an runtime
# dependency (RDEPENDS) on libaio-dev.
INSANE_SKIP_${PN} += "dev-deps"
