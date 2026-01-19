SRCBRANCH = "dev-6.6-axiado"
SRCREV = "ed31f66cc0583e74f94b1bb336a9d45f2f6d79a7"
KSRC = "git://github.com/axiado/linux-axiado;protocol=https;branch=${SRCBRANCH}"
SRC_URI += "file://ax3000-net-mod.dtsi \
            file://kernel.scc \
            file://kernel.cfg \
            "

LINUX_VERSION ?= "6.6.106"

KBUILD_DEFCONFIG ?= "ax3000_defconfig"

do_configure:append() {
    cp ${UNPACKDIR}/ax3000-net-mod.dtsi ${S}/arch/arm64/boot/dts/axiado/
}

require linux-axiado.inc
