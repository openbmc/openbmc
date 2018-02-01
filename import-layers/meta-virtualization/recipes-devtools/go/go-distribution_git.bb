DESCRIPTION = "The Docker toolset to pack, ship, store, and deliver content"
HOMEPAGE = "https://github.com/docker/distribution"
SECTION = "devel/go"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${PKG_NAME}/LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRCNAME = "distribution"

PKG_NAME = "github.com/docker/${SRCNAME}"
SRC_URI = "git://${PKG_NAME}.git;branch=docker/1.13;destsuffix=git/src/${PKG_NAME}"

SRCREV = "28602af35aceda2f8d571bad7ca37a54cf0250bc"
PV = "2.6.0+git${SRCPV}"

S = "${WORKDIR}/git"

# NO-OP the do compile rule because this recipe is source only.
do_compile() {
}

do_install() {
	install -d ${D}${prefix}/local/go/src/${PKG_NAME}
	for j in $(cd ${S} && find src/${PKG_NAME} -name "*.go"); do
	    if [ ! -d ${D}${prefix}/local/go/$(dirname $j) ]; then
	        mkdir -p ${D}${prefix}/local/go/$(dirname $j)
	    fi
	    cp $j ${D}${prefix}/local/go/$j
	done
	cp -r ${S}/src/${PKG_NAME}/LICENSE ${D}${prefix}/local/go/src/${PKG_NAME}/
}

SYSROOT_PREPROCESS_FUNCS += "go_distribution_digeset_sysroot_preprocess"

go_distribution_digeset_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${prefix}/local/go/src/${PKG_NAME}
    cp -r ${D}${prefix}/local/go/src/${PKG_NAME} ${SYSROOT_DESTDIR}${prefix}/local/go/src/$(dirname ${PKG_NAME})
}

FILES_${PN} += "${prefix}/local/go/src/${PKG_NAME}/*"
