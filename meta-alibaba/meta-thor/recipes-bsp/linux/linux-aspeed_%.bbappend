FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
KERNEL_VERSION_SANITY_SKIP="1"
SRC_URI += "\
	file://aspeed-alibaba-thor.dts \
	file://alibaba.cfg \
	"

do_patch_append(){
	for DTB in "${KERNEL_DEVICETREE}"; do
		DT=`basename ${DTB} .dtb`
		if [ -r "${WORKDIR}/${DT}.dts" ]; then
			echo "debug: ${STAGING_KERNEL_DIR}"
			cp ${WORKDIR}/aspeed-alibaba-thor.dts \
				${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
		fi
	done
}
