SUMMARY = "libc and patchelf tarball for use with uninative.bbclass"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

TOOLCHAIN_TARGET_TASK = ""

TOOLCHAIN_HOST_TASK = "\
    nativesdk-glibc \
    nativesdk-glibc-gconv-ibm850 \
    nativesdk-patchelf \
    "

INHIBIT_DEFAULT_DEPS = "1"

TOOLCHAIN_OUTPUTNAME ?= "${BUILD_ARCH}-nativesdk-libc"

RDEPENDS = "${TOOLCHAIN_HOST_TASK}"

EXCLUDE_FROM_WORLD = "1"

inherit meta
inherit populate_sdk

deltask install
deltask package
deltask packagedata

SDK_DEPENDS += "patchelf-native"

SDK_PACKAGING_FUNC = ""

fakeroot create_sdk_files() {
	cp ${COREBASE}/scripts/relocate_sdk.py ${SDK_OUTPUT}/${SDKPATH}/

	# Replace the ##DEFAULT_INSTALL_DIR## with the correct pattern.
	# Escape special characters like '+' and '.' in the SDKPATH
	escaped_sdkpath=$(echo ${SDKPATH}/sysroots/${SDK_SYS} |sed -e "s:[\+\.]:\\\\\\\\\0:g")
	sed -i -e "s:##DEFAULT_INSTALL_DIR##:$escaped_sdkpath:" ${SDK_OUTPUT}/${SDKPATH}/relocate_sdk.py
}


fakeroot tar_sdk() {
	mkdir -p ${SDK_DEPLOY}
	cd ${SDK_OUTPUT}/${SDKPATH}
	mv sysroots/${SDK_SYS} ./${BUILD_SYS}
	rm sysroots -rf
	patchelf --set-interpreter ${@''.join('a' for n in xrange(1024))} ./${BUILD_SYS}/usr/bin/patchelf
	mv ./${BUILD_SYS}/usr/bin/patchelf ./${BUILD_SYS}/usr/bin/patchelf-uninative
	tar ${SDKTAROPTS} -c -j --file=${SDK_DEPLOY}/${TOOLCHAIN_OUTPUTNAME}.tar.bz2 .
}
