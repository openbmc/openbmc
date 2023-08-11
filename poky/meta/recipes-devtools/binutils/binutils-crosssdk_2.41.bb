require binutils-cross_${PV}.bb

inherit crosssdk

PN = "binutils-crosssdk-${SDK_SYS}"

SRC_URI += "file://0001-binutils-crosssdk-Generate-relocatable-SDKs.patch"

do_configure:prepend () {
	sed -i 's#/usr/local/lib /lib /usr/lib#${SDKPATHNATIVE}/lib ${SDKPATHNATIVE}/usr/lib /usr/local/lib /lib /usr/lib#' ${S}/ld/configure.tgt
}
