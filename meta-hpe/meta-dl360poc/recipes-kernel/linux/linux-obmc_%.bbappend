FILESEXTRAPATHS:prepend := "${THISDIR}/linux-obmc:"
SRC_URI += "file://gxp.dts \
            "

do_patch:append() {
  for DTB in "${KERNEL_DEVICETREE}"; do
      DT=`basename ${DTB} .dtb`
      if [ -r "${UNPACKDIR}/${DT}.dts" ]; then
          cp ${UNPACKDIR}/${DT}.dts \
              ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts
      fi
  done
 
}

