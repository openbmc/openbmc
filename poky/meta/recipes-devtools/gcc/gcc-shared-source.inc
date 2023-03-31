do_fetch() {
	:
}
do_fetch[noexec] = "1"
deltask do_unpack
deltask do_patch

SRC_URI = ""

do_configure[depends] += "gcc-source-${PV}:do_preconfigure"
do_populate_lic[depends] += "gcc-source-${PV}:do_unpack"
do_deploy_source_date_epoch[depends] += "gcc-source-${PV}:do_deploy_source_date_epoch"

# Copy the SDE from the shared workdir to the recipe workdir
do_deploy_source_date_epoch () {
	sde_file=${SDE_FILE}
	sde_file=${sde_file#${WORKDIR}/}
	mkdir -p ${SDE_DEPLOYDIR} $(dirname ${SDE_FILE})
	cp -p $(dirname ${S})/$sde_file ${SDE_DEPLOYDIR}
	cp -p $(dirname ${S})/$sde_file ${SDE_FILE}
}
