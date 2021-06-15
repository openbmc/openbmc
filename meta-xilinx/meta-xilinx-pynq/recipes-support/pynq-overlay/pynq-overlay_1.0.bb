SUMMARY = "Xilinx PYNQ overlay"
HOMEPAGE = "http://pynq.io"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=23;md5=04c57e41ad93d4d6b6ca3d766372b0fa"

DEPENDS += "dtc-native"

SRC_URI = " \
           file://pynq_zynqmp.dtsi \
           file://pynq_zynq.dtsi \
           file://pynq_zynqmp_symbols.dtsi \
           file://pynq_zynq_symbols.dtsi \
           file://generic-uio.conf \
           file://LICENSE \
	   "

S="${WORKDIR}"

PYNQ_DTSI ??= "pynq_zynqmp.dtsi"
PYNQ_DTSI_zynqmp ?= "pynq_zynqmp.dtsi"
PYNQ_DTSI_zynq ?= "pynq_zynq.dtsi"
PYNQ_SYMBOL_DTSI_zynqmp ?= "pynq_zynqmp_symbols.dtsi"
PYNQ_SYMBOL_DTSI_zynq ?= "pynq_zynq_symbols.dtsi"
PYNQ_DTBO ?= "pynq.dtbo"
PYNQ_SYMBOL_DTBO ?= "pynq-symbols.dtbo"

do_compile() {

	dtc -I dts -O dtb -@ ${WORKDIR}/${PYNQ_DTSI} -o ${S}/${PYNQ_DTBO}
	dtc -I dts -O dtb -@ ${WORKDIR}/${PYNQ_SYMBOL_DTSI} -o ${S}/${PYNQ_SYMBOL_DTBO}
}


do_install() {

	install -d ${D}/lib/firmware
	install -m 755 ${S}/${PYNQ_DTBO} ${D}/lib/firmware/${PYNQ_DTBO}
	install -m 755 ${S}/${PYNQ_SYMBOL_DTBO} ${D}/lib/firmware/${PYNQ_SYMBOL_DTBO}

	install -d ${D}/etc/modprobe.d
	install -m 644 ${WORKDIR}/generic-uio.conf ${D}${sysconfdir}/modprobe.d/generic-uio.conf

}

FILES_${PN} += "/lib/firmware/pynq.dtbo /lib/firmware/pynq-symbols.dtbo ${sysconfdir}/modprobe.d/generic-uio.conf"
